package com.example.manuelalejandro.organizer;

/**
 * Created by manuelalejandro on 2017-02-22.
 */
public class Settings {

    private static boolean isLoadingImages = false;

    public static boolean isLoadingImages() {
        return isLoadingImages;
    }

    public static void setIsLoadingImages(boolean isLoadingImages) {
        Settings.isLoadingImages = isLoadingImages;
    }
}
