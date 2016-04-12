package com.lsurvila.githubsearchexample.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.lsurvila.githubsearchexample.data.db.model.FavoriteEntry;

public class FavoritesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_FAVORITE_TABLE = "CREATE TABLE "
            + FavoriteEntry.TABLE_NAME + " ("
            + FavoriteEntry._ID + " INTEGER PRIMARY KEY,"
            + FavoriteEntry.COLUMN_ID + " TEXT,"
            + FavoriteEntry.COLUMN_TITLE + " TEXT,"
            + FavoriteEntry.COLUMN_URL + " TEXT )";

    public FavoritesDbHelper(@NonNull Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
