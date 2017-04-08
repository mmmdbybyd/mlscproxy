package android.lovefantasy.mlscproxy.Tools;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lovefantasy on 17-4-3.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    Context mContext=null;
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table pattern (name text,time realt,tx real,rx real,ltx real ,lrx real,primary key(name))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists pattern");
        db.execSQL("create table pattern (name text,time real,tx real,rx real,ltx real ,lrx real,primary key(name))");

    }
}
