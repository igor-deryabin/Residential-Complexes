package com.example.buildings.utils.extension

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat

fun Activity.hasPermissions(permissions: Array<String>): Boolean = permissions.all {
    ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
}

fun Activity.hideKeyboard() {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow((currentFocus ?: View(this)).windowToken, 0)
    window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    currentFocus?.clearFocus()
}