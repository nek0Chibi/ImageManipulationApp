package com.example.assignment1

import ImagePreviewScreen
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.canhub.cropper.CropImageContract
import com.example.assignment1.events.onImageEditEvent
import com.example.assignment1.events.onImagePreviewEvent
import com.example.assignment1.events.onNavigationEvent
import com.example.assignment1.events.uriToBitmap
import com.example.assignment1.screens.ImageEditScreen
import com.example.assignment1.screens.MainScreen
import com.example.assignment1.screens.WelcomeScreen

@Composable
fun Navigation(viewModel: MainViewModel = hiltViewModel()) {

    val navController = rememberNavController()
    val context = LocalContext.current
    var infoDialogVisible by remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = Routes.WELCOMESCREEN
    ) {
        composable(
            route = Routes.WELCOMESCREEN,
            enterTransition = { fadeIn() + slideInVertically() },
            exitTransition = { fadeOut() + slideOutVertically() }
        ) {
            WelcomeScreen { navController.navigate(Routes.MAINSCREEN) }
        }
        composable(Routes.MAINSCREEN) {
            MainScreen(
                onImagePicked = { pickedBitmap, info ->
                    viewModel.updateBitmap(pickedBitmap)
                    viewModel.updateImage(info)
                },
                onNavigate = { event ->
                    onNavigationEvent(event, navController)
                }
            )
        }
        composable(Routes.IMAGEPREVIEWSCREEN) {
            val bitmap = viewModel.bitmap.collectAsState().value
            val imageUri = viewModel.imageInfo.collectAsState().value
            ImagePreviewScreen(
                bitmap = bitmap,
                onNavigate = { event ->
                    onNavigationEvent(event, navController)
                },
                onClickEvent = { event ->
                    onImagePreviewEvent(
                        event = event,
                        navController = navController,
                        imageUri = imageUri?.uri,
                        context = context,
                        onSaveImage = { viewModel.onEvent(UiEvent.OnSaveImage) },
                        showInfoDialog = { infoDialogVisible = true }
                    )
                }
            )

            if (infoDialogVisible) {
                ImageInfoDialog(imageInfo = imageUri!!) {
                    infoDialogVisible = false
                }
            }

        }
        composable(Routes.IMAGEEDITSCREEN) {

            val bitmap = viewModel.editableImage.collectAsState().value
            val imageUri = viewModel.imageInfo.collectAsState().value

            val cropImageLauncher = rememberLauncherForActivityResult(
                contract = CropImageContract()
            ) { result ->
                if (result.isSuccessful) {
                    viewModel.onNewEdit(uriToBitmap(result.uriContent!!, context)!!.copy(Bitmap.Config.ARGB_8888, true))
                } else {
                    println("Crop Image Error: ${result.error}")
                }
            }

            ImageEditScreen(
                bitmap = bitmap!!,
                onNavigate = { event ->
                    onNavigationEvent(event, navController)
                },
                onClickEvent = { event ->
                    onImageEditEvent(
                        event = event,
                        onEvent = viewModel::onEvent,
                        imageUri = imageUri?.uri,
                    ) {
                        cropImageLauncher.launch(it)
                    }
                },
            )
        }
    }
}


@Composable
fun ImageInfoDialog(
    imageInfo: ImageInfo,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier.size(200.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
//            val imageInfo = imageUri?.let { getImageInfoFromUri(LocalContext.current, it) }

                Text(text = "Name: ${imageInfo.name ?: "Unknown"}")
                Text(text = "Width: ${imageInfo.width ?: "Unknown"}")
                Text(text = "Height: ${imageInfo.height ?: "Unknown"}")
                Text(text = "Format: ${imageInfo.format ?: "Unknown"}")
                Text(text = "File Size: ${imageInfo.fileSize ?: "Unknown"}")
            }
        }
    }
}


object Routes {


    const val WELCOMESCREEN = "welcome"
    const val MAINSCREEN = "main_screen"
    const val IMAGEPREVIEWSCREEN = "image_preview_screen"
    const val IMAGEEDITSCREEN = "image_edit_screen"

}




