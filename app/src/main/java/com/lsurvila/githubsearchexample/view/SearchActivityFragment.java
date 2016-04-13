package com.lsurvila.githubsearchexample.view;

import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.lsurvila.githubsearchexample.AndroidUtils;
import com.lsurvila.githubsearchexample.R;
import com.lsurvila.githubsearchexample.data.GitHubDao;
import com.lsurvila.githubsearchexample.data.ModelConverter;
import com.lsurvila.githubsearchexample.data.db.FavoritesDbHelper;
import com.lsurvila.githubsearchexample.data.db.GitHubDb;
import com.lsurvila.githubsearchexample.data.network.GitHubApi;
import com.lsurvila.githubsearchexample.model.GitHubRepo;
import com.lsurvila.githubsearchexample.model.Paginator;
import com.lsurvila.githubsearchexample.presenter.SearchPresenter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;

/**
 * Allows to search GitHub and favorite repositories.
 */
public class SearchActivityFragment extends Fragment implements GitHubSearchView {

    @Bind(R.id.search_result_list)
    RecyclerView searchResultList;

    @Bind(R.id.search_root_view)
    CoordinatorLayout searchRootView;

    private int previousTotal = 0; // The total number of items in the data set after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.

    private SearchPresenter presenter;
    private SearchResultAdapter adapter;
    private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            final int visibleItemCount = recyclerView.getChildCount();
            final int totalItemCount = layoutManager.getItemCount();
            final int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            final int visibleThreshold = 20;
            if (!loading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem + visibleThreshold)) {
                // End has been reached

                // TODO enable and fix later
                // presenter.searchMore();

                loading = true;
            }
        }
    };
    private LinearLayoutManager layoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
    }

    /**
     * Initializes and injects all classes we will need for this Fragment.
     * If app had more screens and classes that could be reused, they would be initialized in bigger
     * scope, for example in Application singleton.
     */
    private void injectDependencies() {
        Paginator paginator = new Paginator();
        AndroidUtils androidUtils = new AndroidUtils(getResources());
        Gson gson = new Gson();
        ModelConverter modelConverter = new ModelConverter(gson, androidUtils);
        GitHubApi gitHubApi = new GitHubApi();
        SQLiteOpenHelper favoritesDbHelper = new FavoritesDbHelper(getContext());
        GitHubDb db = new GitHubDb(favoritesDbHelper);
        GitHubDao gitHubDao = new GitHubDao(gitHubApi, db, modelConverter);
        presenter = new SearchPresenter(this, gitHubDao, androidUtils, paginator);
        adapter = new SearchResultAdapter();
        adapter.setOnItemClickListener((parent, view, position, id) -> showMessage(adapter.getItem(position).getRepositoryName()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        layoutManager = new LinearLayoutManager(getContext());
        searchResultList.setLayoutManager(layoutManager);
        searchResultList.setAdapter(adapter);
        searchResultList.addOnScrollListener(onScrollListener);
        return view;
    }

    public void setQueryChangeListener(Observable<CharSequence> queryTextChanges) {
        presenter.search(queryTextChanges);
    }

    @Override
    public void showResults(List<GitHubRepo> gitHubRepos) {
        adapter.setItems(gitHubRepos);
    }

    @Override
    public void appendResults(List<GitHubRepo> gitHubRepos) {
        adapter.appendItems(gitHubRepos);
    }

    @Override
    public void showMessage(String errorMessage) {
        Snackbar.make(searchRootView, errorMessage, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void openDetails(String url) {

    }

    @Override
    public void onDestroyView() {
        searchResultList.removeOnScrollListener(onScrollListener);
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

}
