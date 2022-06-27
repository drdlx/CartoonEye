package com.drdlx.cartooneye.startScreen

//import com.drdlx.cartooneye.common.helpers.SnackbarHelper

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.drdlx.cartooneye.mainScreens.mainScreen.view.MainScreen
import com.drdlx.cartooneye.navigation.AppNavigation
import com.drdlx.cartooneye.navigation.routeObjects.AppScreens
import com.drdlx.cartooneye.navigation.routeObjects.popRouteName
import com.google.ar.core.*
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.Texture
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.ArFrontFacingFragment
import com.google.ar.sceneform.ux.AugmentedFaceNode
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.function.Function


class MainActivity : FragmentActivity() {

    companion object {
        private const val launchEffectName = "Navigator"
        private const val TAG = "MainActivity"
    }


    private val loaders: MutableSet<CompletableFuture<*>> = HashSet()
    private var arSceneView: ArSceneView? = null
    private var faceTexture: Texture? = null
    private var faceModel: ModelRenderable? = null
    private val facesNodes: HashMap<AugmentedFace, AugmentedFaceNode> = HashMap()
    private var arFragment: ArFrontFacingFragment? = null

    private val navigator: AppNavigation by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadModels()
        loadTextures()

        val fragmentManager = this.supportFragmentManager

        this.arFragment = ArFrontFacingFragment().also {
            it.setOnViewCreatedListener(this::onViewCreated)
            it.setOnAugmentedFaceUpdateListener(this::onAugmentedFaceTrackingUpdate)
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
//                        surfaceView = surfaceView,
                        restartActivityCallback = { restartActivity() },
                        recordingVideoCallback = { /*onClickRecord()*/ },
                        supportFragmentManager = fragmentManager,
                        arFragment = arFragment,
                    )
                }
            }

        }
    }

    private fun onViewCreated(arSceneView: ArSceneView) {
        this.arSceneView = arSceneView

        // This is important to make sure that the camera stream renders first so that
        // the face mesh occlusion works correctly.
        arSceneView.setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST)

        // Check for face detections
        arFragment?.setOnAugmentedFaceUpdateListener(::onAugmentedFaceTrackingUpdate)
    }

    override fun onDestroy() {

        super.onDestroy()
        for (loader in loaders) {
            if (!loader.isDone) {
                loader.cancel(true)
            }
        }
    }

    private fun restartActivity() {
        this.recreate()
    }

    private fun loadModels() {
        loaders.add(ModelRenderable.builder()
            .setSource(this, Uri.parse("models/fox.glb"))
            .setIsFilamentGltf(true)
            .build()
            .thenAccept { model: ModelRenderable ->
                faceModel = model
            }
            .exceptionally(Function<Throwable, Void?> { throwable: Throwable? ->
                Toast.makeText(this, "Unable to load renderable", Toast.LENGTH_LONG).show()
                null
            })
        )
    }

    private fun loadTextures() {
        loaders.add(
            Texture.builder()
                .setSource(this, Uri.parse("textures/freckles.png"))
                .setUsage(Texture.Usage.COLOR_MAP)
                .build()
                .thenAccept(Consumer { texture: Texture ->
                    faceTexture = texture
                })
                .exceptionally(Function<Throwable, Void?> { throwable: Throwable? ->
                    Toast.makeText(this, "Unable to load texture", Toast.LENGTH_LONG).show()
                    null
                })
        )
    }

    private fun onAugmentedFaceTrackingUpdate(augmentedFace: AugmentedFace) {
        if (faceModel == null || faceTexture == null) {
            return
        }
        val existingFaceNode = facesNodes[augmentedFace]
        when (augmentedFace.trackingState) {
            TrackingState.TRACKING -> if (existingFaceNode == null) {
                val faceNode = AugmentedFaceNode(augmentedFace)
                val modelInstance = faceNode.setFaceRegionsRenderable(faceModel)
                modelInstance.isShadowCaster = false
                modelInstance.isShadowReceiver = true
                faceNode.faceMeshTexture = faceTexture
                arSceneView!!.scene.addChild(faceNode)
                facesNodes[augmentedFace] = faceNode
            }
            TrackingState.STOPPED -> {
                if (existingFaceNode != null) {
                    arSceneView!!.scene.removeChild(existingFaceNode)
                }
                facesNodes.remove(augmentedFace)
            }
            else -> {Log.d(TAG, "Tracking paused")}
        }
    }

}
