package com.drdlx.cartooneye.tabScreens.galleryTabScreen.viewModel

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.graphics.toRectF
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

                val textureBitmap = BitmapFactory.decodeStream(context.assets.open("2d-assets/nose.png"))
//                textureBitmap.config = Bitmap.Config.ARGB_8888

                image.bitmapInternal?.let { canvas.drawBitmap(it, Matrix(), null) }

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

                            val rightEar = face.getLandmark(FaceLandmark.RIGHT_EAR)

                            val leftCheek = face.getLandmark(FaceLandmark.LEFT_CHEEK)
                            val rightCheek = face.getLandmark(FaceLandmark.RIGHT_CHEEK)
                            val nose = face.getLandmark(FaceLandmark.NOSE_BASE)

                            canvas.drawCircle(face.boundingBox.exactCenterX(), face.boundingBox.exactCenterY(), (bounds.width() / 2).toFloat(), paint)

                            when (leftCheek != null) {
                                true -> {
                                    val leftCheekPos = leftCheek.position
                                    when (rightCheek != null && nose != null) {
                                        true -> {
                                            val rightCheekPos = rightCheek.position
                                            println ("leftCheekPos: $leftCheekPos")
                                            println ("rightCheekPos: $rightCheekPos")

                                            /*// Go with both cheek positions
                                            val cheekBonds = Rect(
                                                leftCheekPos.x.toInt(),
                                                bounds.top.toInt(),
                                                rightCheekPos.x.toInt(),
                                                bounds.bottom.toInt(),
                                            )
                                            canvas.drawRect(cheekBonds, paint)*/
                                            val textureMatrix = Matrix()

                                            val noseBottom = face.getContour(FaceContour.NOSE_BOTTOM)
                                            val noseBridge = face.getContour(FaceContour.NOSE_BRIDGE)
                                            println("noseBottom: $noseBottom")
                                            println("noseBridge: $noseBridge")
//                                            val noseRect = RectF(noseLeftBottom!!.x, noseCenterTop!!.y, noseRightBottom!!.x, noseRightBottom!!.y)

                                            println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA ${noseBottom}")
                                            println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA ${face.getContour(FaceContour.NOSE_BRIDGE)?.points}")
                                            val textureBitmapCopy = textureBitmap.copy(Bitmap.Config.ARGB_8888, true)
                                            val noseRect = RectF(
                                                noseBottom!!.points.first().x,
                                                noseBridge!!.points.first().y,
                                                (noseBottom!!.points.last().x),
                                            (noseBottom!!.points.last().y)
                                            )

                                            canvas.drawRect(noseRect, paint)

                                            textureMatrix.setRectToRect(canvas.clipBounds.toRectF(), noseRect, Matrix.ScaleToFit.FILL)
                                            canvas.drawBitmap(textureBitmapCopy, null, noseRect, null)
//                                            canvas.drawBitmap(textureBitmapCopy, textureMatrix, null)
                                        }
                                        false -> {
                                            println ("leftCheekPos: $leftCheekPos")
                                            // Go with left cheek placement on canvas only
                                        }
                                    }
                                }
                                false -> {
                                    when (rightCheek != null) {
                                        true -> {
                                            // Go with right cheek placement on canvas only
                                        }
                                        false -> {
                                            Log.i(TAG, "No cheeks")
                                        }
                                    }
                                }
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