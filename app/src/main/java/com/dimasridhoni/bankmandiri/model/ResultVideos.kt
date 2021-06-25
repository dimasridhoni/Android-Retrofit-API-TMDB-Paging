/**
 * Created by Dimas Ridhoni on 6/25/21 3:14 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 6/25/21 3:14 PM
 */

package com.dimasridhoni.bankmandiri.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class ResultVideos {

    @SerializedName("results")
    @Expose
    var videos: List<Video> = ArrayList()

}