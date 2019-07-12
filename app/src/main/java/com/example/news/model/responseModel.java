package com.example.news.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class responseModel {

    @SerializedName("status")
    private String status;

    @SerializedName("articles")
    private ArrayList<newsModel> articles;


    public responseModel(String status, ArrayList<newsModel> articles) {
        this.status = status;
        this.articles = articles;
    }

    public ArrayList<newsModel> getArticles() {
        return articles;
    }

    public String getStatus() {
        return status;
    }
}
