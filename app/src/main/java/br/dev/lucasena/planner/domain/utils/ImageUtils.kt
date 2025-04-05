package br.dev.lucasena.planner.domain.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import android.util.*

fun Context.imageUriToBitmap(uri: Uri): Bitmap? =
    try {
        val contentResolver = this.contentResolver
        // versions greater then Android 9
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }
    } catch (_: Exception) {
        null
    }

fun bitmapToString(bitmap: Bitmap): String {
    val byteArrayOutputString = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.WEBP, 50, byteArrayOutputString)
    val byteArray = byteArrayOutputString.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun stringToBitmap(byteArrayString: String): Bitmap? =
    try {
        val decodeBytes = Base64.decode(byteArrayString, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodeBytes, 0, decodeBytes.size)
    } catch (_: Exception) {
        null
    }