package com.denofdevelopers.thisbeweatherapp.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

object InputViewHandler {

    fun hideKeyboard(view: View) {
        val imm =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.applicationWindowToken, 0)
    }


    fun showCursor(view: View) {
        if (view is EditText){
            view.isCursorVisible = true
        }
    }
}