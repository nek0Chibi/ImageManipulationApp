package com.example.assignment1.events

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.assignment1.UiEvent

sealed class ImageEditEvent {
    data object OnGrayscaleClicked : ImageEditEvent()
    data object OnCropClicked : ImageEditEvent()
    data object OnResizeClicked : ImageEditEvent()
    data object OnRotateClicked : ImageEditEvent()
    data class OnSaveClicked(val bitmap: Bitmap) : ImageEditEvent()
}


fun onImageEditEvent(
    event: ImageEditEvent,
    onEvent: (UiEvent) -> Unit,
    imageUri: Uri?,
    onLaunchCropIntent: (CropImageContractOptions) -> Unit,
) {
    when(event) {
        ImageEditEvent.OnGrayscaleClicked -> onEvent(UiEvent.OnGrayscaleClicked)
        ImageEditEvent.OnRotateClicked -> onEvent(UiEvent.OnRotateBitmap)
        is ImageEditEvent.OnSaveClicked -> onEvent(UiEvent.UpdateBitmap)
        ImageEditEvent.OnCropClicked -> {
            imageUri?.let {
                val cropOptions = CropImageContractOptions(
                    uri = it,
                    cropImageOptions = CropImageOptions(
                        cropShape = CropImageView.CropShape.RECTANGLE,
                        guidelines = CropImageView.Guidelines.ON
                    )
                )
                onLaunchCropIntent(cropOptions)
            }

        }
        else -> { /* no-op */ }
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