package com.lsurvila.githubsearchexample.data;

import com.lsurvila.githubsearchexample.model.GitHubRepo;
import com.lsurvila.githubsearchexample.model.GitHubRepoViewModel;

import rx.Observable;

public class GitHubDao {

    public Observable<GitHubRepoViewModel> search(String searchQuery, int pageNumber) {
        return null;
    }

    public void saveFavorite(GitHubRepo gitHubRepo) {

    }

    public Observable<GitHubRepoViewModel> getAllFavorites() {
        return null;
    }

    public Observable<GitHubRepoViewModel> getFavorites(String query) {
        return null;
    }

    public void removeFavorite(GitHubRepo gitHubRepo) {

    }

}
