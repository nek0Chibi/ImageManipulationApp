package com.example.assignment1.screens

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.assignment1.events.NavigationUiEvent
import com.example.assignment1.events.uriToBitmap


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onImagePicked: (Bitmap, Uri) -> Unit,
    onNavigate: (NavigationUiEvent) -> Unit
) {

    val context = LocalContext.current

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(uri, flag)

            val bitmap = uriToBitmap(it, context)
            bitmap?.let { newBitmap ->
                onImagePicked(newBitmap, it)
            }

            onNavigate(NavigationUiEvent.NavigateToImagePreviewScreen)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Image Manipulation App") },
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 24.dp, start = 8.dp, end = 8.dp),
            )
        }
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .clip(RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
        ) {
            Box(
                modifier = Modifier
                    .aspectRatio(2f)
                    .padding(32.dp)
                    .dashedBorder(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp,
                        dashLength = 8.dp,
                        gapLength = 4.dp,
                        cornerRadius = 16.dp
                    )
                    .clickable {
                        pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Browse Image from Gallery",
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}


@Preview
@Composable
private fun MainScreenPreview() {
    MainScreen(
        onImagePicked = { _, _ -> },
        onNavigate = {}
    )
}

@Composable
fun pickImageFromGallery(
    context: Context,
    onImagePicked: (Bitmap) -> Unit
): ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?> {
    return rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(uri, flag)

            val bitmap = uriToBitmap(it, context)
            bitmap?.let { newBitmap ->
                onImagePicked(newBitmap)
            }
        }
    }
}

fun Modifier.dashedBorder(
    color: Color,
    strokeWidth: Dp = 2.dp,
    dashLength: Dp = 8.dp,
    gapLength: Dp = 4.dp,
    cornerRadius: Dp = 0.dp // Optional: To support rounded corners
) = this.drawWithContent {
    drawContent() // Draw the container content first

    // Draw the dashed border
    val stroke = Stroke(
        width = strokeWidth.toPx(),
        pathEffect = PathEffect.dashPathEffect(
            floatArrayOf(dashLength.toPx(), gapLength.toPx())
        )
    )

    // Create a rectangle or rounded rectangle based on the corner radius
    val borderSize = Size(size.width, size.height)
    if (cornerRadius > 0.dp) {
        drawRoundRect(
            color = color,
            size = borderSize,
            cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx()),
            style = stroke
        )
    } else {
        drawRect(
            color = color,
            size = borderSize,
            style = stroke
        )
    }
}
