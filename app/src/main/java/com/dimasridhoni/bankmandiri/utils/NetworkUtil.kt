/**
 * Created by Dimas Ridhoni on 6/25/21 11:12 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 6/25/21 11:11 PM
 */
package com.dimasridhoni.bankmandiri.utils

import android.content.Context
import android.net.ConnectivityManager

object NetworkUtil {
    fun hasNetwork(context: Context): Boolean {
        var isConnected = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) isConnected = true
        return isConnected
    }
}