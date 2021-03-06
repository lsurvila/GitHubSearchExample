package com.lsurvila.githubsearchexample.data;

import android.support.annotation.NonNull;

import com.lsurvila.githubsearchexample.data.db.GitHubDb;
import com.lsurvila.githubsearchexample.data.network.GitHubApi;
import com.lsurvila.githubsearchexample.model.GitHubRepo;
import com.lsurvila.githubsearchexample.model.GitHubRepoViewModel;

import rx.Observable;
import rx.schedulers.Schedulers;

public class GitHubDao {

    private final GitHubApi api;
    private final GitHubDb db;
    private final ModelConverter modelConverter;

    public GitHubDao(@NonNull GitHubApi api, @NonNull GitHubDb db, @NonNull ModelConverter modelConverter) {
        this.api = api;
        this.db = db;
        this.modelConverter = modelConverter;
    }

    public Observable<GitHubRepoViewModel> search(String searchQuery, int pageNumber, int perPage) {
        return api.search(searchQuery, pageNumber, perPage)
                .map(modelConverter::toViewModel)
                .subscribeOn(Schedulers.io());
    }

    public Observable<Boolean> saveFavorite(GitHubRepo gitHubRepo) {
        return db.insert(modelConverter.toContentValues(gitHubRepo));
    }

    public Observable<GitHubRepoViewModel> getAllFavorites() {
        return db.queryAll()
                .map(modelConverter::toViewModel);
    }

    public Observable<Boolean> removeFavorite(GitHubRepo gitHubRepo) {
        return db.delete(gitHubRepo.getId());
    }

}
