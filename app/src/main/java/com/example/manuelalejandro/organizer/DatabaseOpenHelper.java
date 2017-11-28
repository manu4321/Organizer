package com.example.manuelalejandro.organizer;

        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;

        import java.io.File;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    final private static String CREATE_CMD =
            "CREATE TABLE organizer (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "artistName text, title text, image text)";

    final public static String DB_NAME = "organizer_db";

    final private static Integer VERSION = 1;
    final private Context context;
    public DatabaseOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.context = context;
    }

    public DatabaseOpenHelper(Context context, String db){
        super(context, db, null, VERSION);
        getWritableExternalDatabase(db);
        this.context = context;
    }
    public void filesInPath(){
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

    void deleteDatabase ( ) {
        context.deleteDatabase(DB_NAME);
    }

    public Context getContext() {
        return context;
    }
    public SQLiteDatabase getWritableExternalDatabase(String db){
        return SQLiteDatabase.openOrCreateDatabase(db, null,null);
    }
}
