package com.drdlx.cartooneye.startScreen

//import com.drdlx.cartooneye.common.helpers.SnackbarHelper

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.drdlx.cartooneye.common.helpers.CameraPermissionHelper
import com.drdlx.cartooneye.common.helpers.DisplayRotationHelper
import com.drdlx.cartooneye.common.helpers.FullScreenHelper
import com.drdlx.cartooneye.common.helpers.TrackingStateHelper
import com.drdlx.cartooneye.common.rendering.BackgroundRenderer
import com.drdlx.cartooneye.common.rendering.ObjectRenderer
import com.drdlx.cartooneye.mainScreens.mainScreen.view.MainScreen
import com.drdlx.cartooneye.navigation.AppNavigation
import com.drdlx.cartooneye.navigation.routeObjects.AppScreens
import com.drdlx.cartooneye.navigation.routeObjects.popRouteName
import com.drdlx.cartooneye.startScreen.model.AppRecordingState
import com.drdlx.cartooneye.utils.AugmentedFaceRenderer
import com.drdlx.cartooneye.utils.UtilsStorage
import com.google.ar.core.*
import com.google.ar.core.Config.AugmentedFaceMode
import com.google.ar.core.exceptions.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10



class MainActivity : ComponentActivity(), GLSurfaceView.Renderer {

    companion object {
        private const val launchEffectName = "Navigator"
        private const val TAG = "MainActivity"
    }

    // Rendering. The Renderers are created here, and initialized when the GL surface is created.
    private var surfaceView: GLSurfaceView? = null

    private var installRequested = false

    private var session: Session? = null
    private var displayRotationHelper: DisplayRotationHelper? = null
    private val trackingStateHelper: TrackingStateHelper = TrackingStateHelper(this)

    private val backgroundRenderer: BackgroundRenderer = BackgroundRenderer()
    private val augmentedFaceRenderer: AugmentedFaceRenderer = AugmentedFaceRenderer()
    private val noseObject: ObjectRenderer = ObjectRenderer()
    private val rightEarObject: ObjectRenderer = ObjectRenderer()
    private val leftEarObject: ObjectRenderer = ObjectRenderer()

    // Temporary matrix allocated here to reduce number of allocations for each frame.
    private val noseMatrix = FloatArray(16)
    private val rightEarMatrix = FloatArray(16)
    private val leftEarMatrix = FloatArray(16)
    private val DEFAULT_COLOR = floatArrayOf(0f, 0f, 0f, 0f)

    private var appRecordingState: AppRecordingState = AppRecordingState.IDLE

    private val navigator: AppNavigation by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        displayRotationHelper = DisplayRotationHelper(this)

        surfaceView = GLSurfaceView(this).also { surfaceView ->
            surfaceView.preserveEGLContextOnPause = true
            surfaceView.setEGLContextClientVersion(2)
            surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0) // Alpha used for plane blending.
            surfaceView.setRenderer(this)
            surfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            surfaceView.setWillNotDraw(false)

