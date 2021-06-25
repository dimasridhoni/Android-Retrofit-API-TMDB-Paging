/**
 * Created by Dimas Ridhoni on 6/25/21 3:27 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 6/25/21 3:27 PM
 */
package com.dimasridhoni.bankmandiri.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.dimasridhoni.bankmandiri.R
import com.dimasridhoni.bankmandiri.model.Genre

class SpinnerAdapterGenre() : BaseAdapter() {

    private var activity: Activity? = null
    private var inflater: LayoutInflater? = null
    private var item: List<Genre>? = null

    constructor(activity: Activity?, item: List<Genre>?) : this() {
        this.activity = activity
        this.item = item
    }
    override fun getCount(): Int {
        return item!!.size
    }

    override fun getItem(location: Int): Any? {
        return item!![location]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        if (inflater == null) inflater = activity
            ?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (convertView == null) convertView = inflater!!.inflate(R.layout.spinner_genre_list, null)
        val tvGenre = convertView!!.findViewById<View>(R.id.genre_name) as TextView
        val genre: Genre
        genre = item!![position]
        tvGenre.text = genre.name
        return convertView
    }


}