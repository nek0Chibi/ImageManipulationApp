package com.example.assignment1

import android.net.Uri

// Data class to hold image information
data class ImageInfo(
    val name: String?,
    val width: Int?,
    val height: Int?,
    val format: String?,  // MIME type (e.g., "image/png", "image/jpeg")
    val fileSize: Long?,
    val uri: Uri?
)

//
//fun getImageInfoFromUri(context: Context, imageUri: Uri): ImageInfo {
//    val contentResolver = context.contentResolver
//    var name: String? = null
//    var fileSize: Long? = null
//    var mimeType: String? = null
//    var width: Int? = null
//    var height: Int? = null
//
//    // Step 1: Retrieve file name, file size, and format
//    contentResolver.query(imageUri, null, null, null, null)?.use { cursor ->
//        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
//
//        if (cursor.moveToFirst()) {
//            name = cursor.getString(nameIndex)
//            fileSize = cursor.getLong(sizeIndex)
//        }
//    }
//
//    // Step 2: Get MIME type (format)
//    mimeType = contentResolver.getType(imageUri)
//
//    // Step 3: Load the Bitmap to get width and height
//    val inputStream = contentResolver.openInputStream(imageUri)
//    val bitmap = BitmapFactory.decodeStream(inputStream)
//    width = bitmap?.width
//    height = bitmap?.height
//
//    // Return the information as a custom data class (ImageInfo)
//    return ImageInfo(name, width, height, mimeType, fileSize)
//}
//
//
