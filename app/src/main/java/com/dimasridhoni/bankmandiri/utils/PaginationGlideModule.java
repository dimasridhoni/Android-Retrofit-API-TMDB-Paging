/**
 * Created by Dimas Ridhoni on 6/25/21 11:12 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 6/25/21 11:11 PM
 */

package com.dimasridhoni.bankmandiri.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

@GlideModule
public class PaginationGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        super.applyOptions(context, builder);
        builder.setDefaultRequestOptions(
                new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL) // cache both original & resized image
        );
    }

}
