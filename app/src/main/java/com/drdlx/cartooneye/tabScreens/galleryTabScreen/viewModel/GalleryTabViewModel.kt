package com.drdlx.cartooneye.tabScreens.galleryTabScreen.viewModel

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdlx.cartooneye.services.files.FilesIOService
import com.drdlx.cartooneye.tabScreens.galleryTabScreen.model.GalleryTabUiState
import com.drdlx.cartooneye.utils.EMPTY_IMAGE_URI
import com.drdlx.cartooneye.utils.makeTemporaryPicture
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import java.io.IOException


@KoinViewModel
class GalleryTabViewModel(
    private val filesIOService: FilesIOService,
) : ViewModel() {

    companion object {
        private const val TAG = "GalleryTabViewModel"
        private const val FACE_POSITION_RADIUS = 8.0f
        private const val ID_TEXT_SIZE = 30.0f
        private const val ID_Y_OFFSET = 40.0f
        private const val BOX_STROKE_WIDTH = 5.0f
        private const val NUM_COLORS = 10
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
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
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


                image.bitmapInternal?.let { canvas.drawBitmap(it, Matrix(), null) }

                detector.process(image)
                    .addOnSuccessListener { faces ->
                        // Task completed successfully
                        viewModelScope.launch {
                            for (face in faces) {
                                val bounds = face.boundingBox

                                val faceContour = face.getContour(FaceContour.FACE)
                                if (faceContour != null) {
                                    val leftEarBottomPointFace = faceContour.points[31]
                                    val leftEarTopPointFace = faceContour.points[34]
                                    val leftEarRect = RectF(
                                        leftEarBottomPointFace.x,
                                        bounds.top.toFloat(),
                                        leftEarTopPointFace.x,
                                        leftEarBottomPointFace.y,
                                    )
                                    val leftEarFinalRect = RectF(
                                        leftEarRect.left - leftEarRect.width(),
                                        leftEarRect.top - leftEarRect.height(),
                                        leftEarRect.right,
                                        leftEarRect.bottom
                                    )
                                    val leftEarTexture =
                                        BitmapFactory.decodeStream(context.assets.open("2d-assets/left-ear.png"))
                                    canvas.drawBitmap(leftEarTexture, null, leftEarFinalRect, null)

                                    val rightEarBottomPointFace = faceContour.points[2]
                                    val rightEarRect = RectF(
                                        rightEarBottomPointFace.x,
                                        bounds.top.toFloat(),
                                        bounds.right.toFloat(),
                                        leftEarBottomPointFace.y,
                                    )
                                    val rightEarFinalRect = RectF(
                                        rightEarRect.left,
                                        rightEarRect.top - rightEarRect.height(),
                                        rightEarRect.right + rightEarRect.width(),
                                        rightEarRect.bottom
                                    )
                                    val rightEarTexture =
                                        BitmapFactory.decodeStream(context.assets.open("2d-assets/right-ear.png"))
                                    canvas.drawBitmap(
                                        rightEarTexture,
                                        null,
                                        rightEarFinalRect,
                                        null
                                    )

                                    val noseBottom = face.getContour(FaceContour.NOSE_BOTTOM)
                                    val noseBridge = face.getContour(FaceContour.NOSE_BRIDGE)
                                    if (noseBottom != null && noseBridge != null) {
                                        val noseTexture =
                                            BitmapFactory.decodeStream(context.assets.open("2d-assets/nose.png"))
                                        val noseRect = RectF(
                                            noseBottom.points.first().x,
                                            noseBridge.points.first().y,
                                            (noseBottom.points.last().x),
                                            (noseBottom.points.last().y)
                                        )
                                        canvas.drawBitmap(noseTexture, null, noseRect, null)
                                    }
                                }
                            }
                            changeCurrentPicture(makeTemporaryPicture(tempBitmap).toUri())
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