package com.example.manuelalejandro.organizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by manuelalejandro on 2017-01-07.
 */
public class ImageHelper {

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    public static Bitmap getImage(File destination, int reqWidth, int reqHeight, Context context){
        Bitmap user_picture_bmp = null;
        try {
            if(destination.getAbsolutePath().toLowerCase().contains("r.drawable") && context != null){
                BitmapFactory.Options options = new BitmapFactory.Options();
                //Log.d("AGGGGGGGRR", " bitmap factory==========" + options);
                options.inJustDecodeBounds = true;
                String resName = destination.getAbsolutePath().replace("/R.drawable.", "").replace("R.drawable.","").replace("/r.drawable.","").replace("r.drawable.","");
                int resId = context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
                BitmapFactory.decodeResource(context.getResources(), resId, options);
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
                options.inJustDecodeBounds = false;
                user_picture_bmp = BitmapFactory.decodeResource(context.getResources(), resId, options);

            }else{
                FileInputStream in = new FileInputStream(destination);
                BitmapFactory.Options options = new BitmapFactory.Options();
                //Log.d("AGGGGGGGRR", " bitmap factory==========" + options);
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(in, null, options);
/*            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inDither = true;*/

                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
                options.inJustDecodeBounds = false;
                in.close();
                in = new FileInputStream(destination);
                user_picture_bmp = BitmapFactory.decodeStream(in, null, options);
            }

        }catch(Exception e){
            //Log.d("AGGGGGGGRR", " bitmap factory==========" + e.getMessage());

        }
        //Log.i("user_picture_bmp", user_picture_bmp + "");
        return user_picture_bmp;
    }

    public static byte[] getByteArrayOfImage(String url, Context context){
        Bitmap bmp = null;
        byte[] byteArray = new byte[0];
        if(url == null || url.equals(""))
            return byteArray;
        try {
            if(url.toLowerCase().contains("r.drawable") && context != null){
                String resName = url.replace("/R.drawable.", "").replace("R.drawable.","").replace("/r.drawable.","").replace("r.drawable.","");
                int resId = context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
                bmp = BitmapFactory.decodeResource(context.getResources(), resId);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                if(resName.contains("yes")){
                    System.out.println("DEBUG");
                }
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byteArray = stream.toByteArray();
            }else{
                bmp = BitmapFactory.decodeStream(new FileInputStream(url), null, null);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byteArray = stream.toByteArray();
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return byteArray;
    }

    public static File copyFile(File sourceFile) throws IOException {
        if (!sourceFile.exists()) {
            return null;
        }
        File destFile = new File(Environment.getExternalStorageDirectory(), +System.currentTimeMillis() + ".jpg");
        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
       // Log.i("ImageHelper", destFile.toString());
        return destFile;

    }

    public static boolean isImage(File file){
        if(file == null || !file.exists() || !file.isFile())
            return false;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);
        return options.outWidth != -1 && options.outHeight != -1;
    }


}
