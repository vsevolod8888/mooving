package com.example.mymoovingpicturedagger.helpers

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService


fun Context.getPath(uri: Uri): String? {  // тоже в реп
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = this.contentResolver.query(uri, projection, null, null, null)
    val column_index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    cursor?.moveToFirst()
    return cursor?.getString(column_index!!)
}

//hide the keyboard
fun View.hideKeyboard() {
    val inputMethodManager = this.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
}