package com.ssaavvll.yandextest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SSAAV on 10.04.2017.
 */

public class HistorySQLiteHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "HistoryTranslate.db";

    private static final String SQL_CREATE_HISTORY =
            "CREATE TABLE " + TranslateContract.History.TABLE_NAME + " (" +
                    TranslateContract.History._ID + " INTEGER PRIMARY KEY," +
                    TranslateContract.History.COLUMN_NAME_TEXT_FROM + " TEXT," +
                    TranslateContract.History.COLUMN_NAME_TEXT_TO + " TEXT," +
                    TranslateContract.History.COLUMN_NAME_LANG_FROM + " TEXT," +
                    TranslateContract.History.COLUMN_NAME_LANG_TO + " TEXT," +
                    TranslateContract.History.COLUMN_NAME_FAVOURITE + " INTEGER)";

    private static final String SQL_DELETE_HISTORY =
            "DROP TABLE IF EXISTS " + TranslateContract.History.TABLE_NAME;

    public HistorySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_HISTORY);
        onCreate(db);
    }
}
