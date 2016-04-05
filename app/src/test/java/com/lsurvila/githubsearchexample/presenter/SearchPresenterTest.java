package com.lsurvila.githubsearchexample.presenter;

import com.lsurvila.githubsearchexample.AndroidUtils;
import com.lsurvila.githubsearchexample.R;
import com.lsurvila.githubsearchexample.data.GitHubDao;
import com.lsurvila.githubsearchexample.model.Paginator;
import com.lsurvila.githubsearchexample.model.GitHubRepo;
import com.lsurvila.githubsearchexample.model.GitHubRepoViewModel;
import com.lsurvila.githubsearchexample.view.SearchView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SearchPresenterTest {

    private SearchPresenter searchPresenter;

    @Mock
    private SearchView searchView;

    @Mock
    private GitHubDao gitHubDao;

    @Mock
    private AndroidUtils androidUtils;

    @Before
    public void setUp() throws Exception {
        searchPresenter = new SearchPresenter(searchView, gitHubDao, androidUtils, new Paginator());
        when(androidUtils.isStringEmpty(anyString())).thenReturn(false);
        when(androidUtils.isStringEmpty("")).thenReturn(true);
        when(androidUtils.isStringEmpty(null)).thenReturn(true);
    }

    @Test
    public void shouldShowAllFavorites_emptyQuery_getAllFavoritesSuccess() throws Exception {
        String searchQuery = "";
        GitHubRepoViewModel result = mockFavoriteResult();
        when(gitHubDao.getAllFavorites()).thenReturn(Observable.just(result));

        searchPresenter.search(searchQuery);

        verify(searchView).showResults(result.getGitHubRepos());
    }

    @Test
    public void shouldShowGenericError_emptyQuery_getAllFavoritesFail() throws Exception {
        String searchQuery = "";
        String genericError = "Error happened. Please try again.";
        when(gitHubDao.getAllFavorites()).thenReturn(Observable.error(new Throwable("Error while retrieving favorites.")));
        when(androidUtils.getString(R.string.error_generic)).thenReturn(genericError);

        searchPresenter.search(searchQuery);

        verify(searchView).showMessage(genericError);
    }

    @Test
    public void shouldShowSearchResultsIncludingFavorites_searchSuccess_getFavoritesSuccess() throws Exception {
        String searchQuery = "okhttp";
        GitHubRepoViewModel result = mockResult();
        GitHubRepoViewModel favorites = mockFavoriteResult();
        when(gitHubDao.search(eq(searchQuery), eq(1), anyInt())).thenReturn(Observable.just(result));
        when(gitHubDao.getFavorites(searchQuery)).thenReturn(Observable.just(favorites));

        searchPresenter.search(searchQuery);

        verify(searchView).showResults(result.getGitHubRepos());
        assertThat(result.getGitHubRepos().get(0).isFavorite()).isTrue();
        assertThat(result.getGitHubRepos().get(1).isFavorite()).isFalse();
        assertThat(result.getGitHubRepos().get(2).isFavorite()).isFalse();
        assertThat(result.getGitHubRepos().get(3).isFavorite()).isTrue();
    }

    @Test
    public void shouldShowSearchResultsAndNoError_searchSuccess_getFavoritesFail() throws Exception {
        String searchQuery = "okhttp";
        GitHubRepoViewModel result = mockResult();
        when(gitHubDao.search(eq(searchQuery), eq(1), anyInt())).thenReturn(Observable.just(result));
        when(gitHubDao.getFavorites(searchQuery)).thenReturn(Observable.error(new Throwable("Error")));

        searchPresenter.search(searchQuery);

        verify(searchView).showResults(result.getGitHubRepos());
        verify(searchView, never()).showMessage(anyString());
    }

    @Test
    public void shouldShowSearchResultsAndNoError_searchSuccess_getFavoritesEmpty() throws Exception {
        String searchQuery = "okhttp";
        GitHubRepoViewModel result = mockResult();
        when(gitHubDao.search(eq(searchQuery), eq(1), anyInt())).thenReturn(Observable.just(result));
        when(gitHubDao.getFavorites(searchQuery)).thenReturn(Observable.just(new GitHubRepoViewModel(new ArrayList<>(), 0)));

        searchPresenter.search(searchQuery);

        verify(searchView).showResults(result.getGitHubRepos());
        verify(searchView, never()).showMessage(anyString());
    }

    @Test
    public void shouldShowFavoritesAndGenericError_searchFail_getFavoritesSuccess() throws Exception {
        String searchQuery = "okhttp";
        GitHubRepoViewModel result = mockFavoriteResult();
        String genericError = "genericError";
        when(gitHubDao.search(eq(searchQuery), eq(1), anyInt())).thenReturn(Observable.error(new Throwable("Error.")));
        when(gitHubDao.getFavorites(searchQuery)).thenReturn(Observable.just(result));
        when(androidUtils.getString(R.string.error_generic)).thenReturn(genericError);

        searchPresenter.search(searchQuery);

        verify(searchView).showResults(result.getGitHubRepos());
        verify(searchView).showMessage(genericError);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldShowGenericError_searchFail_getFavoritesFail() throws Exception {
        String searchQuery = "okhttp";
        String genericError = "genericError";
        when(gitHubDao.search(eq(searchQuery), eq(1), anyInt())).thenReturn(Observable.error(new Throwable("Error.")));
        when(gitHubDao.getFavorites(searchQuery)).thenReturn(Observable.error(new Throwable("Error.")));
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
        when(gitHubDao.search(eq(searchQuery), eq(1), anyInt())).thenReturn(Observable.error(new Throwable("Error.")));
        when(gitHubDao.getFavorites(searchQuery)).thenReturn(Observable.just(new GitHubRepoViewModel(new ArrayList<>(), 0)));
        when(androidUtils.getString(R.string.error_generic)).thenReturn(genericError);

        searchPresenter.search(searchQuery);

        verify(searchView, never()).showResults(anyList());
        verify(searchView).showMessage(genericError);
    }

    @Test // rare case, when repository is removed, but was made favorite by user before, we still show them
    public void shouldShowFavoriteAndNoError_searchEmpty_getFavoritesSuccess() throws Exception {
        String searchQuery = "okhttp";
        GitHubRepoViewModel favorites = mockFavoriteResult();
        when(gitHubDao.search(eq(searchQuery), eq(1), anyInt())).thenReturn(Observable.just(new GitHubRepoViewModel(new ArrayList<>(), 0)));
        when(gitHubDao.getFavorites(searchQuery)).thenReturn(Observable.just(favorites));

        searchPresenter.search(searchQuery);

        verify(searchView, never()).showMessage(anyString());
        verify(searchView).showResults(favorites.getGitHubRepos());
    }


    @SuppressWarnings("unchecked")
    @Test
    public void shouldShowNotFoundError_searchEmpty_getFavoritesFail() throws Exception {
        String searchQuery = "okhttp";
        String notFoundError = "Not found error.";
        when(gitHubDao.search(eq(searchQuery), eq(1), anyInt())).thenReturn(Observable.just(new GitHubRepoViewModel(new ArrayList<>(), 0)));
        when(gitHubDao.getFavorites(searchQuery)).thenReturn(Observable.error(new Throwable("Error")));
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
        when(gitHubDao.search(eq(searchQuery), eq(1), anyInt())).thenReturn(Observable.just(new GitHubRepoViewModel(new ArrayList<>(), 0)));
        when(gitHubDao.getFavorites(searchQuery)).thenReturn(Observable.just(new GitHubRepoViewModel(new ArrayList<>(), 0)));
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
        verify(gitHubDao).saveFavorite(gitHubRepo);
    }

    @Test
    public void shouldRemoveFavorite() throws Exception {
        GitHubRepo gitHubRepo = new GitHubRepo("0", "okhttp");

        searchPresenter.removeFavorite(gitHubRepo);

        assertThat(gitHubRepo.isFavorite()).isFalse();
        verify(gitHubDao).removeFavorite(gitHubRepo);
    }

    @Test
    public void shouldAppendResults_searchCalledTwice_twoPages() throws Exception {
        String query = "okhttp";
        GitHubRepoViewModel result = mockResult();
        when(gitHubDao.search(eq(query), anyInt(), anyInt())).thenReturn(Observable.just(result));
        when(gitHubDao.getFavorites(query)).thenReturn(Observable.just(new GitHubRepoViewModel(new ArrayList<>(), 0)));

        searchPresenter.search(query);
        searchPresenter.searchNext(query);

        verify(searchView).appendResults(result.getGitHubRepos());
    }

    @Test
    public void shouldNotAppendResults_searchCalledTwice_onePage() throws Exception {
        String query = "okhttp";
        GitHubRepoViewModel result = mockResultOnePage();
        when(gitHubDao.search(eq(query), anyInt(), anyInt())).thenReturn(Observable.just(result));
        when(gitHubDao.getFavorites(query)).thenReturn(Observable.just(new GitHubRepoViewModel(new ArrayList<>(), 0)));

        searchPresenter.search(query);
        searchPresenter.searchNext(query);

        verify(searchView, never()).appendResults(result.getGitHubRepos());
    }

    private GitHubRepoViewModel mockResult() {
        List<GitHubRepo> gitHubRepos = new ArrayList<>();
        gitHubRepos.add(new GitHubRepo("1", "square/okhttp"));
        gitHubRepos.add(new GitHubRepo("2", "hongyangAndroid/okhttp-utils"));
        gitHubRepos.add(new GitHubRepo("3", "duzechao/OKHttpUtils"));
        gitHubRepos.add(new GitHubRepo("4", "kymjs/RxVolley"));
        return new GitHubRepoViewModel(gitHubRepos, 2);
    }

    private GitHubRepoViewModel mockResultOnePage() {
        List<GitHubRepo> gitHubRepos = new ArrayList<>();
        gitHubRepos.add(new GitHubRepo("1", "square/okhttp"));
        gitHubRepos.add(new GitHubRepo("2", "hongyangAndroid/okhttp-utils"));
        gitHubRepos.add(new GitHubRepo("3", "duzechao/OKHttpUtils"));
        gitHubRepos.add(new GitHubRepo("4", "kymjs/RxVolley"));
        return new GitHubRepoViewModel(gitHubRepos, 1);
    }

    private GitHubRepoViewModel mockFavoriteResult() {
        List<GitHubRepo> gitHubRepos = new ArrayList<>();
        gitHubRepos.add(new GitHubRepo("1", "square/okhttp"));
        gitHubRepos.add(new GitHubRepo("4", "kymjs/RxVolley"));
        gitHubRepos.get(0).setFavorite(true);
        gitHubRepos.get(1).setFavorite(true);
        return new GitHubRepoViewModel(gitHubRepos, 0);
    }

}