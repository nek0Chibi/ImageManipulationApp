package com.example.assignment1

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awxkee.aire.Aire
import com.example.assignment1.events.rotate
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val storage: StorageRepository
) : ViewModel() {

    private val defaultBitmap = createBitmap(100, 100)

    private val _bitmap = MutableStateFlow(defaultBitmap)
    val bitmap = _bitmap.asStateFlow()

    private val _imageInfo = MutableStateFlow<ImageInfo?>(null)
    val imageInfo = _imageInfo.asStateFlow()

    private val _editableImage = MutableStateFlow<Bitmap?>(_bitmap.value)
    val editableImage = _editableImage.asStateFlow()

    fun onNewEdit(bitmap: Bitmap) { _editableImage.update { bitmap } }

    fun updateBitmap(newBitmap: Bitmap) {
        _bitmap.value = newBitmap
        _editableImage.update { newBitmap }
    }

    fun updateImage(new: ImageInfo) { _imageInfo.update { new } }

    fun onEvent(event: UiEvent) {
        when(event) {
            UiEvent.UpdateBitmap -> _editableImage.value?.let { new ->_bitmap.update { new } }
            UiEvent.OnGrayscaleClicked -> _editableImage.getAndUpdate { Aire.grayscale(it!!) }
            UiEvent.OnRotateBitmap -> _editableImage.getAndUpdate { it!!.rotate(90f) }
            UiEvent.OnSaveImage -> {
                viewModelScope.launch {
                    val state = combine(_bitmap, _imageInfo.filterNotNull()) { bmp, info ->
                        Pair(bmp, info)
                    }.first()

                    storage.saveImage(state.first, state.second)

                }
            }
        }
    }

}

class StorageRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun saveImage(
        bitmap : Bitmap,
        info: ImageInfo,
    ) {

        val file = File(context.noBackupFilesDir.absolutePath, "temp_file_${info.name}.jpg")

        try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        sdk29andUp {
            try {
                val collection = Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                val path = Environment.DIRECTORY_PICTURES + "/" + context.getString(R.string.app_name)
                val formatter = DateTimeFormatter.ofPattern("dd-MM HH:mm")

                val values = ContentValues().apply {
                    put(Media.DISPLAY_NAME, "${info.name}-${LocalDateTime.now().format(formatter)}")
                    put(Media.SIZE, file.length())
                    put(Media.MIME_TYPE, "image/jpeg")
                    put(Media.RELATIVE_PATH, path)
                }

                context.contentResolver.insert(
                    collection, values
                )?.also { uri ->
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        val fis = FileInputStream(file)
                        var length : Int
                        val buffer = ByteArray(8192)
                        while (fis.read(buffer).also { length = it } > 0)
                            outputStream.write(buffer, 0, length)
                    }
                    println(uri)
                } ?: throw IOException("Error creating entry in MediaStore")
            }catch (e : IOException){
                e.printStackTrace()
            }catch (e : IllegalArgumentException){
                e.printStackTrace()
            } catch (e : Exception){
                e.printStackTrace()
            } finally {
                file.delete()
            }
        } ?: saveImageBefore29(file, info)
    }

    private fun saveImageBefore29(image: File, info: ImageInfo) {
        try {
            val resolver = context.contentResolver
            val collection = Media.EXTERNAL_CONTENT_URI

            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .absolutePath + "/${context.getString(R.string.app_name)}"
            val directory = File(dir)
            if (!directory.exists())
                directory.mkdirs()

            val formatter = SimpleDateFormat("dd-MM HH:mm", Locale.getDefault())

            val fileName = "${info.name}-${formatter.format(Date())} "
            val savedImage = File(dir, fileName)
            val values = ContentValues().apply {
                put(Media.DISPLAY_NAME, fileName)
                put(Media.SIZE, image.length())
                put(Media.MIME_TYPE, "image/jpeg")
                put(Media.DATA, savedImage.path)
            }

            resolver.insert(collection, values)?.also { uri ->
                resolver.openOutputStream(uri)?.use { outputStream ->
                    val fis = FileInputStream(image)
                    var length : Int
                    val buffer = ByteArray(8192)
                    while (fis.read(buffer).also { length = it } > 0)
                        outputStream.write(buffer, 0 , length)
                }
            } ?: throw IOException("Error Writing to MediaStore")
        }catch (e : IOException){
            e.printStackTrace()
        }catch (e : IllegalArgumentException){
            e.printStackTrace()
        }catch (e : Exception){
            e.printStackTrace()
        } finally {
            image.delete()
        }
    }
}

sealed interface UiEvent {
    data object UpdateBitmap: UiEvent
    data object OnGrayscaleClicked: UiEvent
    data object OnRotateBitmap: UiEvent
    data object OnSaveImage: UiEvent
}

inline fun <T> sdk29andUp(onSdk29: () -> T): T? = if (Build.VERSION.SDK_INT >= 29) onSdk29() else null