package com.lsurvila.githubsearchexample.presenter;

import com.lsurvila.githubsearchexample.AndroidUtils;
import com.lsurvila.githubsearchexample.R;
import com.lsurvila.githubsearchexample.data.GitHubRepository;
import com.lsurvila.githubsearchexample.model.GitHubRepo;
import com.lsurvila.githubsearchexample.view.SearchView;

import java.util.ArrayList;

import rx.Observable;

public class SearchPresenter {

    private final SearchView searchView;
    private final GitHubRepository gitHubRepository;
    private final AndroidUtils androidUtils;

    public SearchPresenter(SearchView searchView, GitHubRepository gitHubRepository, AndroidUtils androidUtils) {
        this.searchView = searchView;
        this.gitHubRepository = gitHubRepository;
        this.androidUtils = androidUtils;
    }

    public void search(String query) {
        if (androidUtils.isStringEmpty(query)) {
            gitHubRepository.getAllFavorites()
                    .subscribe(searchView::showResults, throwable -> {
                        searchView.showMessage(androidUtils.getString(R.string.error_generic));
                    });
        } else {
            Observable.zip(gitHubRepository.search(query).onErrorResumeNext(throwable -> {
                searchView.showMessage(androidUtils.getString(R.string.error_generic));
                return Observable.just(new ArrayList<>());
            }), gitHubRepository.getFavorites(query).onErrorResumeNext(throwable1 -> {
                return Observable.just(new ArrayList<>());
            }), (searchResults, favorites) -> {
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
                            searchView.showMessage(androidUtils.getString(R.string.error_not_found, query));
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
