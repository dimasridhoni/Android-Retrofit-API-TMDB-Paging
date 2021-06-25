/**
 * Created by Dimas Ridhoni on 6/25/21 11:51 AM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 6/25/21 9:59 AM
 */

/**
 * Created by Dimas Ridhoni on 4/5/20 10:31 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 4/5/20 10:31 PM
 */
package com.dimasridhoni.bankmandiri.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dimasridhoni.bankmandiri.R
import com.dimasridhoni.bankmandiri.model.Review
import com.dimasridhoni.bankmandiri.utils.PaginationAdapterCallback
import java.util.*

class PaginationAdapterReview internal constructor(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var reviewResults: MutableList<Review>?
    private var isLoadingAdded = false
    private var retryPageLoad = false
    private val mCallback: PaginationAdapterCallback
    private var errorMsg: String? = null
    fun getReviewResults(): List<Review>? {
        return reviewResults
    }

    fun setReviewResults(reviewResults: MutableList<Review>?) {
        this.reviewResults = reviewResults
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)
        val viewItem = inflater.inflate(R.layout.item_review_list, parent, false)
        viewHolder = ReviewVH(viewItem)
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val review = reviewResults!![position] // Genre
        val reviewVH = holder as ReviewVH
        reviewVH.author.text = review.author
        reviewVH.content.text = review.content
    }

    override fun getItemCount(): Int {
        return if (reviewResults == null) 0 else reviewResults!!.size
    }

    override fun getItemViewType(position: Int): Int {
        println("Position : $position")
        println("Review Result Size : " + reviewResults!!.size)
        return if (position == reviewResults!!.size - 1 && isLoadingAdded) LOADING else ITEM
    }
    /*
        Helpers - bind Views
   _________________________________________________________________________________________________
    */
    /**
     * @param movie
     * @return [releasedate] | [2letterlangcode]
     */
    /**
     * Using Glide to handle image loading.
     * Learn more about Glide here:
     * [](http://blog.grafixartist.com/image-gallery-app-android-studio-1-4-glide/)
     *
     *
     * //     * @param posterPath from [Movie.getPosterPath]
     *
     * @return Glide builder
     */
    fun add(r: Review) {
        reviewResults!!.add(r)
        notifyItemInserted(reviewResults!!.size - 1)
    }

    fun addAll(reviewResults: List<Review>) {
        for (review in reviewResults) {
            add(review)
        }
    }

    fun remove(r: Review?) {
        val position = reviewResults!!.indexOf(r)
        if (position > -1) {
            reviewResults!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        isLoadingAdded = false
        while (itemCount > 0) {
            remove(getItem(0))
        }
    }

    val isEmpty: Boolean
        get() = itemCount == 0

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(Review())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position = reviewResults!!.size - 1
        val review = getItem(position)
        if (review != null) {
            reviewResults!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun getItem(position: Int): Review {
        return reviewResults!![position]
    }

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    fun showRetry(show: Boolean, errorMsg: String?) {
        retryPageLoad = show
        notifyItemChanged(reviewResults!!.size - 1)
        if (errorMsg != null) this.errorMsg = errorMsg
    }
    /*
   View Holders
   _________________________________________________________________________________________________
    */
    /**
     * Header ViewHolder
     */
    protected inner class ReviewVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val author: TextView
        val content: TextView

        init {
            author = itemView.findViewById(R.id.author)
            content = itemView.findViewById(R.id.content)
        }
    }

    companion object {
        // View Types
        private const val ITEM = 0
        private const val LOADING = 1
    }

    init {
        mCallback = context as PaginationAdapterCallback
        reviewResults = ArrayList()
    }
}