package com.lsurvila.githubsearchexample.data;

import com.lsurvila.githubsearchexample.model.GitHubRepo;

import java.util.List;

import rx.Observable;

public class GitHubRepository {

    public Observable<List<GitHubRepo>> search(String searchQuery) {
        return null;
    }

    public void saveFavorite(GitHubRepo gitHubRepo) {

    }

    public Observable<List<GitHubRepo>> getAllFavorites() {
        return null;
    }

    public Observable<List<GitHubRepo>> getFavorites(String query) {
        return null;
    }
}
