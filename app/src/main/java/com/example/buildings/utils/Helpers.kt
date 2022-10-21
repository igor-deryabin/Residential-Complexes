package com.example.buildings.utils

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.IOException
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

val EXTERNAL_STORAGE_PERMISSIONS_REQUIRED = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE
)

fun Double.toMoney(): String {
    val df = DecimalFormat(
        "##0.###",
        DecimalFormatSymbols(Locale.ENGLISH).apply { groupingSeparator = ' ' })
    val minimumFractionDigits = if (this % 1 != 0.0) 1 else 0
    df.minimumFractionDigits = minimumFractionDigits
    val bd = toBigDecimal()
    return df.format(bd)
}

fun getFileFromUri(context: Context, uri: Uri): File {
    return File(RealPathUtil.getRealPath(context, uri)!!)
}

@Throws(IOException::class)
fun createImageFile(context: Context): File {
    var fileName = ""
    val suffix = ".jpg"
    val timeStamp = SimpleDateFormat("ddMMyyyy_HHmmssSSS", Locale.ENGLISH).format(Date())
    fileName = timeStamp

    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(fileName, suffix, storageDir)
}
