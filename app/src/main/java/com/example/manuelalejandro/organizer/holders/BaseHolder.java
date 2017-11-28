package com.example.manuelalejandro.organizer.holders;

import android.widget.ImageView;

/**
 * Created by mfreites on 2017-08-03.
 */

public abstract class BaseHolder {
    public int ID;
    public ImageView imageView;
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}
