package com.example.manuelalejandro.organizer.FileAccessor;

import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import com.example.manuelalejandro.organizer.ImageHelper;
import com.example.manuelalejandro.organizer.R;
import com.example.manuelalejandro.organizer.Utils.Constants;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mfreites on 2017-07-29.
 */

public class FilesIntoCollection {

    String path;
    public FilesIntoCollection(){
        this(Constants.IMAGE_DIRECTORY);
    }
    public FilesIntoCollection(String path){
        this.path = Environment.getExternalStorageDirectory() + path;
    }
    private List setUpList(){
        List s = new ArrayList();
        File file = new File(path);
        Log.i(path, path);
        File[] listOFFiles = file.listFiles();

        if(listOFFiles != null){
            for(File f: listOFFiles){
                if(ImageHelper.isImage(f)){
                    s.add(f);
                    Log.i("GetImagesIntoSets", "file:"  + f.getName());

                }else{
                    Log.i("GetImagesIntoSets", "Not a file"  + f.getName());
                }
            }
        }

        return s;
    }
    public List getImagesIntoList(){
        return getImagesIntoList(true);
    }
    public List getImagesIntoList(boolean getImagesFromDrawable){
        List  s = setUpList();
        if(getImagesFromDrawable){
            s.addAll(getImagesFromDrawable());
        }
        return s;
    }

    public List getImagesFromDrawable(){
        Field[] fields = R.drawable.class.getFields();
        List drawables = new ArrayList();
        for(Field f : fields) {
            try{
                String name = f.getName();
                if(!name.contains("abc_")){
                    drawables.add(Constants.R_DRAWABLE_PREFIX + name);
                    Log.i("field is: ", name+ "");
                }else{
                    Log.w("Skippible drawable", "Skipping file with field:" + name);
                }


            }catch(Exception e){
                Log.e("GetImagesFromDrawable", "Exception happened during getting drawables, continue looping" + e.getMessage());
            }

        }

        return drawables;
    }


    public String getPath() {
        return Environment.getExternalStorageDirectory()+path;
    }

    public void setPath(String path) {
        this.path = Environment.getExternalStorageDirectory()+path;
    }

}
