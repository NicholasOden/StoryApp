package com.example.picodiploma.storyapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "CreateImageFile"

fun createImageFile(context: Context): Uri? {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"

    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    if (storageDir == null) {
        Log.e(TAG, "Failed to get external storage directory")
        return null
    }

    val imageFile: File
    try {
        imageFile = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
    } catch (ex: IOException) {
        Log.e(TAG, "Failed to create image file", ex)
        return null
    }

    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
}

fun File.reduceFileImage(maxFileSize: Long): File? {

    if (!this.exists()) {
        Log.e(TAG, "File not found: ${this.path}")
        return null
    }
    val bitmap = BitmapFactory.decodeFile(path)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
        Log.d(TAG, "Compression quality: $compressQuality")
    } while (streamLength > maxFileSize)
    try {
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(this))
    } catch (ex: Exception) {
        Log.e(TAG, "Failed to compress image file", ex)
        return null
    }
    return this
}
