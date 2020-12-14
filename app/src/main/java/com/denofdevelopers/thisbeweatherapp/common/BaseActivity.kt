package com.denofdevelopers.thisbeweatherapp.common

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.denofdevelopers.thisbeweatherapp.util.InputViewHandler
import io.github.inflationx.viewpump.ViewPumpContextWrapper

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActivityComponent()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view is EditText) {
                InputViewHandler.showCursor(view)
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    view.clearFocus()
                    InputViewHandler.hideKeyboard(view)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    protected abstract fun setupActivityComponent()

}