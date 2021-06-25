/**
 * Created by Dimas Ridhoni on 6/25/21 3:52 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 6/25/21 3:52 PM
 */

package com.dimasridhoni.bankmandiri.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Video {

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("key")
    @Expose
    var key: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("site")
    @Expose
    var site: String? = null

}