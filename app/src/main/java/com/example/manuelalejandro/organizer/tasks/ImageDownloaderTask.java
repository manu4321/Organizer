package com.example.manuelalejandro.organizer.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.manuelalejandro.organizer.CursorArrayAdapter;
import com.example.manuelalejandro.organizer.ImageHelper;
import com.example.manuelalejandro.organizer.R;
import com.example.manuelalejandro.organizer.holders.BaseHolder;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by mfreites on 2017-07-29.
 */

public class ImageDownloaderTask extends AsyncTask<Object, Void, Bitmap> {
    private static final int REQ_WIDTH = 100;
    private static final int REQ_HEIGHT = 100;
    private WeakReference<BaseHolder> holderReference;
    private WeakReference<ImageView> imageViewWeakReference;

    private Context context;
    private int passedId = 0;
    private static ImageDownloaderTask instance;

    public static ImageDownloaderTask getInstance(Context context, BaseHolder viewHolder, int position){
        return  new ImageDownloaderTask(context, viewHolder, position);
    }
    public ImageDownloaderTask(Context context, BaseHolder holder, int position){
        this.context = context;
        holderReference = new WeakReference<BaseHolder>(holder);
        this.passedId = position;
    }

    public void ExecuteImageProcess(String url){
        this.execute(url);
    }
    @Override
    protected Bitmap doInBackground(Object... params) {
        Object o = params[0];
        if(o instanceof File) {
            return ImageHelper.getImage((File) o, REQ_WIDTH, REQ_HEIGHT, context);
        }else if(o instanceof String){
                return ImageHelper.getImage(new File((String)o), REQ_WIDTH, REQ_HEIGHT, context);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }
        if (holderReference != null) {
            // Log.i("there", cap + " is " + holderReference.get().smallImage);
            BaseHolder viewHolder = holderReference.get();
            if (viewHolder != null && viewHolder.getImageView() != null) {
                if(viewHolder.getID() == passedId){
                    if (bitmap != null) {
                        viewHolder.getImageView().setImageBitmap(bitmap);
                    } else {
                        //Log.i("asde", "aseeeeee");
                        Drawable placeholder = context.getResources().getDrawable(R.mipmap.ic_launcher);
                        viewHolder.getImageView().setImageDrawable(placeholder);
                    }
                }

            }
        }

    }
}