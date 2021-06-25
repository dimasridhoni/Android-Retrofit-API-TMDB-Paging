/**
 * Created by Dimas Ridhoni on 6/25/21 11:05 AM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 6/25/21 11:05 AM
 */
package com.dimasridhoni.bankmandiri.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class ResultGenres {
    @SerializedName("genres")
    @Expose
    var genres: List<Genre> = ArrayList()
}