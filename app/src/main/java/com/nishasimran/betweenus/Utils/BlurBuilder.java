package com.nishasimran.betweenus.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.renderscript.Allocation;
import androidx.renderscript.Element;
import androidx.renderscript.RenderScript;
import androidx.renderscript.ScriptIntrinsicBlur;

public class BlurBuilder {
    private static final float BITMAP_SCALE = 0.4f;
    private static final float BLUR_RADIUS = 7.5f;

    public static Bitmap blur(Context context, @DrawableRes int drawableId, Float scale, Float radius) {
        if (context != null) {
            Bitmap image = BitmapFactory.decodeResource(context.getResources(), drawableId);

            int width, height;
            if (scale != null) {
                width = Math.round(image.getWidth() * scale);
            } else {
                width = Math.round(image.getWidth() * BITMAP_SCALE);
            }
            if (radius != null) {
                height = Math.round(image.getHeight() * radius);
            } else {
                height = Math.round(image.getHeight() * BITMAP_SCALE);
            }

            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            RenderScript rs = RenderScript.create(context);
            ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
            theIntrinsic.setRadius(BLUR_RADIUS);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);

            return outputBitmap;
        }
        return null;
    }
}
