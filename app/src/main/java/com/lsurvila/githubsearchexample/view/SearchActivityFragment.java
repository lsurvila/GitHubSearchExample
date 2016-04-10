package com.lsurvila.githubsearchexample.view;

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
public class SearchActivityFragment extends Fragment implements GithubSearchView {

    @SuppressWarnings("WeakerAccess")
    @Bind(R.id.search_result_list)
    RecyclerView searchResultList;
    @SuppressWarnings("WeakerAccess")
    @Bind(R.id.search_root_view)
    CoordinatorLayout searchRootView;

    private SearchPresenter presenter;
    private SearchResultAdapter adapter;

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
        GitHubDao gitHubDao = new GitHubDao(gitHubApi, modelConverter);
        presenter = new SearchPresenter(this, gitHubDao, androidUtils, paginator);
        adapter = new SearchResultAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        searchResultList.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultList.setAdapter(adapter);
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
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
