package com.lsurvila.githubsearchexample.view;

import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.lsurvila.githubsearchexample.R;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;

// TODO Nice to have
// unsubscribe sql insert/delete observables on destroy
// increase test coverage
// fix pagination for network requests, also unsubscribe on destroy
// open details in webview activity
// ui (espresso) tests
// sqlbrite implementation
// keep results on configuration change
// progress dialogs
// add pagination for db items
// hide toolbar while scrolling

public class SearchActivity extends RxAppCompatActivity {

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
                // debounce will emit item only 300ms after last item is emitted
                // (after user types in last character)
                .debounce(300, TimeUnit.MILLISECONDS)
                // as network operations can be lengthy, onBackpressureLatest will
                // ensure that items won’t be emitted faster than a subscriber can
                // consume (will emit last item as soon as subscriber is able to
                // consume)
                .onBackpressureLatest()
                .subscribeOn(AndroidSchedulers.mainThread())
                // will stop when Activity is destroyed
                .compose(RxLifecycle.bindUntilEvent(lifecycle(), ActivityEvent.DESTROY)));
        return true;
    }

}
