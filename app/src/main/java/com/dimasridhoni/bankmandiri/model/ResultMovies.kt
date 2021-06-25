/**
 * Created by Dimas Ridhoni on 6/25/21 11:31 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 6/25/21 11:31 PM
 */
package com.dimasridhoni.bankmandiri.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class ResultMovies {
    @SerializedName("page")
    @Expose
    var page: Int? = null

    @SerializedName("results")
    @Expose
    var movies: List<Movie> = ArrayList()

    @SerializedName("total_results")
    @Expose
    var totalResults: Int? = null

    @SerializedName("total_pages")
    @Expose
    var totalPages: Int? = null
}