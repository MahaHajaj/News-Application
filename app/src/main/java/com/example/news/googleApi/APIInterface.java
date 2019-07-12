package com.example.news.googleApi;

import com.example.news.model.responseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIInterface {

    @GET("top-headlines")
    Call<responseModel> getLatestNews(@Query("sources") String source, @Query("sortBy") String sortBy, @Query("apiKey") String apiKey);

}
