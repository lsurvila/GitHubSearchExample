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
    public void shouldShowAllFavorites_emptyQuery_getAllFavoritesSuccess() throws Exception {
        String searchQuery = "";
        List<GitHubRepo> result = mockFavoriteResult();
        when(gitHubRepository.getAllFavorites()).thenReturn(Observable.just(result));

        searchPresenter.search(searchQuery);

        verify(searchView).showResults(result);
    }

    @Test
    public void shouldShowGenericError_emptyQuery_getAllFavoritesFail() throws Exception {
        String searchQuery = "";
        String genericError = "Error happened. Please try again.";
        when(gitHubRepository.getAllFavorites()).thenReturn(Observable.error(new Throwable("Error while retrieving favorites.")));
        when(androidUtils.getString(R.string.error_generic)).thenReturn(genericError);

        searchPresenter.search(searchQuery);

        verify(searchView).showMessage(genericError);
    }

    @Test
    public void shouldShowSearchResultsIncludingFavorites_searchSuccess_getFavoritesSuccess() throws Exception {
        String searchQuery = "okhttp";
        List<GitHubRepo> result = mockResult();
        List<GitHubRepo> favorites = mockFavoriteResult();
        when(gitHubRepository.search(searchQuery)).thenReturn(Observable.just(result));
        when(gitHubRepository.getFavorites(searchQuery)).thenReturn(Observable.just(favorites));

        searchPresenter.search(searchQuery);

        verify(searchView).showResults(result);
        assertThat(result.get(0).isFavorite()).isTrue();
        assertThat(result.get(1).isFavorite()).isFalse();
        assertThat(result.get(2).isFavorite()).isFalse();
        assertThat(result.get(3).isFavorite()).isTrue();
    }

    @Test
    public void shouldShowSearchResultsAndNoError_searchSuccess_getFavoritesFail() throws Exception {
        String searchQuery = "okhttp";
        List<GitHubRepo> result = mockResult();
        when(gitHubRepository.search(searchQuery)).thenReturn(Observable.just(result));
        when(gitHubRepository.getFavorites(searchQuery)).thenReturn(Observable.error(new Throwable("Error")));

        searchPresenter.search(searchQuery);

        verify(searchView).showResults(result);
        verify(searchView, never()).showMessage(anyString());
    }

    @Test
    public void shouldShowSearchResultsAndNoError_searchSuccess_getFavoritesEmpty() throws Exception {
        String searchQuery = "okhttp";
        List<GitHubRepo> result = mockResult();
        when(gitHubRepository.search(searchQuery)).thenReturn(Observable.just(result));
        when(gitHubRepository.getFavorites(searchQuery)).thenReturn(Observable.just(new ArrayList<>()));

        searchPresenter.search(searchQuery);

        verify(searchView).showResults(result);
        verify(searchView, never()).showMessage(anyString());
    }

    @Test
    public void shouldShowFavoritesAndGenericError_searchFail_getFavoritesSuccess() throws Exception {
        String searchQuery = "okhttp";
        List<GitHubRepo> result = mockFavoriteResult();
        String genericError = "genericError";
        when(gitHubRepository.search(searchQuery)).thenReturn(Observable.error(new Throwable("Error.")));
        when(gitHubRepository.getFavorites(searchQuery)).thenReturn(Observable.just(result));
        when(androidUtils.getString(R.string.error_generic)).thenReturn(genericError);

        searchPresenter.search(searchQuery);

        verify(searchView).showResults(result);
        verify(searchView).showMessage(genericError);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldShowGenericError_searchFail_getFavoritesFail() throws Exception {
        String searchQuery = "okhttp";
        String genericError = "genericError";
        when(gitHubRepository.search(searchQuery)).thenReturn(Observable.error(new Throwable("Error.")));
        when(gitHubRepository.getFavorites(searchQuery)).thenReturn(Observable.error(new Throwable("Error.")));
        when(androidUtils.getString(R.string.error_generic)).thenReturn(genericError);

        searchPresenter.search(searchQuery);

        verify(searchView, never()).showResults(anyList());
        verify(searchView).showMessage(genericError);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldShowGenericError_searchFail_getFavoritesEmpty() throws Exception {
        String searchQuery = "okhttp";
        String genericError = "genericError";
        when(gitHubRepository.search(searchQuery)).thenReturn(Observable.error(new Throwable("Error.")));
        when(gitHubRepository.getFavorites(searchQuery)).thenReturn(Observable.just(new ArrayList<>()));
        when(androidUtils.getString(R.string.error_generic)).thenReturn(genericError);

        searchPresenter.search(searchQuery);

        verify(searchView, never()).showResults(anyList());
        verify(searchView).showMessage(genericError);
    }

    @Test // rare case, when repository is removed, but was made favorite by user before, we still show them
    public void shouldShowFavoriteAndNoError_searchEmpty_getFavoritesSuccess() throws Exception {
        String searchQuery = "okhttp";
        List<GitHubRepo> favorites = mockFavoriteResult();
        when(gitHubRepository.search(searchQuery)).thenReturn(Observable.just(new ArrayList<>()));
        when(gitHubRepository.getFavorites(searchQuery)).thenReturn(Observable.just(favorites));

        searchPresenter.search(searchQuery);

        verify(searchView, never()).showMessage(anyString());
        verify(searchView).showResults(favorites);
    }


    @SuppressWarnings("unchecked")
    @Test
    public void shouldShowNotFoundError_searchEmpty_getFavoritesFail() throws Exception {
        String searchQuery = "okhttp";
        String notFoundError = "Not found error.";
        when(gitHubRepository.search(searchQuery)).thenReturn(Observable.just(new ArrayList<>()));
        when(gitHubRepository.getFavorites(searchQuery)).thenReturn(Observable.error(new Throwable("Error")));
        when(androidUtils.getString(R.string.error_not_found, searchQuery)).thenReturn(notFoundError);

        searchPresenter.search(searchQuery);

        verify(searchView).showMessage(notFoundError);
        verify(searchView, never()).showResults(anyList());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldShowNotFoundError_searchEmpty_getFavoritesEmpty() throws Exception {
        String searchQuery = "okhttp";
        String notFoundError = "Not found error.";
        when(gitHubRepository.search(searchQuery)).thenReturn(Observable.just(new ArrayList<>()));
        when(gitHubRepository.getFavorites(searchQuery)).thenReturn(Observable.just(new ArrayList<>()));
        when(androidUtils.getString(R.string.error_not_found, searchQuery)).thenReturn(notFoundError);

        searchPresenter.search(searchQuery);

        verify(searchView).showMessage(notFoundError);
        verify(searchView, never()).showResults(anyList());
    }


    @Test
    public void shouldSaveFavorite() throws Exception {
        GitHubRepo gitHubRepo = new GitHubRepo("0", "okhttp");

        searchPresenter.saveFavorite(gitHubRepo);

        assertThat(gitHubRepo.isFavorite()).isTrue();
        verify(gitHubRepository).saveFavorite(gitHubRepo);
    }

    @Test
    public void shouldRemoveFavorite() throws Exception {
        GitHubRepo gitHubRepo = new GitHubRepo("0", "okhttp");

        searchPresenter.removeFavorite(gitHubRepo);

        assertThat(gitHubRepo.isFavorite()).isFalse();
        verify(gitHubRepository).removeFavorite(gitHubRepo);
    }

    @Test
    public void shouldAppendResultsOfNextPage() throws Exception {
        String query = "okhttp";
        List<GitHubRepo> results = mockResult();
        when(gitHubRepository.search(query)).thenReturn(Observable.just(results));
        when(gitHubRepository.getFavorites(query)).thenReturn(Observable.just(new ArrayList<>()));

        searchPresenter.search(query);

        verify(searchView).showResults(results);
        assertThat(searchPresenter.currentPage).isEqualTo(0);

        // user scrolls at the bottom of screen, then another search query is executed

        List<GitHubRepo> resultsFromOtherPage = mockResult();
        when(gitHubRepository.search(query)).thenReturn(Observable.just(resultsFromOtherPage));

        searchPresenter.searchNextPage(query);

        verify(searchView).appendResults(resultsFromOtherPage);
        assertThat(searchPresenter.currentPage).isEqualTo(1);
    }

    // TODO paging if no more pages
    // TODO reset paging
    // TODO click on item

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