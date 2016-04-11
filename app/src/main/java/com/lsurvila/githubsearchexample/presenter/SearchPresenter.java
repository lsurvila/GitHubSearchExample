package com.lsurvila.githubsearchexample.presenter;

import android.support.annotation.NonNull;

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

    // TODO pagination
    public void search(Observable<CharSequence> queryChanges) {
        queryChanges.map(CharSequence::toString)
                // concatMap ensures ordering of observables (flatMap does not
                // care about order)
                .concatMap(this::performSearch)
                .subscribeOn(androidUtils.getRunningThread())
                .observeOn(androidUtils.getMainThread())
                .subscribe(this::handleResult, throwable -> {
                    // TODO should not happen, as will stop producing queries, implement onErrorNext
                    showError();
                });
    }

    private Observable<GitHubRepoViewModel> performSearch(String query) {
        paginator.reset();
        queryString = query;
        isQueryEmpty = AndroidUtils.isEmpty(query);
        int page = Paginator.FIRST_PAGE;
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
                mGitHubSearchView.showResults(searchResults.getGitHubRepos());
            }
        }
    }

    private Observable<GitHubRepoViewModel> searchApi(String query, int page) {
        return gitHubDao.search(query, page, paginator.getPerPage())
                .onErrorResumeNext(throwable -> {
                    showError();
                    return getEmptyData();
                });
    }

    private Observable<GitHubRepoViewModel> searchDb(String query) {
        return gitHubDao.getFavorites(query).onErrorResumeNext(getEmptyData());
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
