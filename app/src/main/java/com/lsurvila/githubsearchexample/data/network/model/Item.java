package com.lsurvila.githubsearchexample.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

    @Expose
    private String id;

    @Expose
    @SerializedName("full_name")
    private String name;

    @Expose
    @SerializedName("html_url")
    private String url;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

}
