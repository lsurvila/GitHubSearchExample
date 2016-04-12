package com.lsurvila.githubsearchexample.data.db;

import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import rx.schedulers.Schedulers;

public class GitHubDb {

    private final BriteDatabase database;

    public GitHubDb(@NonNull SQLiteOpenHelper sqLiteOpenHelper) {
        SqlBrite sqlBrite = SqlBrite.create();
        database = sqlBrite.wrapDatabaseHelper(sqLiteOpenHelper, Schedulers.io());
    }

}
