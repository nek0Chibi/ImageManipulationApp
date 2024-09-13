package com.example.assignment1.events

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.navigation.NavController
import com.example.assignment1.Routes

sealed class ImagePreviewEvent {
    data object OnInfoClicked : ImagePreviewEvent()
    data object OnEditClicked : ImagePreviewEvent()
    data object OnShareClicked : ImagePreviewEvent()
    data object OnSaveClicked : ImagePreviewEvent()
}

fun onImagePreviewEvent(
    event: ImagePreviewEvent,
    navController: NavController,
    imageUri: Uri?,
    context: Context,
    onSaveImage: () -> Unit,
    showInfoDialog: () -> Unit
) {
    when (event) {
        ImagePreviewEvent.OnInfoClicked -> showInfoDialog()

        ImagePreviewEvent.OnEditClicked -> {
            navController.navigate(Routes.IMAGEEDITSCREEN)
        }

        ImagePreviewEvent.OnShareClicked -> {
            shareImage(imageUri, context)
        }

        ImagePreviewEvent.OnSaveClicked -> onSaveImage()
    }
}

fun shareImage(imageUri: Uri?, context: Context) {
    val shareIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, imageUri)
        type = "image/png" // Set MIME type of the image
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant permission to read Uri
    }

    context.startActivity(
        Intent.createChooser(shareIntent, "Share Image Via")
    )
}