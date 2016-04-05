package com.lsurvila.githubsearchexample.view;

import com.lsurvila.githubsearchexample.model.GitHubRepo;

import java.util.List;

public interface SearchView {

    void showResults(List<GitHubRepo> gitHubRepos);
    void appendResults(List<GitHubRepo> gitHubRepos);
    void showMessage(String errorMessage);
    void openDetails(String url);

}
