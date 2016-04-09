package com.lsurvila.githubsearchexample.presenter;

import android.support.annotation.NonNull;

import com.lsurvila.githubsearchexample.AndroidUtils;
import com.lsurvila.githubsearchexample.R;
import com.lsurvila.githubsearchexample.data.GitHubDao;
import com.lsurvila.githubsearchexample.model.Paginator;
import com.lsurvila.githubsearchexample.model.GitHubRepo;
import com.lsurvila.githubsearchexample.model.GitHubRepoViewModel;
import com.lsurvila.githubsearchexample.view.SearchView;

import java.util.ArrayList;

import rx.Observable;

class SearchPresenter {

    private final SearchView searchView;
    private final GitHubDao gitHubDao;
    private final AndroidUtils androidUtils;
    private final Paginator paginator;

    public SearchPresenter(@NonNull SearchView searchView, @NonNull GitHubDao gitHubDao,
                           @NonNull AndroidUtils androidUtils, @NonNull Paginator paginator) {
        this.searchView = searchView;
        this.gitHubDao = gitHubDao;
        this.androidUtils = androidUtils;
        this.paginator = paginator;
    }

    public void search(String query) {
        paginator.reset();
        search(query, paginator.getNextPage());
    }

    public void searchNext(String query) {
        search(query, paginator.getNextPage());
    }

    private void search(String query, int pageNumber) {
        if (!paginator.isPageInvalid()) {
            if (androidUtils.isStringEmpty(query)) {
                gitHubDao.getAllFavorites()
                        .subscribe(gitHubRepoViewModel -> searchView.showResults(gitHubRepoViewModel.getGitHubRepos()), throwable -> {
                            searchView.showMessage(androidUtils.getString(R.string.error_generic));
                        });
            } else {
                Observable.zip(gitHubDao.search(query, pageNumber, paginator.getPerPage()).onErrorResumeNext(throwable -> {
                    searchView.showMessage(androidUtils.getString(R.string.error_generic));
                    return Observable.just(new GitHubRepoViewModel(new ArrayList<>(), 0));
                }), gitHubDao.getFavorites(query).onErrorResumeNext(throwable1 -> {
                    return Observable.just(new GitHubRepoViewModel(new ArrayList<>(), 0));
                }), (searchResults, favorites) -> {
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
                })
                        .subscribe(searchResults -> {
                            if (searchResults.getGitHubRepos().size() == 0) {
                                searchView.showMessage(androidUtils.getString(R.string.error_not_found, query));
                            } else {
                                paginator.setLastPage(searchResults.getLastPage());
                                if (paginator.isFirstPage()) {
                                    searchView.showResults(searchResults.getGitHubRepos());
                                } else {
                                    searchView.appendResults(searchResults.getGitHubRepos());
                                }
                            }
                        }, Throwable::printStackTrace);
            }
        }
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
        searchView.openDetails(gitHubRepo.getUrl());
    }
}
