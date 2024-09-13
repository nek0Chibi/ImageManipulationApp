package com.example.assignment1.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PhotoSizeSelectSmall
import androidx.compose.material.icons.filled.PieChartOutline
import androidx.compose.material.icons.filled.Rotate90DegreesCw
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import com.example.assignment1.events.ImageEditEvent
import com.example.assignment1.events.NavigationUiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageEditScreen(
    bitmap: Bitmap,
    onNavigate: (NavigationUiEvent) -> Unit,
    onClickEvent: (ImageEditEvent) -> Unit,
    onCancelEvent: (Bitmap) -> Unit
) {

    val bitmapOrg = mutableStateOf(bitmap)


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onNavigate(NavigationUiEvent.NavigateBack)
                            onCancelEvent(bitmapOrg.value)
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            onNavigate(NavigationUiEvent.NavigateToImagePreviewScreen)
                            onClickEvent(ImageEditEvent.OnSaveClicked(bitmap))
                        }
                    ) {
                        Icon(Icons.Default.Done,null)
                    }
                },
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ImageEditOptions.entries.forEach {
                        ImageEditOptionsIcon(option = it, onEvent = onClickEvent)
                    }
                }
            }
        }
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {

            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "image preview",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )

//            Image(
//                painter = painterResource(R.drawable.image),
//                contentDescription = "image preview",
//                contentScale = ContentScale.Fit,
//                modifier = Modifier.fillMaxSize()
//            )
        }

    }

}

@Composable
fun ImageEditOptionsIcon(
    option: ImageEditOptions,
    onEvent: (ImageEditEvent) -> Unit
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clickable {
                onEvent(option.event)
            }
    ) {
        Icon(imageVector = option.icon, contentDescription = null)
        Text(text = option.title)
    }

}

enum class ImageEditOptions(
    val icon: ImageVector,
    val title: String,
    val event: ImageEditEvent
) {
    Grayscale(icon = Icons.Default.PieChartOutline, title = "Grayscale", event = ImageEditEvent.OnGrayscaleClicked),
    Crop(icon = Icons.Default.Crop, title = "Crop", event = ImageEditEvent.OnCropClicked),
    Resize(icon = Icons.Default.PhotoSizeSelectSmall, title = "Resize", event = ImageEditEvent.OnResizeClicked),
    Rotate(icon = Icons.Default.Rotate90DegreesCw, title = "Rotate", event = ImageEditEvent.OnRotateClicked),
}


@Preview
@Composable
private fun ImageEditScreenPreview() {
    ImageEditScreen(
        bitmap = createBitmap(100, 100),
        onNavigate = {},
        onClickEvent = {},
        onCancelEvent = {}
    )
}