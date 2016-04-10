package com.lsurvila.githubsearchexample.data;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.gson.Gson;
import com.lsurvila.githubsearchexample.AndroidUtils;
import com.lsurvila.githubsearchexample.data.network.model.Item;
import com.lsurvila.githubsearchexample.data.network.model.Repositories;
import com.lsurvila.githubsearchexample.model.GitHubRepo;
import com.lsurvila.githubsearchexample.model.GitHubRepoViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class ModelConverter {

    private final AndroidUtils androidUtils;
    private final Gson gson;

    public ModelConverter(@NonNull Gson gson, @NonNull AndroidUtils androidUtils) {
        this.gson = gson;
        this.androidUtils = androidUtils;
    }

    public GitHubRepoViewModel toViewModel(Response<ResponseBody> response) {
        String linkHeader = response.headers().get("Link");
        int lastPage = getLastPage(linkHeader);
        List<GitHubRepo> gitHubRepos = new ArrayList<>();
        try {
            Repositories repositories = gson.fromJson(response.body().string(), Repositories.class);
            gitHubRepos = toViewModel(gitHubRepos, repositories);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new GitHubRepoViewModel(gitHubRepos, lastPage);
    }

    // TODO test
    private List<GitHubRepo> toViewModel(List<GitHubRepo> gitHubRepos, Repositories repositories) {
        for (int i = 0; i < repositories.getItems().size(); i++) {
            Item item  = repositories.getItems().get(i);
            gitHubRepos.add(new GitHubRepo(item.getId(), item.getName(), item.getUrl()));
        }
        return gitHubRepos;
    }

    @VisibleForTesting
    int getLastPage(String linkHeader) {
        return Integer.parseInt(androidUtils.getQueryFromUrl(getLastPageUrl(linkHeader), "page"));
    }

    @VisibleForTesting
    String getLastPageUrl(String linkHeader) {
        String[] links = linkHeader.split(",");
        for (String link : links) {
            String[] parts = link.split(";");
            String urlString = parts[0].trim();
            urlString = urlString.substring(1, urlString.length() - 1);
            String rel = parts[1];
            if (rel.contains("last")) {
                return urlString;
            }
        }
        return null;
    }

}
