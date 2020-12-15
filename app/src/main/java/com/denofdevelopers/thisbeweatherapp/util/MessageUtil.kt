package com.denofdevelopers.thisbeweatherapp.util

import android.content.Context
import android.widget.Toast

object MessageUtil {

    fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}