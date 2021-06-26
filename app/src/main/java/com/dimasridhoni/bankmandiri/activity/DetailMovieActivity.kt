/**
 * Created by Dimas Ridhoni on 6/25/21 10:33 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 6/25/21 10:33 PM
 */
package com.dimasridhoni.bankmandiri.activity

import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.bumptech.glide.Glide
import com.dimasridhoni.bankmandiri.R
import com.dimasridhoni.bankmandiri.adapter.PaginationAdapterReview
import com.dimasridhoni.bankmandiri.api.MovieApi.getClient
import com.dimasridhoni.bankmandiri.api.MovieService
import com.dimasridhoni.bankmandiri.model.ResultReview
import com.dimasridhoni.bankmandiri.model.ResultVideos
import com.dimasridhoni.bankmandiri.model.Review
import com.dimasridhoni.bankmandiri.model.Video
import com.dimasridhoni.bankmandiri.utils.PaginationAdapterCallback
import com.dimasridhoni.bankmandiri.utils.PaginationScrollListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeoutException

class DetailMovieActivity : AppCompatActivity(), PaginationAdapterCallback {

    private var movieService: MovieService? = null
    private var mMovieTitle: TextView? = null
    private var mMovieDesc: TextView? = null
    private var mYear: TextView? = null
    private var mPosterImg: ImageView? = null
    var id: Int? = null
    private var title: String? = null
    private var overview: String? = null
    private var image: String? = null
    var adapter: PaginationAdapterReview? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var rv: RecyclerView? = null
    var progressBar: ProgressBar? = null
    var errorLayout: LinearLayout? = null
    var btnRetry: Button? = null
    var txtError: TextView? = null
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var isLoading = false
    private var isLastPage = false
    private var currentPage = PAGE_START

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_movie)
        //init service and load data
        movieService = getClient(this)!!.create(MovieService::class.java)
        mMovieTitle = findViewById(R.id.movie_title)
        mMovieDesc = findViewById(R.id.movie_desc)
        mYear = findViewById(R.id.movie_year)
        mPosterImg = findViewById(R.id.movie_poster)
        val i = intent
        id = i.getIntExtra("id", 0)
        title = i.getStringExtra("title")
        overview = i.getStringExtra("overview")
        image = i.getStringExtra("image")
        loadImage(image ?: "xbSuFiJbbBWCkyCCKIMfuDCA4yV.jpg")
        mMovieTitle?.setText(title)
        mMovieDesc?.setText(overview)
        rv = findViewById(R.id.main_recycler)
        progressBar = findViewById(R.id.main_progress)
        errorLayout = findViewById(R.id.error_layout)
        btnRetry = findViewById(R.id.error_btn_retry)
        txtError = findViewById(R.id.error_txt_cause)
        swipeRefreshLayout = findViewById(R.id.main_swiperefresh)
        adapter = PaginationAdapterReview(this)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv?.setLayoutManager(linearLayoutManager)
        rv?.setItemAnimator(DefaultItemAnimator())
        rv?.setAdapter(adapter)
        rv?.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                this@DetailMovieActivity.isLoading = true
                currentPage += 1
                loadNextPage()
            }

            override fun getTotalPageCount(): Int {
                return TOTAL_PAGES
            }

            override fun isLastPage(): Boolean {
                //return isLastPage
                return true
            }

            override fun isLoading(): Boolean {
                //return isLoading
                return false
            }
        })

        //init service and load data
        movieService = getClient(this)!!.create(MovieService::class.java)
        loadFirstPage()
        btnRetry?.setOnClickListener(View.OnClickListener { view: View? -> loadFirstPage() })
        swipeRefreshLayout?.setOnRefreshListener(OnRefreshListener { doRefresh() })
    }

    private fun loadImage(posterPath: String) {
        Glide.with(this)
            .load(BASE_URL_IMG + posterPath)
            .centerCrop()
            .into(mPosterImg!!)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_refresh -> {
                // Signal SwipeRefreshLayout to start the progress indicator
                swipeRefreshLayout!!.isRefreshing = true
                doRefresh()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Triggers the actual background refresh via the [SwipeRefreshLayout]
     */
    private fun doRefresh() {
        progressBar!!.visibility = View.VISIBLE
        if (callReviewsApi(id).isExecuted) callReviewsApi(id).cancel()

        // TODO: Check if data is stale.
        //  Execute network request if cache is expired; otherwise do not update data.
        //adapter!!.getReviewResults().clear()
        adapter!!.clear()
        adapter!!.notifyDataSetChanged()
        loadFirstPage()
        swipeRefreshLayout!!.isRefreshing = false
    }

    private fun loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ")

        // To ensure list is visible when retry button in error view is clicked
        hideErrorView()
        currentPage = PAGE_START

        callReviewsApi(id).enqueue(object : Callback<ResultReview> {
            override fun onResponse(call: Call<ResultReview>, response: Response<ResultReview>) {
                hideErrorView()

//                Log.i(TAG, "onResponse: " + (response.raw().cacheResponse() != null ? "Cache" : "Network"));
                //Log.i(TAG, "Hasil: " + response.body().getReviews().get(0).getAuthor());

                // Got data. Send it to adapter
                val reviews = fetchResults(response)
                progressBar!!.visibility = View.GONE
                adapter!!.addAll(reviews)

                //if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
                //else
                isLastPage = true
            }

            override fun onFailure(call: Call<ResultReview>, t: Throwable) {
                t.printStackTrace()
                showErrorView(t)
            }
        })

        callVideosApi(id).enqueue(object : Callback<ResultVideos> {
            override fun onResponse(call: Call<ResultVideos>, response: Response<ResultVideos>) {
                hideErrorView()
                val videos = fetchResultsVideo(response)
                progressBar!!.visibility = View.GONE
                val youTubePlayerView = findViewById<YouTubePlayerView>(R.id.youtube_player_view)
                lifecycle.addObserver(youTubePlayerView)

                youTubePlayerView.addYouTubePlayerListener(object :
                    AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        if (videos.size>0) {
                            val videoId = videos.get(0).key ?: "ZxMTar5F4Ak"
                            videoId?.let { youTubePlayer.loadVideo(it, 0f) }
                        } else {
                            val videoId = "ZxMTar5F4Ak"
                            videoId?.let { youTubePlayer.loadVideo(it, 0f) }
                        }
                    }
                })

                isLastPage = true
            }

            override fun onFailure(call: Call<ResultVideos>, t: Throwable) {
                t.printStackTrace()
                showErrorView(t)
            }
        })
    }

    /**
     * @param response extracts List<[&gt;][Movie] from response
     * @return
     */
    private fun fetchResults(response: Response<ResultReview>): List<Review> {
        val resultReview = response.body()
        return resultReview!!.reviews
    }

    private fun fetchResultsVideo(response: Response<ResultVideos>): List<Video> {
        val resultVideos = response.body()
        return resultVideos!!.videos
    }

    private fun loadNextPage() {
        Log.d(TAG, "loadNextPage: $currentPage")
        callReviewsApi(id).enqueue(object : Callback<ResultReview> {
            override fun onResponse(call: Call<ResultReview>, response: Response<ResultReview>) {
//                Log.i(TAG, "onResponse: " + currentPage
//                        + (response.raw().cacheResponse() != null ? "Cache" : "Network"));
                adapter!!.removeLoadingFooter()
                isLoading = false
                val reviews = fetchResults(response)
                adapter!!.addAll(reviews)
                if (currentPage != TOTAL_PAGES) adapter!!.addLoadingFooter() else isLastPage = true
            }

            override fun onFailure(call: Call<ResultReview>, t: Throwable) {
                t.printStackTrace()
                adapter!!.showRetry(true, fetchErrorMessage(t))
            }
        })
    }

    /**
     * Performs a Retrofit call to the top rated movies API.
     * Same API call for Pagination.
     * As [.currentPage] will be incremented automatically
     * by @[PaginationScrollListener] to load next page.
     */
    private fun callReviewsApi(id: Int?): Call<ResultReview> {
        return movieService!!.getReviewMovie(
            id,
            getString(R.string.my_api_key),
            currentPage
        )
    }

    private fun callVideosApi(id: Int?): Call<ResultVideos> {
        return movieService!!.getVideoMovie(
            id,
            getString(R.string.my_api_key),
            currentPage
        )
    }

    override fun retryPageLoad() {
        loadNextPage()
    }

    /**
     * @param throwable required for [.fetchErrorMessage]
     * @return
     */
    private fun showErrorView(throwable: Throwable) {
        if (errorLayout!!.visibility == View.GONE) {
            errorLayout!!.visibility = View.VISIBLE
            progressBar!!.visibility = View.GONE
            txtError!!.text = fetchErrorMessage(throwable)
        }
    }

    /**
     * @param throwable to identify the type of error
     * @return appropriate error message
     */
    private fun fetchErrorMessage(throwable: Throwable): String {
        var errorMsg = resources.getString(R.string.error_msg_unknown)
        if (!isNetworkConnected) {
            errorMsg = resources.getString(R.string.error_msg_no_internet)
        } else if (throwable is TimeoutException) {
            errorMsg = resources.getString(R.string.error_msg_timeout)
        }
        return errorMsg
    }

    // Helpers -------------------------------------------------------------------------------------
    private fun hideErrorView() {
        if (errorLayout!!.visibility == View.VISIBLE) {
            errorLayout!!.visibility = View.GONE
            progressBar!!.visibility = View.VISIBLE
        }
    }

    /**
     * Remember to add android.permission.ACCESS_NETWORK_STATE permission.
     *
     * @return
     */
    private val isNetworkConnected: Boolean
        private get() {
            val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo != null
        }

    companion object {
        private const val TAG = "DetailMovieActivity"
        private const val BASE_URL_IMG = "https://image.tmdb.org/t/p/w200"
        private const val PAGE_START = 1

        // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
        private const val TOTAL_PAGES = 5
    }
}