            surfaceView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        setContent {

            val navigationController = rememberNavController()
            LaunchedEffect(launchEffectName) {
                navigator.navRoute.onEach {
                    when (it.route) {
                        popRouteName -> {
                            when (it.popTargetRoute.isEmpty()) {
                                true -> navigationController.popBackStack()
                                false -> navigationController.popBackStack(
                                    it.popTargetRoute,
                                    it.inclusive,
                                    it.saveState
                                )
                            }
                        }
                        else -> navigationController.navigate(it.route, it.options)
                    }
                }.launchIn(this)
            }

            NavHost(
                navController = navigationController,
                startDestination = AppScreens.CameraScreen.route
            ) {
                composable(route = AppScreens.CameraScreen.route) {
                    MainScreen(
                        surfaceView = surfaceView,
                        restartActivityCallback = { restartActivity() },
                        recordingVideoCallback = { onClickRecord() }
                    )
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()

        if (session == null) {
            var exception: Exception? = null
            var message: String? = null
            try {
                when (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
                    ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                        installRequested = true
                        return
                    }
                    ArCoreApk.InstallStatus.INSTALLED -> {}
                }

                // ARCore requires camera permissions to operate. If we did not yet obtain runtime
                // permission on Android M and above, now is a good time to ask the user for it.
                if (!CameraPermissionHelper.hasCameraPermission(this)) {
                    CameraPermissionHelper.requestCameraPermission(this)
                    return
                }

                // Create the session and configure it to use a front-facing (selfie) camera.
                session = Session(this, EnumSet.of(Session.Feature.FRONT_CAMERA))

                val cameraConfigFilter = CameraConfigFilter(session)
                cameraConfigFilter.facingDirection = CameraConfig.FacingDirection.FRONT
                val cameraConfigs: List<CameraConfig> =
                    session!!.getSupportedCameraConfigs(cameraConfigFilter)
                if (!cameraConfigs.isEmpty()) {
                    // Element 0 contains the camera config that best matches the session feature
                    // and filter settings.
                    session!!.cameraConfig = cameraConfigs[0]
                } else {
                    message = "This device does not have a front-facing (selfie) camera"
                    exception = UnavailableDeviceNotCompatibleException(message)
                }

                configureSession()

            } catch (e: UnavailableArcoreNotInstalledException) {
                message = "Please install ARCore"
                exception = e
            } catch (e: UnavailableUserDeclinedInstallationException) {
                message = "Please install ARCore"
                exception = e
            } catch (e: UnavailableApkTooOldException) {
                message = "Please update ARCore"
                exception = e
            } catch (e: UnavailableSdkTooOldException) {
                message = "Please update this app"
                exception = e
            } catch (e: UnavailableDeviceNotCompatibleException) {
                message = "This device does not support AR"
                exception = e
            } catch (e: Exception) {
                message = "Failed to create AR session"
                exception = e
            }
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                Log.e(
                    TAG,
                    "Exception creating session",
                    exception
                )
                return
            }
        }

        // Note that order matters - see the note in onPause(), the reverse applies here.
        try {
            session!!.resume()
        } catch (e: CameraNotAvailableException) {
            Toast.makeText(this, "Camera not available. Try restarting the app.", Toast.LENGTH_LONG)
                .show()
            session = null
            return
        }

        surfaceView!!.onResume()
        displayRotationHelper!!.onResume()
    }

    override fun onPause() {
        super.onPause()

        if (session != null) {
            // Note that the order matters - GLSurfaceView is paused first so that it does not try
            // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
            // still call session.update() and get a SessionPausedException.
            displayRotationHelper!!.onPause()
            surfaceView!!.onPause()
            session!!.pause()
        }
    }

    override fun onDestroy() {
        if (session != null) {
            // Explicitly close ARCore Session to release native resources.
            // Review the API reference for important considerations before calling close() in apps with
            // more complicated lifecycle requirements:
            // https://developers.google.com/ar/reference/java/arcore/reference/com/google/ar/core/Session#close()
            session!!.close()
            session = null
        }
        super.onDestroy()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)

