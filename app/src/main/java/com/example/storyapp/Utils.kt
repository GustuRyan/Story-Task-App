package com.example.storyapp

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"

fun getImageUri(context: Context): Uri {
    val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/StoryApp")
        }
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: throw IllegalStateException("Failed to create MediaStore entry")
    } else {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(storageDir, "StoryApp/$timeStamp.jpg").apply {
            if (!parentFile.exists()) parentFile.mkdirs()
        }
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    }
}

fun compressImage(context: Context, imageUri: Uri, maxSizeInMB: Int = 1): File? {
    // Maximum size in byte
    val maxSizeInBytes = maxSizeInMB * 1024 * 1024

    val inputStream = context.contentResolver.openInputStream(imageUri)
    val originalBitmap = BitmapFactory.decodeStream(inputStream)
    inputStream?.close()

    // Compress the bitmap
    var compressedFile: File? = null
    var quality = 100
    do {
        val outputStream = ByteArrayOutputStream()
        originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val byteArray = outputStream.toByteArray()
        outputStream.close()

        if (byteArray.size <= maxSizeInBytes || quality <= 10) {
            // Save compressed bitmap to file
            compressedFile = File(context.cacheDir, "compressed_image_${System.currentTimeMillis()}.jpg")
            FileOutputStream(compressedFile).use { fos ->
                fos.write(byteArray)
                fos.flush()
            }
            break
        }
        quality -= 10
    } while (quality > 0)

    return compressedFile
}

class FakeFlowDelegate<T> {
    val flow: Flow<T> = flowOf() // or any other default value/implementation
}
