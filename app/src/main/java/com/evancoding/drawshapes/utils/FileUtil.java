package com.evancoding.drawshapes.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {
    public static File save(Bitmap bitmap, Context context) {
        if (bitmap == null) {
            Toast.makeText(context, "You have not drawn anything yet.", Toast.LENGTH_LONG).show();
            return null;
        }

        String root = Environment.getExternalStorageDirectory().toString();
        File dir = new File(root + "/draw_images");
        if (!dir.exists() || !dir.isDirectory()) {
            if (!dir.mkdirs()) {
                Toast.makeText(context, "Can't create directory.", Toast.LENGTH_LONG).show();
                return dir;
            }
        }
        String time = new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date());
        File file = new File(dir, "Draw" + time + ".png");
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Toast.makeText(context, "Image saved at" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "Can't save file.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return file;
    }

    public static void mail(Bitmap bitmap, Activity activity) {
        File file = save(bitmap, activity);
        if (file == null) {
            return;
        }

        Uri uri = Uri.fromFile(file);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("image/*");

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My drawing");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Here is my drawing.");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        activity.startActivity(Intent.createChooser(emailIntent, "Sending image..."));
    }
}
