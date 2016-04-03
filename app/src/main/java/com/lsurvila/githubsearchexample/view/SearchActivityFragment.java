package com.lsurvila.githubsearchexample.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lsurvila.githubsearchexample.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Allows to search GitHub and favorite repositories.
 */
public class SearchActivityFragment extends Fragment {

    @Bind(R.id.search_result_list)
    RecyclerView searchResultList;

    public SearchActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        searchResultList.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultList.setAdapter(new SearchResultAdapter());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
