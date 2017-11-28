package com.example.manuelalejandro.organizer.Permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;

/**
 * Created by mfreites on 2017-07-23.
 */

public class PermissionUtil {
    public static final int REQUEST_EXTERNAL_STORAGE_PHOTO = 1;
    public static final int REQUEST_EXTERNAL_STORAGE_SELECTOR = 2;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static boolean hasPermissionOrRequest(Activity c, final int requestCode){
        int permission = ActivityCompat.checkSelfPermission(c, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean hasPermission = false;
        if (permission == PackageManager.PERMISSION_GRANTED) {
            hasPermission = true;
        } else {
            hasPermission = false;
            ActivityCompat.requestPermissions(
                    c,
                    PERMISSIONS_STORAGE,
                    requestCode
            );
        }
        return hasPermission;
    }

    public static boolean forceToHavePermission(Activity c){
        return forceToHavePermission(c, false, REQUEST_EXTERNAL_STORAGE_PHOTO);
    }

    private static boolean forceToHavePermission(Activity c, boolean perm, final int requestCode){
        if(perm){
            return true;
        }
        int permission = ActivityCompat.checkSelfPermission(c, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean hasPermission = false;
        if (permission == PackageManager.PERMISSION_GRANTED) {
            hasPermission = true;
        } else {
            hasPermission = false;
            ActivityCompat.requestPermissions(
                    c,
                    PERMISSIONS_STORAGE,
                    requestCode
            );
        }
        return forceToHavePermission(c, hasPermission, requestCode);
    }

    public void requestPermission(Activity c, final int requestCode){
        ActivityCompat.requestPermissions(
                c,
                PERMISSIONS_STORAGE,
                requestCode
        );
    }
}
