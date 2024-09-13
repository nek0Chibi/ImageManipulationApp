package com.example.assignment1

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val defaultBitmap = createBitmap(100, 100)

    private val _bitmap = MutableStateFlow(defaultBitmap)
    val bitmap = _bitmap.asStateFlow()

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri = _imageUri.asStateFlow()

    fun updateBitmap(newBitmap: Bitmap) {
        _bitmap.value = newBitmap
    }

    fun updateImageUri(newUri: Uri) {
        _imageUri.value = newUri
    }

}