        // Prepare the rendering objects. This involves reading shaders, so may throw an IOException.
        try {
            // Create the texture and pass it to ARCore session to be filled during update().
            backgroundRenderer.createOnGlThread( /*context=*/this)
            augmentedFaceRenderer.createOnGlThread(this, "models/freckles.png")
            augmentedFaceRenderer.setMaterialProperties(0.0f, 1.0f, 0.1f, 6.0f)
            noseObject.createOnGlThread( /*context=*/this, "models/nose.obj", "models/nose_fur.png")
            noseObject.setMaterialProperties(0.0f, 1.0f, 0.1f, 6.0f)
            noseObject.setBlendMode(ObjectRenderer.BlendMode.AlphaBlending)
            rightEarObject.createOnGlThread(this, "models/forehead_right.obj", "models/ear_fur.png")
            rightEarObject.setMaterialProperties(0.0f, 1.0f, 0.1f, 6.0f)
            rightEarObject.setBlendMode(ObjectRenderer.BlendMode.AlphaBlending)
            leftEarObject.createOnGlThread(this, "models/forehead_left.obj", "models/ear_fur.png")
            leftEarObject.setMaterialProperties(0.0f, 1.0f, 0.1f, 6.0f)
            leftEarObject.setBlendMode(ObjectRenderer.BlendMode.AlphaBlending)
        } catch (e: IOException) {
            Log.e(TAG, "Failed to read an asset file", e)
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        displayRotationHelper!!.onSurfaceChanged(width, height)
        GLES20.glViewport(0, 0, width, height)
        UtilsStorage.setDimensions(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        // Clear screen to notify driver it should not load any pixels from previous frame.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        if (session == null) {
            return
        }
        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        displayRotationHelper!!.updateSessionIfNeeded(session!!)
        try {
            session!!.setCameraTextureName(backgroundRenderer.textureId)

            // Obtain the current frame from ARSession. When the configuration is set to
            // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
            // camera framerate.
            val frame = session!!.update()
            val camera = frame.camera

            // Get projection matrix.
            val projectionMatrix = FloatArray(16)
            camera.getProjectionMatrix(projectionMatrix, 0, 0.1f, 100.0f)

            // Get camera matrix and draw.
            val viewMatrix = FloatArray(16)
            camera.getViewMatrix(viewMatrix, 0)

            // Compute lighting from average intensity of the image.
            // The first three components are color scaling factors.
            // The last one is the average pixel intensity in gamma space.
            val colorCorrectionRgba = FloatArray(4)
            frame.lightEstimate.getColorCorrection(colorCorrectionRgba, 0)

            // If frame is ready, render camera preview image to the GL surface.
            backgroundRenderer.draw(frame)

            // Keep the screen unlocked while tracking, but allow it to lock when tracking stops.
            trackingStateHelper.updateKeepScreenOnFlag(camera.trackingState)

            // ARCore's face detection works best on upright faces, relative to gravity.
            // If the device cannot determine a screen side aligned with gravity, face
            // detection may not work optimally.
            val faces = session!!.getAllTrackables(
                AugmentedFace::class.java
            )
            for (face in faces) {
                if (face.trackingState != TrackingState.TRACKING) {
                    break
                }
                val scaleFactor = 1.0f
                // Face objects use transparency so they must be rendered back to front without depth write.
                GLES20.glDepthMask(false)

                // Each face's region poses, mesh vertices, and mesh normals are updated every frame.

                // 1. Render the face mesh first, behind any 3D objects attached to the face regions.
                val modelMatrix = FloatArray(16)
                face.centerPose.toMatrix(modelMatrix, 0)
                augmentedFaceRenderer.draw(
                    projectionMatrix, viewMatrix, modelMatrix, colorCorrectionRgba, face
                )

                // 2. Next, render the 3D objects attached to the forehead.
                face.getRegionPose(AugmentedFace.RegionType.FOREHEAD_RIGHT)
                    .toMatrix(rightEarMatrix, 0)
                rightEarObject.updateModelMatrix(rightEarMatrix, scaleFactor)
                rightEarObject.draw(
                    viewMatrix,
                    projectionMatrix,
                    colorCorrectionRgba,
                    DEFAULT_COLOR
                )
                face.getRegionPose(AugmentedFace.RegionType.FOREHEAD_LEFT)
                    .toMatrix(leftEarMatrix, 0)
                leftEarObject.updateModelMatrix(leftEarMatrix, scaleFactor)
                leftEarObject.draw(
                    viewMatrix,
                    projectionMatrix,
                    colorCorrectionRgba,
                    DEFAULT_COLOR
                )

                // 3. Render the nose last so that it is not occluded by face mesh or by 3D objects attached
                // to the forehead regions.
                face.getRegionPose(AugmentedFace.RegionType.NOSE_TIP).toMatrix(noseMatrix, 0)
                noseObject.updateModelMatrix(noseMatrix, scaleFactor)
                noseObject.draw(
                    viewMatrix,
                    projectionMatrix,
                    colorCorrectionRgba,
                    DEFAULT_COLOR
                )
            }
        } catch (t: Throwable) {
            // Avoid crashing the application due to unhandled exceptions.
            Log.e(TAG, "Exception on the OpenGL thread", t)
        } finally {
            GLES20.glDepthMask(true)
        }
    }

    private fun configureSession() {
        Log.i(TAG, "Config session!")
        val config = Config(session)
        config.augmentedFaceMode = AugmentedFaceMode.MESH3D
        session!!.configure(config)
    }

    private fun restartActivity() {
        this.recreate()
    }

    private fun pauseARCoreSession() {
        // Pause the GLSurfaceView so that it doesn't update the ARCore session.
        // Pause the ARCore session so that we can update its configuration.
        // If the GLSurfaceView is not paused,
        //   onDrawFrame() will try to update the ARCore session
        //   while it's paused, resulting in a crash.
        surfaceView!!.onPause()
        session!!.pause()
    }

    private fun resumeARCoreSession(): Boolean {
        // We must resume the ARCore session before the GLSurfaceView.
        // Otherwise, the GLSurfaceView will try to update the ARCore session.
        try {
            session!!.resume()
        } catch (e: CameraNotAvailableException) {
            Log.e(TAG, "CameraNotAvailableException in resumeARCoreSession", e)
            return false
        }
        surfaceView!!.onResume()
        return true
    }

