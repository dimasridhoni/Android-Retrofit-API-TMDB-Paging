/**
 * Created by Dimas Ridhoni on 6/25/21 10:44 AM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 6/25/21 10:44 AM
 */
package com.dimasridhoni.bankmandiri.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Genre {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null
}