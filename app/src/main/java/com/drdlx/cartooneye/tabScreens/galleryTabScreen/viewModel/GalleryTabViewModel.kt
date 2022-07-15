package com.drdlx.cartooneye.tabScreens.galleryTabScreen.viewModel

import android.R
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.SparseArray
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdlx.cartooneye.services.files.FilesIOService
import com.drdlx.cartooneye.tabScreens.galleryTabScreen.model.GalleryTabUiState
import com.drdlx.cartooneye.utils.EMPTY_IMAGE_URI
import com.drdlx.cartooneye.utils.makeTemporaryPicture
import com.google.ar.core.Frame
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import java.io.IOException


@KoinViewModel
class GalleryTabViewModel(
    private val filesIOService: FilesIOService,
) : ViewModel() {

    companion object {
        private const val TAG = "GalleryTabViewModel"
    }

    private val currentPictureUri = MutableLiveData(EMPTY_IMAGE_URI)

    val uiState = GalleryTabUiState(
        currentPictureUri = currentPictureUri
    )

    fun changeCurrentPicture(uri: Uri) {
        currentPictureUri.value = uri
    }

    fun processImage(context: Context) {
        val uri = currentPictureUri.value

        if (uri != null && uri != EMPTY_IMAGE_URI) {

            val highAccuracyOpts = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build()

            val image: InputImage
            try {
                image = InputImage.fromFilePath(context, uri)
                val detector = FaceDetection.getClient(highAccuracyOpts)

                val paint = Paint()
                paint.strokeWidth = 6f
                paint.color = Color.RED
                paint.style = Paint.Style.STROKE

                val tempBitmap =
                    Bitmap.createBitmap(image.width, image.height, Bitmap.Config.RGB_565)
                val canvas = Canvas(tempBitmap)

                image.bitmapInternal?.let { canvas.drawBitmap(it, 0f, 0f, null) }

                detector.process(image)
                    .addOnSuccessListener { faces ->
                        // Task completed successfully
                        viewModelScope.launch {
                        for (face in faces) {
                            val bounds = face.boundingBox
                            val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
                            val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees
                            println("Success")
                            println("bounds: $bounds")
                            println("rotY: $rotY")
                            println("rotZ: $rotZ")
                            canvas.drawRect(bounds, paint)

                            // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                            // nose available):
                            val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
                            leftEar?.let {
                                val leftEarPos = leftEar.position
                                println ("leftEarPos: $leftEarPos")
                            }

                            // If contour detection was enabled:
                            val leftEyeContour = face.getContour(FaceContour.LEFT_EYE)?.points
                            val upperLipBottomContour = face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points

                            println("leftEyeContour: $leftEyeContour")
                            println("upperLipBottomContour: $upperLipBottomContour")

                            // If classification was enabled:
                            if (face.smilingProbability != null) {
                                val smileProb = face.smilingProbability
                                println("smileProb: $smileProb")
                            }
                            if (face.rightEyeOpenProbability != null) {
                                val rightEyeOpenProb = face.rightEyeOpenProbability
                                println("rightEyeOpenProb: $rightEyeOpenProb")
                            }

                            // If face tracking was enabled:
                            if (face.trackingId != null) {
                                val id = face.trackingId
                                println("trackingId: $id")
                            }


                        }

                        val tempFile = makeTemporaryPicture(tempBitmap)
                        changeCurrentPicture(tempFile.toUri())

                        }

                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Face detection failed $e")
                        Toast.makeText(context, "Face detection failed $e", Toast.LENGTH_SHORT).show()
                    }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun saveCurrentPicture(uri: Uri, context: Context) {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
        filesIOService.savePhoto(bitmap, context.contentResolver)
    }

}