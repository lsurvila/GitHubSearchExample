package com.lsurvila.githubsearchexample.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.gson.Gson;
import com.lsurvila.githubsearchexample.AndroidUtils;
import com.lsurvila.githubsearchexample.data.db.model.FavoriteEntry;
import com.lsurvila.githubsearchexample.data.network.model.Item;
import com.lsurvila.githubsearchexample.data.network.model.Repositories;
import com.lsurvila.githubsearchexample.model.GitHubRepo;
import com.lsurvila.githubsearchexample.model.GitHubRepoViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class ModelConverter {

    private final AndroidUtils androidUtils;
    private final Gson gson;

    public ModelConverter(@NonNull Gson gson, @NonNull AndroidUtils androidUtils) {
        this.gson = gson;
        this.androidUtils = androidUtils;
    }

    public GitHubRepoViewModel toViewModel(Response<ResponseBody> response) {
        String linkHeader = response.headers().get("Link");
        int lastPage = getLastPage(linkHeader);
        List<GitHubRepo> gitHubRepos = new ArrayList<>();
        try {
            Repositories repositories = gson.fromJson(response.body().string(), Repositories.class);
            gitHubRepos = toViewModel(gitHubRepos, repositories);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new GitHubRepoViewModel(gitHubRepos, lastPage);
    }

    private List<GitHubRepo> toViewModel(List<GitHubRepo> gitHubRepos, Repositories repositories) {
        for (int i = 0; i < repositories.getItems().size(); i++) {
            Item item  = repositories.getItems().get(i);
            gitHubRepos.add(new GitHubRepo(item.getId(), item.getName(), item.getUrl()));
        }
        return gitHubRepos;
    }

    @VisibleForTesting
    int getLastPage(String linkHeader) {
        if (!AndroidUtils.isEmpty(linkHeader)) {
            return Integer.parseInt(androidUtils.getQueryFromUrl(getLastPageUrl(linkHeader), "page"));
        } else {
            return 1;
        }
    }

    @VisibleForTesting
    String getLastPageUrl(String linkHeader) {
        String[] links = linkHeader.split(",");
        for (String link : links) {
            String[] parts = link.split(";");
            String urlString = parts[0].trim();
            urlString = urlString.substring(1, urlString.length() - 1);
            String rel = parts[1];
            if (rel.contains("last")) {
                return urlString;
            }
        }
        return null;
    }

    public GitHubRepoViewModel toViewModel(final Cursor cursor) {
        List<GitHubRepo> gitHubRepos = new ArrayList<>();
        GitHubRepoViewModel gitHubRepoViewModel = new GitHubRepoViewModel(gitHubRepos, 1);
        if (cursor.moveToFirst()) {
            while(!cursor.isAfterLast()) {
                gitHubRepos.add(toGitHubRepo(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return gitHubRepoViewModel;
    }

    private GitHubRepo toGitHubRepo(Cursor cursor) {
        String id = getStringFromCursor(cursor, FavoriteEntry.COLUMN_ID);
        String name = getStringFromCursor(cursor, FavoriteEntry.COLUMN_TITLE);
        String url = getStringFromCursor(cursor, FavoriteEntry.COLUMN_URL);
        return new GitHubRepo(id, name, url);
    }

    private String getStringFromCursor(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    public ContentValues toContentValues(GitHubRepo repo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoriteEntry.COLUMN_ID, repo.getId());
        contentValues.put(FavoriteEntry.COLUMN_TITLE, repo.getRepositoryName());
        contentValues.put(FavoriteEntry.COLUMN_URL, repo.getUrl());
        return contentValues;
    }

}
