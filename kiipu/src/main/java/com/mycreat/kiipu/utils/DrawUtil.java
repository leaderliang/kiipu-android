package com.mycreat.kiipu.utils;

import android.graphics.*;
import android.util.DisplayMetrics;
import com.mycreat.kiipu.core.KiipuApplication;

/**
 * Created by zhanghaihai on 2017/8/18.
 */
public class DrawUtil {

    public static void drawRoundCorner(Canvas canvas, float width, float height, float ltRadius, float rtRadius, float lbRadius, float rbRadius){

        Path path = new Path();

        //左上角圆弧
        float controlDis = ltRadius - ltRadius * getRadians(45);
        path.moveTo(0, 0);
        path.lineTo(0, ltRadius);
        path.quadTo( controlDis, controlDis, ltRadius, 0);
        path.lineTo(0, 0);

        //右上角圆弧
        controlDis = rtRadius - rtRadius * getRadians(45);
        path.moveTo(width, 0);
        path.lineTo(width - rtRadius, 0);
        path.quadTo( width - controlDis, controlDis, width, rtRadius);
        path.lineTo(width, 0);

        //左下角圆弧
        controlDis = lbRadius - lbRadius * getRadians(45);
        path.moveTo(0, height);
        path.lineTo(0, height - lbRadius);
        path.quadTo( controlDis, height - controlDis, width, lbRadius);
        path.lineTo(0, height);

        //右下角圆弧
        controlDis = rbRadius - rbRadius * getRadians(45);
        path.moveTo(width, height);
        path.lineTo(width, height - rbRadius);
        path.quadTo( width - controlDis, height - controlDis, width - rbRadius, height);
        path.lineTo(width, height);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        //绘制背景
        //canvas.drawRect(0, 0, width, height, paint);
        //裁剪圆角
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPath(path, paint);
    }


    public static float getRadians(float angle){

        return (float )(Math.PI * angle / 180);
    }
}
