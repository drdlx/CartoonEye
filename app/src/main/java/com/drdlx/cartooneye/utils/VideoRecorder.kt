package com.drdlx.cartooneye.utils

import android.content.res.Configuration
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import android.util.Size
import android.view.Surface
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.SceneView
import java.io.File
import java.io.IOException

/**
 * Video Recorder class handles recording the contents of a SceneView. It uses MediaRecorder to
 * encode the video. The quality settings can be set explicitly or simply use the CamcorderProfile
 * class to select a predefined set of parameters.
 */
class VideoRecorder {
    // recordingVideoFlag is true when the media recorder is capturing video.
    var isRecording = false
        private set
    private var mediaRecorder: MediaRecorder? = null
    private var videoSize: Size? = null
    private var sceneView: SceneView? = null
    private var videoCodec = 0
    private var videoDirectory: File? = null
    private var videoBaseName: String? = null
    var videoPath: File? = null
        private set
    private var bitRate = DEFAULT_BITRATE
    private var frameRate = DEFAULT_FRAMERATE
    private var encoderSurface: Surface? = null
    fun setBitRate(bitRate: Int) {
        this.bitRate = bitRate
    }

    fun setFrameRate(frameRate: Int) {
        this.frameRate = frameRate
    }

    fun setSceneView(sceneView: SceneView?) {
        this.sceneView = sceneView
    }

    /**
     * Toggles the state of video recording.
     *
     * @return true if recording is now active.
     */
    fun onToggleRecord(view: ArSceneView): Boolean {
        if (isRecording) {
            stopRecordingVideo(view)
        } else {
            startRecordingVideo(view)
        }
        return isRecording
    }

    private fun startRecordingVideo(view: ArSceneView) {
        if (mediaRecorder == null) {
            mediaRecorder = MediaRecorder()
        }
        try {
            buildFilename()
            setUpMediaRecorder()
        } catch (e: IOException) {
            Log.e(TAG, "Exception setting up recorder", e)
            return
        }

        // Set up Surface for the MediaRecorder
        encoderSurface = mediaRecorder!!.surface
        view.startMirroringToSurface(
            encoderSurface, 0, 0, videoSize!!.width, videoSize!!.height
        )
        isRecording = true
    }

    private fun buildFilename() {
        if (videoDirectory == null) {
            videoDirectory = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString() + "/Sceneform"
            )
        }
        if (videoBaseName == null || videoBaseName!!.isEmpty()) {
            videoBaseName = "Sample"
        }
        videoPath = File(
            videoDirectory,
            videoBaseName + java.lang.Long.toHexString(System.currentTimeMillis()) + ".mp4"
        )
        val dir = videoPath!!.parentFile
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }

    private fun stopRecordingVideo(sceneView: ArSceneView?) {
        // UI
        isRecording = false
        if (encoderSurface != null) {
            sceneView!!.stopMirroringToSurface(encoderSurface)
            encoderSurface = null
        }
        // Stop recording
        mediaRecorder!!.stop()
        mediaRecorder!!.reset()
    }

    @Throws(IOException::class)
    private fun setUpMediaRecorder() {
        mediaRecorder!!.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder!!.setOutputFile(videoPath!!.absolutePath)
        mediaRecorder!!.setVideoEncodingBitRate(bitRate)
        mediaRecorder!!.setVideoFrameRate(frameRate)
        mediaRecorder!!.setVideoSize(videoSize!!.width, videoSize!!.height)
        mediaRecorder!!.setVideoEncoder(videoCodec)
        mediaRecorder!!.prepare()
        try {
            mediaRecorder!!.start()
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Exception starting capture: " + e.message, e)
        }
    }

    fun setVideoSize(width: Int, height: Int) {
        videoSize = Size(width, height)
    }

    fun setVideoQuality(quality: Int, orientation: Int) {
        var profile: CamcorderProfile? = null
        if (CamcorderProfile.hasProfile(quality)) {
            profile = CamcorderProfile.get(quality)
        }
        if (profile == null) {
            // Select a quality  that is available on this device.
            for (level in FALLBACK_QUALITY_LEVELS) {
                if (CamcorderProfile.hasProfile(level)) {
                    profile = CamcorderProfile.get(level)
                    break
                }
            }
        }
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setVideoSize(profile!!.videoFrameWidth, profile.videoFrameHeight)
        } else {
            setVideoSize(profile!!.videoFrameHeight, profile.videoFrameWidth)
        }
        setVideoCodec(profile.videoCodec)
        setBitRate(profile.videoBitRate)
        setFrameRate(profile.videoFrameRate)
    }

    fun setVideoCodec(videoCodec: Int) {
        this.videoCodec = videoCodec
    }

    companion object {
        private const val TAG = "VideoRecorder"
        private const val DEFAULT_BITRATE = 10000000
        private const val DEFAULT_FRAMERATE = 30
        private val FALLBACK_QUALITY_LEVELS = intArrayOf(
            CamcorderProfile.QUALITY_HIGH,
            CamcorderProfile.QUALITY_2160P,
            CamcorderProfile.QUALITY_1080P,
           CamcorderProfile.QUALITY_720P,
            CamcorderProfile.QUALITY_480P
        )
    }
}