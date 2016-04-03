package com.lsurvila.githubsearchexample.presenter;

import com.lsurvila.githubsearchexample.AndroidUtils;
import com.lsurvila.githubsearchexample.R;
import com.lsurvila.githubsearchexample.data.GitHubRepository;
import com.lsurvila.githubsearchexample.model.GitHubRepo;
import com.lsurvila.githubsearchexample.view.SearchView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
// TODO go through all cases again
// query empty, not empty
// search success, fail, empty
// favorite success, fail, empty

// remove favorite
// paging
public class SearchPresenterTest {

    private SearchPresenter searchPresenter;

    @Mock
    private SearchView searchView;

    @Mock
    private GitHubRepository gitHubRepository;

    @Mock
    private AndroidUtils androidUtils;

    @Before
    public void setUp() throws Exception {
        searchPresenter = new SearchPresenter(searchView, gitHubRepository, androidUtils);
        when(androidUtils.isStringEmpty(anyString())).thenReturn(false);
        when(androidUtils.isStringEmpty("")).thenReturn(true);
        when(androidUtils.isStringEmpty(null)).thenReturn(true);
    }

    @Test
    public void shouldShowResultsWhenSearchIsSuccessful() throws Exception {
        String searchQuery = "okhttp";
        List<GitHubRepo> result = mockResult();
        when(gitHubRepository.search(searchQuery)).thenReturn(Observable.just(result));
        when(gitHubRepository.getFavorites(searchQuery)).thenReturn(Observable.just(new ArrayList<>()));

        searchPresenter.search(searchQuery);

        verify(searchView).showResults(result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldShowGenericErrorWhenSearchIsFailed() throws Exception {
        String searchQuery = "okhttp";
        String errorMessage = "Error from server.";
        String genericError = "Generic error to show for user";
        when(gitHubRepository.search(searchQuery)).thenReturn(Observable.error(new Throwable(errorMessage)));
        when(androidUtils.getString(R.string.error_generic)).thenReturn(genericError);

        searchPresenter.search(searchQuery);

        verify(searchView).showSnackbar(genericError);
        verify(searchView, never()).showResults(anyList());
    }

    @Test
    public void shouldShowOnlyFavoritesWhenQueryIsEmpty() throws Exception {
        String searchQuery = "";
        List<GitHubRepo> favoriteResults = mockFavoriteResult();
        when(gitHubRepository.getAllFavorites()).thenReturn(Observable.just(favoriteResults));

        searchPresenter.search(searchQuery);

        verify(searchView).showResults(favoriteResults);
    }

    @Test
    public void shouldClearResultsWhenThereAreNoFavoritesAndQueryIsEmpty() throws Exception {
        String searchQuery = "";
        List<GitHubRepo> emptyFavoritesResults = new ArrayList<>();
        when(gitHubRepository.getAllFavorites()).thenReturn(Observable.just(emptyFavoritesResults));

        searchPresenter.search(searchQuery);

        verify(searchView).showResults(emptyFavoritesResults);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldShowErrorIfRetrievingFavoritesFailed() throws Exception {
        String searchQuery = "";
        String error = "Failed to retrieve favorites. Try to restart app.";
        when(gitHubRepository.getAllFavorites()).thenReturn(Observable.error(new Throwable("Error occurred.")));
        when(androidUtils.getString(R.string.error_favorites_failed)).thenReturn(error);

        searchPresenter.search(searchQuery);

        verify(searchView).showSnackbar(error);
        verify(searchView, never()).showResults(anyList());
    }

    @Test
    public void shouldSaveFavorite() throws Exception {
        GitHubRepo gitHubRepo = new GitHubRepo("0", "okhttp");

        searchPresenter.saveFavorite(gitHubRepo);

        assertThat(gitHubRepo.isFavorite()).isTrue();
        verify(gitHubRepository).saveFavorite(gitHubRepo);
    }

    // TODO search (no favorites among search results) +++
    // TODO search + favorites (favorites are among search results) +++
    // TODO favorites (search has no results)
    // TODO remove favorite
    // TODO paging

    @Test
    public void shouldShowResultsIncludingFavorites() throws Exception {
        String searchQuery = "okhttp";
        List<GitHubRepo> results = mockResult();
        List<GitHubRepo> favoriteResults = mockFavoriteResult();
        when(gitHubRepository.search(searchQuery)).thenReturn(Observable.just(results));
        when(gitHubRepository.getFavorites(searchQuery)).thenReturn(Observable.just(favoriteResults));

        searchPresenter.search(searchQuery);

        verify(searchView).showResults(results);
        assertThat(results.get(0).isFavorite()).isTrue();
        assertThat(results.get(1).isFavorite()).isFalse();
        assertThat(results.get(2).isFavorite()).isFalse();
        assertThat(results.get(3).isFavorite()).isTrue();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldShowNotFoundErrorIfSearchAndFavoritesHaveNoResults() throws Exception {
        String searchQuery = "okxml";
        String notFoundError = "We couldn’t find any repositories matching 'okxml'";
        when(gitHubRepository.search(searchQuery)).thenReturn(Observable.just(new ArrayList<>()));
        when(gitHubRepository.getFavorites(searchQuery)).thenReturn(Observable.just(new ArrayList<>()));
        when(androidUtils.getString(R.string.error_not_found, searchQuery)).thenReturn(notFoundError);

        searchPresenter.search(searchQuery);

        verify(searchView).showSnackbar(notFoundError);
        verify(searchView, never()).showResults(anyList());
    }

    @Test
    public void shouldShowFavoriteIfSearchHasNoResults() throws Exception {
        String searchQuery = "okhttp";
        List<GitHubRepo> favorites = mockFavoriteResult();
        when(gitHubRepository.search(searchQuery)).thenReturn(Observable.just(new ArrayList<>()));
        when(gitHubRepository.getFavorites(searchQuery)).thenReturn(Observable.just(favorites));

        searchPresenter.search(searchQuery);

        verify(searchView).showResults(favorites);
        verify(searchView, never()).showSnackbar(anyString());
    }

    @Test
    public void shouldShowFavoriteIfSearchHasFailed() throws Exception {
        String searchQuery = "okhttp";
        List<GitHubRepo> favorites = mockFavoriteResult();
        when(gitHubRepository.search(searchQuery)).thenReturn(Observable.error(new Throwable("Error occurred.")));
        when(gitHubRepository.getFavorites(searchQuery)).thenReturn(Observable.just(favorites));

        searchPresenter.search(searchQuery);

        verify(searchView).showResults(favorites);
        verify(searchView, never()).showSnackbar(anyString());

    }

    private List<GitHubRepo> mockResult() {
        List<GitHubRepo> gitHubRepos = new ArrayList<>();
        gitHubRepos.add(new GitHubRepo("1", "square/okhttp"));
        gitHubRepos.add(new GitHubRepo("2", "hongyangAndroid/okhttp-utils"));
        gitHubRepos.add(new GitHubRepo("3", "duzechao/OKHttpUtils"));
        gitHubRepos.add(new GitHubRepo("4", "kymjs/RxVolley"));
        return gitHubRepos;
    }

    private List<GitHubRepo> mockFavoriteResult() {
        List<GitHubRepo> gitHubRepos = new ArrayList<>();
        gitHubRepos.add(new GitHubRepo("1", "square/okhttp"));
        gitHubRepos.add(new GitHubRepo("4", "kymjs/RxVolley"));
        gitHubRepos.get(0).setFavorite(true);
        gitHubRepos.get(1).setFavorite(true);
        return gitHubRepos;
    }

}