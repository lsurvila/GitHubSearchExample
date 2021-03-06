package com.lsurvila.githubsearchexample.presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.lsurvila.githubsearchexample.AndroidUtils;
import com.lsurvila.githubsearchexample.R;
import com.lsurvila.githubsearchexample.data.GitHubDao;
import com.lsurvila.githubsearchexample.model.Paginator;
import com.lsurvila.githubsearchexample.model.GitHubRepo;
import com.lsurvila.githubsearchexample.model.GitHubRepoViewModel;
import com.lsurvila.githubsearchexample.view.GitHubSearchView;

import java.util.ArrayList;

import rx.Observable;

public class SearchPresenter {

    private static final String TAG = "SearchPresenter";

    private final GitHubSearchView gitHubSearchView;
    private final GitHubDao gitHubDao;
    private final AndroidUtils androidUtils;
    private final Paginator paginator;

    private boolean isQueryEmpty = false;
    private String queryString;

    public SearchPresenter(@NonNull GitHubSearchView gitHubSearchView, @NonNull GitHubDao gitHubDao,
                           @NonNull AndroidUtils androidUtils, @NonNull Paginator paginator) {
        this.gitHubSearchView = gitHubSearchView;
        this.gitHubDao = gitHubDao;
        this.androidUtils = androidUtils;
        this.paginator = paginator;
    }

    public void search(Observable<CharSequence> queryChanges) {
        searchInPage(queryChanges, paginator.resetAndGetNextPage());
    }

    public void searchMore() {
        searchInPage(Observable.just(queryString), paginator.getNextPage());
    }

    private void searchInPage(Observable<CharSequence> queryChanges, int page) {
        queryChanges.map(CharSequence::toString)
                // concatMap ensures ordering of observables (flatMap does not
                // care about order)
                .concatMap(queryString -> performSearch(queryString, page))
                .subscribeOn(androidUtils.getRunningThread())
                .observeOn(androidUtils.getMainThread())
                .onErrorResumeNext(throwable -> {
                    Log.e(TAG, "Unexpected error while searching. Should not happen, queries will continue", throwable);
                    showError();
                    return Observable.empty();
                })
                .subscribe(this::handleResult, throwable -> {
                    showError();
                    Log.e(TAG, "Error happened onError of Subscriber. Should not happen, queries will stop", throwable);
                });
    }

    private Observable<GitHubRepoViewModel> performSearch(String query, int page) {
        queryString = query;
        isQueryEmpty = AndroidUtils.isEmpty(query);
        if (isQueryEmpty) {
            return searchDb();
        } else {
            return Observable.zip(searchApi(query, page), searchDb(), this::getMergedData);
        }
    }

    private void showError() {
        gitHubSearchView.showMessage(androidUtils.getString(R.string.error_generic));
    }

    private void handleResult(GitHubRepoViewModel searchResults) {
        if (isQueryEmpty) {
                gitHubSearchView.showResults(searchResults.getGitHubRepos());
        } else {
            if (searchResults.getGitHubRepos().size() == 0) {
                gitHubSearchView.showMessage(androidUtils.getString(R.string.error_not_found, queryString));
            } else {
                paginator.setLastPage(searchResults.getLastPage());
                if (paginator.isFirstPage()) {
                    gitHubSearchView.showResults(searchResults.getGitHubRepos());
                } else {
                    gitHubSearchView.appendResults(searchResults.getGitHubRepos());
                }
            }
        }
    }

    private Observable<GitHubRepoViewModel> searchApi(String query, int page) {
        return gitHubDao.search(query, page, paginator.getPerPage())
                .onErrorResumeNext(throwable -> {
                    Log.e(TAG, "Error while searching api with query=" + query, throwable);
                    showError();
                    return getEmptyData();
                });
    }

    private Observable<GitHubRepoViewModel> searchDb() {
        return gitHubDao.getAllFavorites().onErrorResumeNext(throwable -> {
            Log.e(TAG, "Error while querying db", throwable);
            showError();
            return getEmptyData();
        });
    }

    private GitHubRepoViewModel getMergedData(GitHubRepoViewModel searchResults, GitHubRepoViewModel favorites) {
        for (int i = 0; i < searchResults.getGitHubRepos().size(); i++) {
            for (int j = 0; j < favorites.getGitHubRepos().size(); j++) {
                if (searchResults.getGitHubRepos().get(i).getId().equals(favorites.getGitHubRepos().get(j).getId())) {
                    searchResults.getGitHubRepos().get(i).setFavorite(true);
                }
            }
        }
        if (searchResults.getGitHubRepos().size() > 0) {
            return searchResults;
        } else {
            return favorites;
        }
    }

    private Observable<GitHubRepoViewModel> getEmptyData() {
        return Observable.just(new GitHubRepoViewModel(new ArrayList<>(), 0));
    }

    public void saveFavorite(GitHubRepo gitHubRepo, int position) {
        gitHubDao.saveFavorite(gitHubRepo)
            .subscribeOn(androidUtils.getRunningThread())
            .observeOn(androidUtils.getMainThread())
            .subscribe(success -> {
                if (success) {
                    gitHubRepo.setFavorite(true);
                    gitHubSearchView.invalidateView(position);
                }
            }, Throwable::printStackTrace);
    }

    public void removeFavorite(GitHubRepo gitHubRepo, int position) {
        gitHubDao.removeFavorite(gitHubRepo)
                .subscribeOn(androidUtils.getRunningThread())
                .observeOn(androidUtils.getMainThread())
                .subscribe(success -> {
                    gitHubRepo.setFavorite(false);
                    gitHubSearchView.invalidateView(position);
                }, Throwable::printStackTrace);
    }

    public void requestDetails(GitHubRepo gitHubRepo) {
        gitHubSearchView.openDetails(gitHubRepo.getUrl());
    }
}
