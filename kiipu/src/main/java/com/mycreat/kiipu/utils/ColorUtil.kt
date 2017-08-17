package com.mycreat.kiipu.utils

import android.graphics.Bitmap
import android.support.v7.graphics.Palette

/**
 * 色彩相关通用方法
 * Created by zhanghaihai on 2017/8/17.
 */
class ColorUtil {
    companion object {
        fun getVibRantColor(bitmap: Bitmap?, defaultColor: Int): Int {
            bitmap ?: return defaultColor
            val builder = Palette.from(bitmap)
            val p = builder.generate()
            return p.getVibrantColor(defaultColor)
        }
    }


}
