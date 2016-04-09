package com.lsurvila.githubsearchexample.data.network;

import com.lsurvila.githubsearchexample.data.network.model.GitHubService;

import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class GitHubApi {

    private static final String BASE_URL = "https://api.github.com/";
    private final GitHubService service;

    public GitHubApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(GitHubService.class);
    }

    public Observable<Response> search(String query, int page, int perPage) {
        return service.search(query, page, perPage);
    }

}