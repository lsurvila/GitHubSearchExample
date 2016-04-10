package com.lsurvila.githubsearchexample.data.network;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

interface GitHubService {

    @GET("search/repositories")
    Observable<Response<ResponseBody>> search(@Query("q") String query, @Query("page") int page, @Query("per_page") int perPage);

}
