package com.example.assignment1.events

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import com.awxkee.aire.Aire
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView

sealed class ImageEditEvent {
    data object OnGrayscaleClicked : ImageEditEvent()
    data object OnCropClicked : ImageEditEvent()
    data object OnResizeClicked : ImageEditEvent()
    data object OnRotateClicked : ImageEditEvent()
    data class OnSaveClicked(val bitmap: Bitmap) : ImageEditEvent()
}


fun onImageEditEvent(
    event: ImageEditEvent,
    context: Context,
    bitmap: Bitmap,
    imageUri: Uri?,
    cropImageLauncher: ManagedActivityResultLauncher<CropImageContractOptions, CropImageView.CropResult>,
    onBitmapChanged: (Bitmap) -> Unit
) {

    when (event) {
        ImageEditEvent.OnGrayscaleClicked -> {
            val softwareBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            val grayscaleBitmap = Aire.grayscale(softwareBitmap)
            onBitmapChanged(grayscaleBitmap)
        }


        ImageEditEvent.OnCropClicked -> {

            imageUri?.let {
                val cropOptions = CropImageContractOptions(
                    uri = it,
                    cropImageOptions = CropImageOptions(
                        cropShape = CropImageView.CropShape.RECTANGLE,
                        guidelines = CropImageView.Guidelines.ON
                    )
                )
                cropImageLauncher.launch(cropOptions)
            }

        }

        ImageEditEvent.OnRotateClicked -> {
            val flippedBitmap = bitmap.rotate(90f)
            onBitmapChanged(flippedBitmap)
        }

        ImageEditEvent.OnResizeClicked -> TODO()

        is ImageEditEvent.OnSaveClicked -> {
            onBitmapChanged(event.bitmap)
        }
    }
}


fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun uriToBitmap(uri: Uri, context: Context): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // For Android 9 (Pie) and above, use ImageDecoder
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            // For Android 8 (Oreo) and below, use MediaStore
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}