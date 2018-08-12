package com.georgcantor.wallpaperapp.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "favorite_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Favorite.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Favorite.TABLE_NAME);
        onCreate(db);
    }

    public long insertToFavorites(String imageUrl, String hdUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Favorite.COLUMN_URL, imageUrl);
        values.put(Favorite.COLUMN_HD_URL, hdUrl);

        long id = db.insert(Favorite.TABLE_NAME, null, values);
        db.close();

        return id;
    }

    public List<Favorite> getAllFavorites() {
        List<Favorite> favoriteList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Favorite.TABLE_NAME + " ORDER BY " +
                Favorite.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Favorite favorite = new Favorite();
                favorite.setId(cursor.getInt(cursor.getColumnIndex(Favorite.COLUMN_ID)));
                favorite.setImageUrl(cursor.getString(cursor.getColumnIndex(Favorite.COLUMN_URL)));
                favorite.setHdUrl(cursor.getString(cursor.getColumnIndex(Favorite.COLUMN_HD_URL)));
                favorite.setTimestamp(cursor.getString(cursor.getColumnIndex(Favorite.COLUMN_TIMESTAMP)));

                favoriteList.add(favorite);
            } while (cursor.moveToNext());
        }
        db.close();
        return favoriteList;
    }

    public int getHistoryCount() {
        String countQuery = "SELECT  * FROM " + Favorite.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public void deleteFromFavorites(String imageUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Favorite.TABLE_NAME, Favorite.COLUMN_URL + " = ?",
                new String[]{imageUrl});
        db.close();
    }

    public boolean containFav(String imageUrl) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT  * FROM " + Favorite.TABLE_NAME + " WHERE "
                + Favorite.COLUMN_URL + " = " + DatabaseUtils.sqlEscapeString(imageUrl);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToNext()) {
            cursor.close();
            return true;
        }
        return false;
    }
}