    private fun startRecording(): Boolean {
        val mp4FileUri: Uri = createMp4File() ?: return false
        Log.d(TAG, "startRecording at: $mp4FileUri")
        pauseARCoreSession()

        // Configure the ARCore session to start recording.
        val recordingConfig = RecordingConfig(session)
            .setMp4DatasetUri(mp4FileUri)
            .setAutoStopOnPause(true)
        try {
            // Prepare the session for recording, but do not start recording yet.
            session!!.startRecording(recordingConfig)
        } catch (e: RecordingFailedException) {
            Log.e(TAG, "startRecording - Failed to prepare to start recording", e)
            return false
        }
        val canResume: Boolean = resumeARCoreSession()
        if (!canResume) return false

        // Correctness checking: check the ARCore session's RecordingState.
        val recordingStatus = session!!.recordingStatus
        Log.d(TAG, String.format("startRecording - recordingStatus %s", recordingStatus))
        return recordingStatus == RecordingStatus.OK
    }

    private fun stopRecording(): Boolean {
        try {
            session!!.stopRecording()
        } catch (e: RecordingFailedException) {
            Log.e(TAG, "stopRecording - Failed to stop recording", e)
            return false
        }

        // Correctness checking: check if the session stopped recording.
        return session!!.recordingStatus == RecordingStatus.NONE
    }

    private fun onClickRecord() {
        Log.d(TAG, "onClickRecord")
        when (UtilsStorage.appRecordingState.value) {
            AppRecordingState.IDLE -> {
                val hasStarted = startRecording()
                Log.d(TAG, String.format("onClickRecord start: hasStarted %b", hasStarted))
                if (hasStarted) UtilsStorage.changeAppRecordingState(AppRecordingState.RECORDING)
            }
            AppRecordingState.RECORDING -> {
                val hasStopped = stopRecording()
                Log.d(TAG, String.format("onClickRecord stop: hasStopped %b", hasStopped))
                if (hasStopped) UtilsStorage.changeAppRecordingState(AppRecordingState.IDLE)
            }
            else -> {}
        }
//        updateRecordButton()
    }

    private val REQUEST_WRITE_EXTERNAL_STORAGE = 1

    private fun checkAndRequestStoragePermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_EXTERNAL_STORAGE
            )
            return false
        }
        return true
    }

    private val MP4_VIDEO_MIME_TYPE = "video/mp4"

    private fun createMp4File(): Uri? {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss")
        val mp4FileName = "arcore-" + dateFormat.format(Date()).toString() + ".mp4"
        val resolver = this.contentResolver
        var videoCollection: Uri? = null
        videoCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        // Create a new Media file record.
        val newMp4FileDetails = ContentValues()
        newMp4FileDetails.put(MediaStore.Video.Media.DISPLAY_NAME, mp4FileName)
        newMp4FileDetails.put(MediaStore.Video.Media.MIME_TYPE, MP4_VIDEO_MIME_TYPE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // The Relative_Path column is only available since API Level 29.
            newMp4FileDetails.put(
                MediaStore.Video.Media.RELATIVE_PATH,
                Environment.DIRECTORY_MOVIES
            )
        } else {
            // Use the Data column to set path for API Level <= 28.
            val mp4FileDir: File =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
            val absoluteMp4FilePath = File(mp4FileDir, mp4FileName).absolutePath
            newMp4FileDetails.put(MediaStore.Video.Media.DATA, absoluteMp4FilePath)
        }
        val newMp4FileUri = resolver.insert(videoCollection, newMp4FileDetails)

        // Ensure that this file exists and can be written.
        if (newMp4FileUri == null) {
            Log.e(
                TAG,
                String.format(
                    "Failed to insert Video entity in MediaStore. API Level = %d",
                    Build.VERSION.SDK_INT
                )
            )
            return null
        }

        // This call ensures the file exist before we pass it to the ARCore API.
        if (!testFileWriteAccess(newMp4FileUri)) {
            return null
        }
        Log.d(
            TAG,
            String.format(
                "createMp4File = %s, API Level = %d",
                newMp4FileUri,
                Build.VERSION.SDK_INT
            )
        )
        return newMp4FileUri
    }

    // Test if the file represented by the content Uri can be open with write access.
    private fun testFileWriteAccess(contentUri: Uri): Boolean {
        try {
            this.contentResolver.openOutputStream(contentUri).use { mp4File ->
                Log.d(
                    TAG,
                    String.format("Success in testFileWriteAccess %s", contentUri.toString())
                )
                return true
            }
        } catch (e: FileNotFoundException) {
            Log.e(
                TAG,
                String.format(
                    "FileNotFoundException in testFileWriteAccess %s",
                    contentUri.toString()
                ),
                e
            )
        } catch (e: IOException) {
            Log.e(
                TAG,
                String.format("IOException in testFileWriteAccess %s", contentUri.toString()),
                e
            )
        }
        return false
    }

}

