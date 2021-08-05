package com.example.mymoovingpicturedagger.helpers

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore


fun Context.getPath(uri: Uri): String? {  // тоже в реп
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = this.contentResolver.query(uri, projection, null, null, null)
    val column_index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    cursor?.moveToFirst()
    return cursor?.getString(column_index!!)
}