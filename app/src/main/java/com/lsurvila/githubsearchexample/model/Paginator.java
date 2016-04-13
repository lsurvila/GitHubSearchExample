package com.lsurvila.githubsearchexample.model;

public class Paginator {

    private static final int DEFAULT_PER_PAGE = 100;
    private static final int FIRST_PAGE = 1;
    private static final int INVALID_PAGE = -1;

    private int currentPage;
    private int lastPage;

    public int getNextPage() {
        if ((lastPage == 0 && currentPage == 0) || (currentPage < lastPage)) {
            return ++currentPage;
        } else {
            return INVALID_PAGE;
        }
    }

    public int getPerPage() {
        return DEFAULT_PER_PAGE;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public boolean isFirstPage() {
        return currentPage == FIRST_PAGE;
    }

    public boolean isValid() {
        return currentPage != INVALID_PAGE;
    }

    public void reset() {
        currentPage = 0;
        lastPage = 0;
    }

    public int resetAndGetNextPage() {
        reset();
        return getNextPage();
    }
}
