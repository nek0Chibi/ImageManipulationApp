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
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import com.example.assignment1.events.ImagePreviewEvent
import com.example.assignment1.events.NavigationUiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePreviewScreen(
    bitmap: Bitmap,
    onNavigate: (NavigationUiEvent) -> Unit,
    onClickEvent: (ImagePreviewEvent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onNavigate(NavigationUiEvent.NavigateBack)
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ImageOptions.entries.forEach {
                        ImageOptionIcon(option = it, onEvent = onClickEvent)
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

        }
    }
}

@Composable
fun ImageOptionIcon(
    option: ImageOptions,
    onEvent: (ImagePreviewEvent) -> Unit
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


enum class ImageOptions(
    val icon: ImageVector,
    val title: String,
    val event: ImagePreviewEvent
) {
    Info(icon = Icons.Outlined.Info, title = "Info", event = ImagePreviewEvent.OnInfoClicked),
    Edit(icon = Icons.Outlined.Edit, title = "Edit", event = ImagePreviewEvent.OnEditClicked),
    Share(icon = Icons.Outlined.Share, title = "Share", event = ImagePreviewEvent.OnShareClicked),
    Save(icon = Icons.Outlined.Save, title = "Save", event = ImagePreviewEvent.OnSaveClicked),
}

@Preview
@Composable
private fun ImagePreviewScreenPreview() {

    ImagePreviewScreen(
        bitmap = createBitmap(100, 100),
        onNavigate = {},
        onClickEvent = {}
    )

}