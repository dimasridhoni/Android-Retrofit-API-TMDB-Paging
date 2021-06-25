/**
 * Created by Dimas Ridhoni on 4/5/20 10:10 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 4/5/20 10:10 PM
 */
package com.dimasridhoni.bankmandiri.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Review {
    @SerializedName("author")
    @Expose
    var author: String? = null

    @SerializedName("content")
    @Expose
    var content: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("url")
    @Expose
    var url: String? = null
}