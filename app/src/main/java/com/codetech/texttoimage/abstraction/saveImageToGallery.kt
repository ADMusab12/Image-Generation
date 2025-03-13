package com.codetech.texttoimage.abstraction

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.OutputStream

fun saveImageToGallery(context: Context, bitmap: Bitmap, title: String): Uri? {
    val contentResolver = context.contentResolver
    val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "$title.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.DATA, "${Environment.getExternalStorageDirectory().path}/$title.jpg")
        }
    }

    val imageUri = contentResolver.insert(imageCollection, contentValues)
    imageUri?.let { uri ->
        try {
            val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
            outputStream?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            }
            return uri
        } catch (e: Exception) {
            contentResolver.delete(uri, null, null)
            e.printStackTrace()
        }
    }
    return null
}