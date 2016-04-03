package com.lsurvila.githubsearchexample.presenter;

import com.lsurvila.githubsearchexample.AndroidUtils;
import com.lsurvila.githubsearchexample.R;
import com.lsurvila.githubsearchexample.data.GitHubRepository;
import com.lsurvila.githubsearchexample.model.GitHubRepo;
import com.lsurvila.githubsearchexample.view.SearchView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

public class SearchPresenter {

    private SearchView searchView;
    private GitHubRepository gitHubRepository;
    private AndroidUtils androidUtils;

    public SearchPresenter(SearchView searchView, GitHubRepository gitHubRepository, AndroidUtils androidUtils) {
        this.searchView = searchView;
        this.gitHubRepository = gitHubRepository;
        this.androidUtils = androidUtils;
    }

    public void search(String query) {
        if (androidUtils.isStringEmpty(query)) {
            gitHubRepository.getAllFavorites()
                    .subscribe(gitHubRepos -> {
                        searchView.showResults(gitHubRepos);
                    }, throwable -> {
                        searchView.showSnackbar(androidUtils.getString(R.string.error_favorites_failed));
                    });
        } else {
            Observable.zip(gitHubRepository.search(query).onErrorResumeNext(throwable -> {
                searchView.showSnackbar(androidUtils.getString(R.string.error_generic));
                return Observable.just(new ArrayList<>());
            }), gitHubRepository.getFavorites(query), (searchResults, favorites) -> {
                for (int i = 0; i < searchResults.size(); i++) {
                    for (int j = 0; j < favorites.size(); j++) {
                        if (searchResults.get(i).getId().equals(favorites.get(j).getId())) {
                            searchResults.get(i).setFavorite(true);
                        }
                    }
                }
                if (searchResults.size() > 0) {
                    return searchResults;
                } else {
                    return favorites;
                }
            })
                    .subscribe(searchResults -> {
                        if (searchResults.size() == 0) {
                            searchView.showSnackbar(androidUtils.getString(R.string.error_not_found, query));
                        } else {
                            searchView.showResults(searchResults);
                        }
                    }, Throwable::printStackTrace);
        }
    }

    public void saveFavorite(GitHubRepo gitHubRepo) {
        gitHubRepo.setFavorite(true);
        gitHubRepository.saveFavorite(gitHubRepo);
    }
}
