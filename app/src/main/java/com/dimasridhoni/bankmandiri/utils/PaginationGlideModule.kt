/**
 * Created by Dimas Ridhoni on 6/25/21 11:12 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 6/25/21 11:11 PM
 */
package com.dimasridhoni.bankmandiri.utils

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

@GlideModule
class PaginationGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        builder.setDefaultRequestOptions(
            RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL) // cache both original & resized image
        )
    }
}