package com.example.buildings.utils.extension

import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.longToast(text: String) {
    Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
}