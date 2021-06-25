/**
 * Created by Dimas Ridhoni on 4/5/20 10:25 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 4/5/20 10:25 PM
 */
package com.dimasridhoni.bankmandiri.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class ResultReview {
    @SerializedName("results")
    @Expose
    var reviews: List<Review> = ArrayList()
}