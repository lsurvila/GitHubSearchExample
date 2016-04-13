package com.lsurvila.githubsearchexample.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.lsurvila.githubsearchexample.data.db.model.FavoriteEntry;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import rx.Observable;
import rx.schedulers.Schedulers;

public class GitHubDb {

    private final BriteDatabase database;

    public GitHubDb(@NonNull SQLiteOpenHelper sqLiteOpenHelper) {
        SqlBrite sqlBrite = SqlBrite.create();
        this.database = sqlBrite.wrapDatabaseHelper(sqLiteOpenHelper, Schedulers.io());
    }

    public void insert(ContentValues contentValues) {
        database.insert(FavoriteEntry.TABLE_NAME, contentValues);
    }

    public Observable<Cursor> queryAll() {
        // TODO after this is executed whole subscription will stop, investigate
        return database.createQuery(FavoriteEntry.TABLE_NAME, "SELECT * FROM " + FavoriteEntry.TABLE_NAME)
                .map(SqlBrite.Query::run);
    }

}
