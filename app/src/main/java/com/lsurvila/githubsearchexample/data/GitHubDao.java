package com.lsurvila.githubsearchexample.data;

import android.support.annotation.NonNull;

import com.lsurvila.githubsearchexample.data.network.GitHubApi;
import com.lsurvila.githubsearchexample.model.GitHubRepo;
import com.lsurvila.githubsearchexample.model.GitHubRepoViewModel;

import rx.Observable;

public class GitHubDao {

    private final GitHubApi api;
    private final ModelConverter modelConverter;

    public GitHubDao(@NonNull GitHubApi api, @NonNull ModelConverter modelConverter) {
        this.api = api;
        this.modelConverter = modelConverter;
    }

    public Observable<GitHubRepoViewModel> search(String searchQuery, int pageNumber, int perPage) {
        return api.search(searchQuery, pageNumber, perPage)
                .map(modelConverter::toViewModel);
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
