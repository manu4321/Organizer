package com.example.manuelalejandro.organizer;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.id;

/**
 * Created by manuelalejandro on 2016-10-04.
 */
public class DatabaseAccessor {

    static SQLiteDatabase db = null;
    static DatabaseOpenHelper dbHelper = null;

    public static String TAG_ID = "_id";
    public static String TAG_ARTIST = "artistName";
    public static String TAG_TITLE = "title";
    public static String TAG_IMAGE = "image";
    public static String TABLE_NAME = "organizer";
    final static String[] columns = {TAG_ID, TAG_ARTIST, TAG_TITLE, TAG_IMAGE};

    public static void importDatabase(){
        String dbName = findCopyDatabaseName(dbHelper.getContext().getDatabasePath(dbHelper.DB_NAME));
        List<Map<String, Object>> list = prepareImportDb(readDb(0));
        dbHelper = new DatabaseOpenHelper(dbHelper.getContext(), Environment.getExternalStorageDirectory() + "/organized/"+ dbName);
        db = dbHelper.getWritableDatabase();

        //do import
        bulkInsert(list);
        //back to normal
        db.close();
        dbHelper = new DatabaseOpenHelper(dbHelper.getContext());
        db = dbHelper.getWritableDatabase();
    }

    public static void bulkInsert(List<Map<String, Object>> list){
        for(Map<String, Object> map : list){
            upsert(map);
        }

    }
    public static void logFilesInPath(){
        File databasePath = new File(Environment.getExternalStorageDirectory() + "/organized/");
        Log.i("as", databasePath.getParentFile().getAbsolutePath());
        for(File f : databasePath.listFiles()){
            if(f.getName().toLowerCase().contains(dbHelper.getDatabaseName())){
                Log.i("Number of database ", f.getAbsolutePath());

            }
        }
    }

    private static String findCopyDatabaseName(File databasePath) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        Log.i("as", databasePath.getParentFile().getAbsolutePath());
        for(File f : databasePath.getParentFile().listFiles()){
            if(f.getName().toLowerCase().contains(dbHelper.getDatabaseName())){
                if(!map.containsKey(dbHelper.getDatabaseName())){
                    map.put(dbHelper.getDatabaseName(), 1);
                }else{
                    int count = map.get(dbHelper.getDatabaseName());
                    map.put(dbHelper.getDatabaseName(), ++count);

                }
            }
        }
        return dbHelper.getDatabaseName() + "_" + map.get(dbHelper.getDatabaseName());
    }

    private static List<Map<String, Object>> prepareImportDb(Cursor c){
        List<Map<String, Object>> list = new ArrayList();
        Map <String, Object> map = new HashMap<String, Object>();
        while(c.moveToNext()){
            map.put(DatabaseAccessor.TAG_ID, id);
            map.put(DatabaseAccessor.TAG_ARTIST, c.getString(c.getColumnIndexOrThrow(TAG_ARTIST)));
            map.put(DatabaseAccessor.TAG_TITLE, c.getString(c.getColumnIndexOrThrow(TAG_TITLE)));
            map.put(DatabaseAccessor.TAG_IMAGE, prepareImageIntoBlob(c.getString(c.getColumnIndexOrThrow(TAG_IMAGE))));
            list.add(map);
        }

        return list;
    }



    private static Object prepareImageIntoBlob(String imageUrl) {
        return ImageHelper.getByteArrayOfImage(imageUrl, dbHelper.getContext());
    }

    public static void upsert(Map<String, Object> map) {
        ContentValues values = new ContentValues();
        if (map.get(TAG_ID) != null) {

            values.put(TAG_ARTIST, (String) map.get(TAG_ARTIST));
            if(map.get(TAG_IMAGE) instanceof  String){
                values.put(TAG_IMAGE, (String) map.get(TAG_IMAGE));
            }else{
                values.put(TAG_IMAGE, (byte[]) map.get(TAG_IMAGE));
            }
            values.put(TAG_TITLE, (String)map.get(TAG_TITLE));
            String[] args = new String[]{(map.get(TAG_ID).toString())};
            db.update(TABLE_NAME, values, "_id=?", args);

        } else {
            values.put(TAG_ARTIST, (String) map.get(TAG_ARTIST));
            values.put(TAG_IMAGE, (String) map.get(TAG_IMAGE));
            values.put(TAG_TITLE, (String) map.get(TAG_TITLE));
            db.insert(TABLE_NAME, null, values);
        }

    }
    public static Cursor readDbLike(String search){
        String [] selectionArgs = {"%" + search + "%"};
        return db.query(TABLE_NAME, null, TAG_ARTIST + "||" + TAG_TITLE + " LIKE ?", selectionArgs, null, null,
                TAG_ARTIST + " ASC ");
    }
    public static void deleteItem(long id) {
        db.delete(TABLE_NAME, "_id=?", new String[]{id + ""});
    }

    public static Cursor readDb(int offset) {
/*        return db.query(TABLE_NAME, null, null, new String[] {}, null, null,
                TAG_ARTIST + " ASC  LIMIT 20 OFFSET " + offset);*/
        return db.query(TABLE_NAME, null, null, new String[]{}, null, null,
                TAG_ARTIST + " ASC ");
    }

    public static Map<String, Object> getRow(String id) {

        Cursor cursor = db.query(TABLE_NAME, columns, "_id = ?", new String[]{id}, null, null,
                null);
        Map<String, Object> map = new HashMap<String, Object>();
        if (cursor.moveToNext()) {
            map.put(DatabaseAccessor.TAG_ID, id);
            map.put(DatabaseAccessor.TAG_ARTIST, cursor.getString(cursor.getColumnIndexOrThrow(TAG_ARTIST)));
            map.put(DatabaseAccessor.TAG_TITLE, cursor.getString(cursor.getColumnIndexOrThrow(TAG_TITLE)));
            map.put(DatabaseAccessor.TAG_IMAGE, cursor.getString(cursor.getColumnIndexOrThrow(TAG_IMAGE)));
        }
        Log.i("DatabaseAccessor.GetRow", map.toString());
        return map;
    }

    public static void deleteAll() {
        db.delete(TABLE_NAME, null, null);
    }

    public static void saveDb(){

    }
}
