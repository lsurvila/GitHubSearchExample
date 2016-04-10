package com.lsurvila.githubsearchexample.data;

import android.support.annotation.NonNull;

import com.lsurvila.githubsearchexample.data.network.GitHubApi;
import com.lsurvila.githubsearchexample.model.GitHubRepo;
import com.lsurvila.githubsearchexample.model.GitHubRepoViewModel;

import java.util.ArrayList;

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
        return Observable.just(new GitHubRepoViewModel(new ArrayList<>(), 0));
    }

    public Observable<GitHubRepoViewModel> getFavorites(String query) {
        return Observable.just(new GitHubRepoViewModel(new ArrayList<>(), 0));
    }

    public void removeFavorite(GitHubRepo gitHubRepo) {

    }

}
