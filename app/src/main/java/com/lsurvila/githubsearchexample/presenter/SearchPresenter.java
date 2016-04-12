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

import java.io.InterruptedIOException;
import java.util.ArrayList;

import rx.Observable;

public class SearchPresenter {

    private static final String TAG = "SearchPresenter";

    private final GitHubSearchView mGitHubSearchView;
    private final GitHubDao gitHubDao;
    private final AndroidUtils androidUtils;
    private final Paginator paginator;

    private boolean isQueryEmpty = false;
    private String queryString;

    public SearchPresenter(@NonNull GitHubSearchView gitHubSearchView, @NonNull GitHubDao gitHubDao,
                           @NonNull AndroidUtils androidUtils, @NonNull Paginator paginator) {
        this.mGitHubSearchView = gitHubSearchView;
        this.gitHubDao = gitHubDao;
        this.androidUtils = androidUtils;
        this.paginator = paginator;
    }

    // TODO Critical
    // review error handling (not found vs other errors, also onError of subscribe should not be executed)

    // TODO Should have
    // add database layer
    // add favorite
    // remove favorite
    // test to convert network model
    // find out why thread interrupted error happens

    // TODO Nice to have
    // fix pagination for network requests
    // ui (espresso) tests
    // keep results on configuration change
    // progress dialogs
    // add animation for vector drawable
    // add pagination for db items

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
            return gitHubDao.getAllFavorites();
        } else {
            return Observable.zip(searchApi(query, page), searchDb(query), this::getMergedData);
        }
    }

    private void showError() {
        mGitHubSearchView.showMessage(androidUtils.getString(R.string.error_generic));
    }

    private void handleResult(GitHubRepoViewModel searchResults) {
        if (isQueryEmpty) {
                mGitHubSearchView.showResults(searchResults.getGitHubRepos());
        } else {
            if (searchResults.getGitHubRepos().size() == 0) {
                mGitHubSearchView.showMessage(androidUtils.getString(R.string.error_not_found, queryString));
            } else {
                paginator.setLastPage(searchResults.getLastPage());
                if (paginator.isFirstPage()) {
                    mGitHubSearchView.showResults(searchResults.getGitHubRepos());
                } else {
                    mGitHubSearchView.appendResults(searchResults.getGitHubRepos());
                }
            }
        }
    }

    private Observable<GitHubRepoViewModel> searchApi(String query, int page) {
        return gitHubDao.search(query, page, paginator.getPerPage())
                .onErrorResumeNext(throwable -> {
                    // TODO find out why it happens, filtering empty queries seems to fixes it, but
                    // TODO we would still like observable to execute for showing all favorites
                    // thread interrupted sometimes happens especially when removing all query fast,
                    // but this error does not affect UX, so for now just do not show error
                    if (!isThreadInterruptedException(throwable)) {
                        showError();
                    }
                    Log.e(TAG, "Error while searching api with query=" + query, throwable);
                    return getEmptyData();
                });
    }

    private boolean isThreadInterruptedException(Throwable throwable) {
        return throwable instanceof InterruptedIOException && throwable.getLocalizedMessage().equals("thread interrupted");
    }

    private Observable<GitHubRepoViewModel> searchDb(String query) {
        return gitHubDao.getFavorites(query).onErrorResumeNext(throwable -> {
            Log.e(TAG, "Error while searching db with query=" + query, throwable);
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

    public void saveFavorite(GitHubRepo gitHubRepo) {
        gitHubRepo.setFavorite(true);
        gitHubDao.saveFavorite(gitHubRepo);
    }

    public void removeFavorite(GitHubRepo gitHubRepo) {
        gitHubRepo.setFavorite(false);
        gitHubDao.removeFavorite(gitHubRepo);
    }

    public void requestDetails(GitHubRepo gitHubRepo) {
        mGitHubSearchView.openDetails(gitHubRepo.getUrl());
    }
}
