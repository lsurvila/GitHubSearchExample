package com.lsurvila.githubsearchexample.model;

import android.support.annotation.NonNull;

public class GitHubRepo {

    private final String id;
    private final String repositoryName;
    private boolean isFavorite;
    private final String url;

    public GitHubRepo(@NonNull String id, @NonNull String repositoryName, @NonNull String url) {
        this.id = id;
        this.repositoryName = repositoryName;
        this.url = url;
        this.isFavorite = false;
    }

    public String getId() {
        return id;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getUrl() {
        return url;
    }

}
