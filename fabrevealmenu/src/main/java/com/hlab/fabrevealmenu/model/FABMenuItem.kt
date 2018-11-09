package com.hlab.fabrevealmenu.model

import android.graphics.Bitmap
import android.graphics.drawable.Drawable

data class FABMenuItem(
        var title: String,
        var id: Int = 0,
        var iconDrawable: Drawable? = null,
        val iconBitmap: Bitmap? = null,
        var isEnabled: Boolean = true
)
