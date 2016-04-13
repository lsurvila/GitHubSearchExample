package com.lsurvila.githubsearchexample.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.lsurvila.githubsearchexample.data.db.model.FavoriteEntry;

import rx.Observable;

public class GitHubDb {

    private final SQLiteOpenHelper database;


    public GitHubDb(@NonNull SQLiteOpenHelper sqLiteOpenHelper) {
        this.database = sqLiteOpenHelper;
    }

    public Observable<Boolean> insert(ContentValues contentValues) {
        return Observable.create((Observable.OnSubscribe<Boolean>) subscriber -> {
            try {
                long rowId = database.getWritableDatabase().insert(FavoriteEntry.TABLE_NAME, null, contentValues);
                subscriber.onNext(rowId != -1);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
                e.printStackTrace();
            }
        });
    }

    public Observable<Boolean> delete(String id) {
        return Observable.create((Observable.OnSubscribe<Boolean>) subscriber -> {
            try {
                int result = database.getWritableDatabase().delete(FavoriteEntry.TABLE_NAME, FavoriteEntry.COLUMN_ID + "=?", new String[] { id });
                subscriber.onNext(result > 0);
                subscriber.onCompleted();
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError(e);
            }

        });
    }

    public Observable<Cursor> queryAll() {
        return Observable.create((Observable.OnSubscribe<Cursor>) subscriber -> {
            try {
                Cursor cursor = database.getReadableDatabase().rawQuery("SELECT * FROM " + FavoriteEntry.TABLE_NAME, null);
                subscriber.onNext(cursor);
                subscriber.onCompleted();
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        });
    }

}
