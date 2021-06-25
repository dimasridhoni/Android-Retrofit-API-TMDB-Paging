/**
 * Created by Dimas Ridhoni on 6/25/21 11:16 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 6/25/21 11:16 PM
 */
package com.dimasridhoni.bankmandiri.api

import com.dimasridhoni.bankmandiri.model.ResultGenres
import com.dimasridhoni.bankmandiri.model.ResultMovies
import com.dimasridhoni.bankmandiri.model.ResultReview
import com.dimasridhoni.bankmandiri.model.ResultVideos
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {
    @GET("discover/movie")
    fun getDiscoverMovies(
        @Query("api_key") apiKey: String?,
        @Query("page") pageIndex: Int
    ): Call<ResultMovies>

    @GET("discover/movie")
    fun getDiscoverMoviesByGenre(
        @Query("api_key") apiKey: String?,
        @Query("page") pageIndex: Int,
        @Query("with_genres") with_genres: Int?
    ): Call<ResultMovies>

    @GET("movie/{id}")
    fun getDetailMovie(
        @Query("api_key") apiKey: String?,
        @Field("id") id: Int
    ): Call<ResultMovies>

    @GET("movie/{id}/reviews")
    fun getReviewMovie(
        @Path("id") id: Int?,
        @Query("api_key") apiKey: String?,
        @Query("page") pageIndex: Int
    ): Call<ResultReview>

    @GET("movie/{id}/videos")
    fun getVideoMovie(
        @Path("id") id: Int?,
        @Query("api_key") apiKey: String?,
        @Query("page") pageIndex: Int
    ): Call<ResultVideos>

    @GET("genre/movie/list")
    fun getGenres(
        @Query("api_key") apiKey: String?,
        @Query("page") pageIndex: Int
    ): Call<ResultGenres>
}