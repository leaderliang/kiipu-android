package com.mycreat.kiipu.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import com.mycreat.kiipu.core.KiipuApplication

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
            val s = p.vibrantSwatch
            return if(s == null) defaultColor else s.rgb
        }

        /** 根据给定的color和alpha值得到给定的color  */
        fun getColor(color: Int, aAlpha: Float): Int {
            val alpha = Math.round(Color.alpha(color) * aAlpha)
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            return Color.argb(alpha, red, green, blue)
        }

        fun  getColor(@ColorRes color:Int):Int{
            return ContextCompat.getColor(KiipuApplication.appContext, color)
        }
    }


}
