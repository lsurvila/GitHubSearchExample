package com.lsurvila.githubsearchexample.data.db.model;

import android.provider.BaseColumns;

public interface FavoriteEntry extends BaseColumns {

    String TABLE_NAME = "favorite";
    String COLUMN_ID = "id";
    String COLUMN_TITLE = "title";
    String COLUMN_URL = "url";

}
