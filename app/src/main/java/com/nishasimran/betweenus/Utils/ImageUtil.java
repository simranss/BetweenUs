package com.nishasimran.betweenus.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class ImageUtil {

    public static Bitmap convert(byte[] decodedBytes) throws IllegalArgumentException {
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static byte[] convert(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        return outputStream.toByteArray();
    }

    public static Bitmap convert(String base64Str) throws IllegalArgumentException {
        Log.d("TAG", "convert: " + base64Str);
        byte[] decodedBytes = Base64.decode(
                base64Str.substring(base64Str.indexOf(",")  + 1),
                Base64.DEFAULT
        );

        return convert(decodedBytes);
    }

    public static String convertToStr(Bitmap bitmap) {
        String string = Base64.encodeToString(convert(bitmap), Base64.DEFAULT);
        Log.d("TAG", "convertToStr: " + string);
        return string;
    }
}
