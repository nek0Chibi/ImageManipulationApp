package com.example.assignment1

import ImagePreviewScreen
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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

    NavHost(
        navController = navController,
        startDestination = Routes.WELCOMESCREEN
    ) {
        composable(Routes.WELCOMESCREEN) {
            WelcomeScreen(navController)
        }
        composable(Routes.MAINSCREEN) {
            MainScreen(
                onImagePicked = { pickedBitmap, uri ->
                    viewModel.updateBitmap(pickedBitmap)
                    viewModel.updateImageUri(uri)
                },
                onNavigate = { event ->
                    onNavigationEvent(event, navController)
                }
            )
        }
        composable(Routes.IMAGEPREVIEWSCREEN) {
            val bitmap = viewModel.bitmap.collectAsState().value
            val imageUri = viewModel.imageUri.collectAsState().value
            ImagePreviewScreen(
                bitmap = bitmap,
                onNavigate = { event ->
                    onNavigationEvent(event, navController)
                },
                onClickEvent = { event ->
                    onImagePreviewEvent(event, navController, imageUri, context)
                }
            )
        }
        composable(Routes.IMAGEEDITSCREEN) {

            val bitmap = viewModel.bitmap.collectAsState().value
            val imageUri = viewModel.imageUri.collectAsState().value

            val cropImageLauncher = rememberLauncherForActivityResult(
                contract = CropImageContract()
            ) { result ->
                if (result.isSuccessful) {
                    val croppedImageUri = result.uriContent

                    val croppedBitmap = uriToBitmap(croppedImageUri!!, context)
                    viewModel.updateBitmap(croppedBitmap!!)

                } else {
                    println("Crop Image Error: ${result.error}")
                }
            }

            ImageEditScreen(
                bitmap = bitmap,
                onNavigate = { event ->
                    onNavigationEvent(event, navController)
                },
                onClickEvent = { event ->
                    onImageEditEvent(event, context, bitmap, imageUri, cropImageLauncher) {
                        viewModel.updateBitmap(it)
                    }
                },
                onCancelEvent = { bitmapOrg ->
                    viewModel.updateBitmap(bitmapOrg)
                }
            )

        }
    }
}



@Composable
fun ImageInfoDialog(
    imageUri: Uri?,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            val imageInfo = imageUri?.let { getImageInfoFromUri(LocalContext.current, it) }

            Text(text = "Name: ${imageInfo?.name ?: "Unknown"}")
            Text(text = "Width: ${imageInfo?.width ?: "Unknown"}")
            Text(text = "Height: ${imageInfo?.height ?: "Unknown"}")
            Text(text = "Format: ${imageInfo?.format ?: "Unknown"}")
            Text(text = "File Size: ${imageInfo?.fileSize ?: "Unknown"}")
        }
    }
}





object Routes {


    const val WELCOMESCREEN = "welcome"
    const val MAINSCREEN = "main_screen"
    const val IMAGEPREVIEWSCREEN = "image_preview_screen"
    const val IMAGEEDITSCREEN = "image_edit_screen"

}




