package com.lsurvila.githubsearchexample.data.network.model;

import okhttp3.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface GitHubService {

    @GET("search/repositories")
    Observable<Response> search(@Query("q") String query, @Query("page") int page, @Query("per_page") int perPage);

}
