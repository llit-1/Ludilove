@file:Suppress("DEPRECATION")

package com.example.ludilove

import android.view.View
import android.view.Window

object FullScreenHelper {
    fun enableFullScreen(window: Window) {
        window.decorView.apply {
            systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_FULLSCREEN // Скрыть строку состояния (status bar)
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Скрыть нижнюю панель навигации (navigation bar)
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }
    }
}
