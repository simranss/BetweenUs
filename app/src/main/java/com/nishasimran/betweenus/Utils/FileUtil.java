package com.nishasimran.betweenus.Utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {

    private static final String TAG = "FileUtils";

    @Nullable
    public static File writeToFile(File dir, String id, String data) {
        try {
            File file = new File(dir, id + ".txt");
            FileWriter outputStreamWriter = new FileWriter(file);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            return file;
        }
        catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.getMessage());
            return null;
        }
    }

    @NonNull
    public static String readFromFile(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in;

        try {
            in = new BufferedReader(new FileReader(file));
            while ((line = in.readLine()) != null)
                stringBuilder.append(line);

        } catch (FileNotFoundException e) {
            Log.e(TAG, "readFromFile: FileNotFoundException", e);
        } catch (IOException e) {
            Log.e(TAG, "readFromFile: IOException", e);
        }

        return stringBuilder.toString();
    }

    @Nullable
    public static File writeImageToFile(Context context, String id, String data) {
        File dir = new File(context.getFilesDir(), "images");
        return writeToFile(dir, id, data);
    }

}
