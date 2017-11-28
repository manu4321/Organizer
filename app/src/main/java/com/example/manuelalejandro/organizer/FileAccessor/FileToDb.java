package com.example.manuelalejandro.organizer.FileAccessor;

import android.app.Activity;
import android.util.Log;

import com.example.manuelalejandro.organizer.DatabaseAccessor;
import com.example.manuelalejandro.organizer.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mfreites on 2017-07-06.
 */

public class FileToDb {
    private static final String TAG = "FileToDb";
    private InputStream rsrStream;
    public FileToDb(Activity act){
        rsrStream = act.getResources().openRawResource(R.raw.initialrecords);
    }

    public FileToDb(InputStream stream){
        this.rsrStream = stream;
    }

    public void initialRecordInsertion(){
        initialRecordInsertion(readFile());
    }
    public void initialRecordInsertion( List<Map<String, Object>> list){
        for(Map<String, Object> m : list){
            DatabaseAccessor.upsert(m);
        }
    }

    private List<Map<String, Object>> readFile(){
        final String METHOD_TAG = "_READFILE";
        List<Map<String, Object>> list = new ArrayList<>();
        BufferedReader r = new BufferedReader(new InputStreamReader(rsrStream));

        try{
            String line;
            while((line = r.readLine()) != null){
                String [] val = line.split(";");
                if(val.length > 1){
                    Map<String, Object> map = new HashMap<>();
                    if(val.length == 3){
                        //Deal with image
                        map.put(DatabaseAccessor.TAG_IMAGE, val[2].trim());
                        Log.i(FileToDb.TAG + METHOD_TAG, "Record has three values");
                    }else if(val.length == 2){
                        map.put(DatabaseAccessor.TAG_IMAGE, "");
                        Log.i(FileToDb.TAG + METHOD_TAG, "Record has Two values");
                    }
                    map.put(DatabaseAccessor.TAG_ARTIST, val[0].trim());
                    map.put(DatabaseAccessor.TAG_TITLE, val[1].trim());
                    list.add(map);
                }
            }
            r.close();
        }catch(IOException ex){
            Log.e(FileToDb.TAG + METHOD_TAG, ex.getMessage());

        }finally{
            r = null;
        }



        return list;
    }



}
