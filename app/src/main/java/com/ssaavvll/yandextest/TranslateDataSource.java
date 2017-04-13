package com.ssaavvll.yandextest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SSAAV on 13.04.2017.
 */

/*public class TranslateDataSource {
    private SQLiteDatabase db;
    private HistorySQLiteHelper dbHelper;
    private String[] allColumns = {TranslateContract.History._ID +
            TranslateContract.History.COLUMN_NAME_TEXT_FROM +
            TranslateContract.History.COLUMN_NAME_TEXT_TO +
            TranslateContract.History.COLUMN_NAME_LANG_TO +
            TranslateContract.History.COLUMN_NAME_LANG_FROM +
            TranslateContract.History.COLUMN_NAME_FAVOURITE
    };
    public TranslateDataSource (Context context) {
        dbHelper = new HistorySQLiteHelper(context);
    }
    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }
    public void close() {
        dbHelper.close();
    }

    public TranslateItem createItem(String comment) {
        ContentValues values = new ContentValues();
        values.put(HistorySQLiteHelper.COLUMN_COMMENT, comment);
        long insertId = db.insert(TranslateContract.History.TABLE_NAME, null,
                values);
        Cursor cursor = db.query(TranslateContract.History.TABLE_NAME,
                allColumns, TranslateContract.History._ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        TranslateItem newItem = cursorToItem(cursor);
        cursor.close();
        return newItem;
    }

    public void deleteItem(TranslateItem translateItem) {
        long id = translateItem.getId();
        db.delete(TranslateContract.History.TABLE_NAME, TranslateContract.History._ID
                + " = " + id, null);
    }

    public List<TranslateItem> getAllItems() {
        List<TranslateItem> items = new ArrayList<TranslateItem>();

        Cursor cursor = db.query(TranslateContract.History.TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            TranslateItem comment = cursorToItem(cursor);
            items.add(comment);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return items;
    }

    private TranslateItem cursorToItem(Cursor cursor) {
        TranslateItem item = new TranslateItem();
        item.setId(cursor.getLong(0));
        item.setComment(cursor.getString(1));
        return item;
    }
}*/
