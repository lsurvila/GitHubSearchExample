package com.lsurvila.githubsearchexample.data.network.model;

import com.google.gson.annotations.Expose;

import java.util.List;

public class Repositories {

    @Expose
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }
}
