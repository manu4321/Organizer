package com.example.manuelalejandro.organizer.cache;

import android.graphics.Bitmap;
import android.media.Image;
import android.util.LruCache;

/**
 * Created by mfreites on 2017-07-31.
 */

public class ImageCache {
    private LruCache<String, Bitmap> mMemoryCache;
    private static ImageCache _instance;
    public static ImageCache getInstance(){
        if(_instance == null){
            _instance = new ImageCache();
        }
        return _instance;
    }
    public ImageCache(){
        this((int) (Runtime.getRuntime().maxMemory() / 1024), (int) (Runtime.getRuntime().maxMemory() / 1024) / 8);
    }
    public ImageCache(int maxMemory, int cacheSize){
        this.mMemoryCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
    }
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}
