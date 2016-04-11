package com.lsurvila.githubsearchexample.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.lsurvila.githubsearchexample.R;

import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;

public class SearchActivity extends AppCompatActivity {

    private SearchActivityFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupActionBar();
        fragment = (SearchActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setIconifiedByDefault(false);
        fragment.setQueryChangeListener(RxSearchView.queryTextChanges(searchView)
                // to prevent making requests too fast (as user may type fast),
                // throttleLast will emit last item during 100ms from the time
                // first item is emitted
                .throttleLast(100, TimeUnit.MILLISECONDS)
                // debounce will emit item only 200ms after last item is emitted
                // (after user types in last character)
                .debounce(200, TimeUnit.MILLISECONDS)
                // as network operations can be lengthy, onBackpressureLatest will
                // ensure that items won’t be emitted faster than a subscriber can
                // consume (will emit last item as soon as subscriber is able to
                // consume)
                .onBackpressureLatest()
                .subscribeOn(AndroidSchedulers.mainThread()));
        return true;
    }

}
