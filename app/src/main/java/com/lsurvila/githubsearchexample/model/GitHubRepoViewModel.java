package com.lsurvila.githubsearchexample.model;

import java.util.List;

public class GitHubRepoViewModel {

    private final List<GitHubRepo> gitHubRepos;
    private final int lastPage;

    public GitHubRepoViewModel(List<GitHubRepo> gitHubRepos, int lastPage) {
        this.gitHubRepos = gitHubRepos;
        this.lastPage = lastPage;
    }

    public List<GitHubRepo> getGitHubRepos() {
        return gitHubRepos;
    }

    public int getLastPage() {
        return lastPage;
    }

}
