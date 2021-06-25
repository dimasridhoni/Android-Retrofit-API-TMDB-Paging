/**
 * Created by Dimas Ridhoni on 6/25/21 12:56 AM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 6/25/21 12:56 AM
 */
package com.dimasridhoni.bankmandiri.activity

import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.dimasridhoni.bankmandiri.R
import com.dimasridhoni.bankmandiri.adapter.PaginationAdapterMovie
import com.dimasridhoni.bankmandiri.adapter.SpinnerAdapterGenre
import com.dimasridhoni.bankmandiri.api.MovieApi.getClient
import com.dimasridhoni.bankmandiri.api.MovieService
import com.dimasridhoni.bankmandiri.model.Genre
import com.dimasridhoni.bankmandiri.model.Movie
import com.dimasridhoni.bankmandiri.model.ResultGenres
import com.dimasridhoni.bankmandiri.model.ResultMovies
import com.dimasridhoni.bankmandiri.utils.PaginationAdapterCallback
import com.dimasridhoni.bankmandiri.utils.PaginationScrollListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeoutException

class MovieActivity : AppCompatActivity(), PaginationAdapterCallback {
    var adapter: PaginationAdapterMovie? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var rv: RecyclerView? = null
    var progressBar: ProgressBar? = null
    var errorLayout: LinearLayout? = null
    var btnRetry: Button? = null
    var txtError: TextView? = null
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var isLoading : Boolean = false
    private var isLastPage : Boolean = false
    private var currentPage = PAGE_START
    var movieList: ArrayList<Movie>? = null
    private var movieService: MovieService? = null
    private var spGenre: Spinner? = null
    var idGenre: Int? = null
    var listGenre: MutableList<Genre> = ArrayList()
    var spinnerAdapterGenre: SpinnerAdapterGenre? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)
        rv = findViewById(R.id.main_recycler)
        progressBar = findViewById(R.id.main_progress)
        errorLayout = findViewById(R.id.error_layout)
        btnRetry = findViewById(R.id.error_btn_retry)
        txtError = findViewById(R.id.error_txt_cause)
        swipeRefreshLayout = findViewById(R.id.main_swiperefresh)
        adapter = PaginationAdapterMovie(this)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv?.setLayoutManager(linearLayoutManager)
        rv?.setItemAnimator(DefaultItemAnimator())
        rv?.setAdapter(adapter)
        adapter!!.setListener { position ->
            println(
                "Tes Klik $position"
            )
        }
        rv?.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                this@MovieActivity.isLoading = true
                currentPage += 1
                loadNextPage()
            }

            override fun getTotalPageCount(): Int {
                return TOTAL_PAGES
            }

            override fun isLastPage(): Boolean {
                //return isLastPage
                return false
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
        spGenre = findViewById(R.id.sp_genre)
        spinnerAdapterGenre = SpinnerAdapterGenre(this@MovieActivity, listGenre)
        spGenre?.setAdapter(spinnerAdapterGenre)
        spGenre?.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                // TODO Auto-generated method stub
                idGenre = listGenre[position].id
                adapter!!.movies.clear()
                adapter!!.notifyDataSetChanged()
                loadFirstPage()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        })
        ambilDataGenre()
    }

    private fun ambilDataGenre() {
        listGenre.clear()
        val g = Genre()
        g.id = null
        g.name = "Pilih Genre"
        listGenre.add(g)
        callGenresApi().enqueue(object : Callback<ResultGenres> {
            override fun onResponse(call: Call<ResultGenres>, response: Response<ResultGenres>) {
                //hideErrorView();
                Log.i(TAG, "Hasil Genre : " + response.body()!!.genres[0].name)

                // Got data. Send it to adapter
                /*List<Genre> genres = fetchResultsGenre(response);
                listGenre = genres;*/for (i in response.body()!!.genres.indices) {
                    val g = Genre()
                    g.id = response.body()!!.genres[i].id
                    g.name = response.body()!!.genres[i].name
                    listGenre.add(g)
                }
                spinnerAdapterGenre!!.notifyDataSetChanged()
                //isLastPage = true;
            }

            override fun onFailure(call: Call<ResultGenres>, t: Throwable) {
                t.printStackTrace()
                showErrorView(t)
            }
        })
    }

    private fun callGenresApi(): Call<ResultGenres> {
        return movieService!!.getGenres(
            getString(R.string.my_api_key),
            currentPage
        )
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
        if (callTopRatedMoviesApi(idGenre).isExecuted) callTopRatedMoviesApi(idGenre).cancel()

        // TODO: Check if data is stale.
        //  Execute network request if cache is expired; otherwise do not update data.
        adapter!!.movies.clear()
        adapter!!.notifyDataSetChanged()
        loadFirstPage()
        swipeRefreshLayout!!.isRefreshing = false
    }

    private fun loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ")

        // To ensure list is visible when retry button in error view is clicked
        hideErrorView()
        currentPage = PAGE_START
        callTopRatedMoviesApi(idGenre).enqueue(object : Callback<ResultMovies?> {
            override fun onResponse(call: Call<ResultMovies?>, response: Response<ResultMovies?>) {
                hideErrorView()
                Log.i(
                    TAG,
                    "onResponse: " + if (response.raw()
                            .cacheResponse() != null
                    ) "Cache" else "Network"
                )
                // Log.i(TAG, "Hasil: " + response.body().getMovies().get(0).getTitle());

                // Got data. Send it to adapter
                val movies = fetchResults(response)
                progressBar!!.visibility = View.GONE
                adapter!!.addAll(movies)
                if (currentPage <= TOTAL_PAGES) adapter!!.addLoadingFooter() else isLastPage = true
            }

            override fun onFailure(call: Call<ResultMovies?>, t: Throwable) {
                t.printStackTrace()
                showErrorView(t)
            }
        })
    }

    /**
     * @param response extracts List<[&gt;][Movie] from response
     * @return
     */
    private fun fetchResults(response: Response<ResultMovies?>): List<Movie> {
        val resultMovies = response.body()
        return resultMovies!!.movies
    }

    private fun fetchResultsGenre(response: Response<ResultGenres>): List<Genre> {
        val resultGenres = response.body()
        return resultGenres!!.genres
    }

    private fun loadNextPage() {
        Log.d(TAG, "loadNextPage: $currentPage")
        callTopRatedMoviesApi(idGenre).enqueue(object : Callback<ResultMovies?> {
            override fun onResponse(call: Call<ResultMovies?>, response: Response<ResultMovies?>) {
//                Log.i(TAG, "onResponse: " + currentPage
//                        + (response.raw().cacheResponse() != null ? "Cache" : "Network"));
                adapter!!.removeLoadingFooter()
                isLoading = false
                val movies = fetchResults(response)
                adapter!!.addAll(movies)
                if (currentPage != TOTAL_PAGES) adapter!!.addLoadingFooter() else isLastPage = true
            }

            override fun onFailure(call: Call<ResultMovies?>, t: Throwable) {
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
    private fun callTopRatedMoviesApi(idGenre: Int?): Call<ResultMovies> {
        return movieService!!.getDiscoverMoviesByGenre(
            getString(R.string.my_api_key),
            currentPage,
            idGenre
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
        private const val TAG = "MovieActivity"
        private const val PAGE_START = 1

        // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
        private const val TOTAL_PAGES = 5
    }
}