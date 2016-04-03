package com.lsurvila.githubsearchexample.model;

public class GitHubRepo {

    private final String id;
    private final String repositoryName;
    private boolean isFavorite;

    public GitHubRepo(String id, String repositoryName) {
        this.id = id;
        this.repositoryName = repositoryName;
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
}
