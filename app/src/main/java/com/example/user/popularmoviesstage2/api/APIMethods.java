package com.example.user.popularmoviesstage2.api;

import com.example.user.popularmoviesstage2.model.Movie;
import com.example.user.popularmoviesstage2.model.MovieList;
import com.example.user.popularmoviesstage2.model.ReviewList;
import com.example.user.popularmoviesstage2.model.VideoList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Andris on 017 17.02.18.
 */

public interface APIMethods {
    @GET("movie/{sort_by}")
    Call<MovieList> getMovieList(@Path("sort_by") String sortBy,
                                 @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<ReviewList> getReviews(@Path("id") String id,
                                @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<VideoList> getVideo(@Path("id") String id,
                             @Query("api_key") String apiKey);
    //for some reasons this link works only when "/3/" added
    //TODO find out why its like that
    @GET("movie/{id}")
    Call<Movie> getMovie(@Path("id") String id,
                           @Query("api_key") String apiKey);

}
