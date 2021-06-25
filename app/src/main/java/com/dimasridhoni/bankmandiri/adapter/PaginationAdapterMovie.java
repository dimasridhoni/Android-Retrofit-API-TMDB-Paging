/**
 * Created by Dimas Ridhoni on 6/25/21 11:51 AM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 6/25/21 9:59 AM
 */

package com.dimasridhoni.bankmandiri.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dimasridhoni.bankmandiri.R;
import com.dimasridhoni.bankmandiri.activity.DetailMovieActivity;
import com.dimasridhoni.bankmandiri.model.Movie;
//import com.dimasridhoni.tesmandiri.utils.GlideApp;
//import com.dimasridhoni.tesmandiri.utils.GlideRequest;
import com.dimasridhoni.bankmandiri.utils.PaginationAdapterCallback;

import java.util.ArrayList;
import java.util.List;

public class PaginationAdapterMovie extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // View Types
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final int HERO = 2;

    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/w200";

    private List<Movie> movieResults;
    private Context context;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    private PaginationAdapterCallback mCallback;

    private String errorMsg;

    public PaginationAdapterMovie.Listener listener;

    public PaginationAdapterMovie(Context context) {
        this.context = context;
        this.mCallback = (PaginationAdapterCallback) context;
        movieResults = new ArrayList<>();
    }

    public List<Movie> getMovies() {
        return movieResults;
    }

    public void setMovies(List<Movie> movieResults) {
        this.movieResults = movieResults;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.item_movie_list, parent, false);
                viewHolder = new MovieVH(viewItem);

                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(viewLoading);
                break;
            case HERO:
                View viewHero = inflater.inflate(R.layout.item_movie_hero, parent, false);
                viewHolder = new HeroVH(viewHero);

                break;
        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Movie movie = movieResults.get(position); // Movie
        System.out.println("Movie Result : "+movie.getTitle());
        switch (getItemViewType(position)) {

            case HERO:
                final HeroVH heroVh = (HeroVH) holder;

                heroVh.mMovieTitle.setText(movie.getTitle());
                heroVh.mYear.setText(formatYearLabel(movie));
                heroVh.mMovieDesc.setText(movie.getOverview());

                loadImageHero(movie.getBackdropPath(), heroVh);


                heroVh.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        System.out.println("Klik "+ movie.getId());
                        Intent intent = new Intent(context, DetailMovieActivity.class);
                        intent.putExtra("id",movie.getId());
                        intent.putExtra("title",movie.getTitle());
                        intent.putExtra("overview",movie.getOverview());
                        intent.putExtra("image", movie.getBackdropPath());
                        context.startActivity(intent);

                    }
                });

                break;

            case ITEM:
                final MovieVH movieVH = (MovieVH) holder;

                movieVH.mMovieTitle.setText(movie.getTitle());
                movieVH.mYear.setText(formatYearLabel(movie));
                movieVH.mMovieDesc.setText(movie.getOverview());

                // load movie thumbnail
                loadImageMovie(movie.getPosterPath(), movieVH);

                movieVH.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        System.out.println("Klik "+ movie.getId());
                        Intent intent = new Intent(context, DetailMovieActivity.class);
                        intent.putExtra("id",movie.getId());
                        intent.putExtra("title",movie.getTitle());
                        intent.putExtra("overview",movie.getOverview());
                        intent.putExtra("image", movie.getBackdropPath());
                        context.startActivity(intent);

                    }
                });

                break;

            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;

                if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.mProgressBar.setVisibility(View.GONE);

                    loadingVH.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    context.getString(R.string.error_msg_unknown));

                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
        }



    }

    @Override
    public int getItemCount() {
        return movieResults == null ? 0 : movieResults.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HERO;
        } else {
            return (position == movieResults.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
        }
    }

    /*
        Helpers - bind Views
   _________________________________________________________________________________________________
    */

    /**
     * @param movie
     * @return [releasedate] | [2letterlangcode]
     */
    private String formatYearLabel(Movie movie) {
        System.out.println("Movie : "+movie.getTitle());
        if ((movie.getReleaseDate() != null) && (movie.getOriginalLanguage() != null)) {
            if ((movie.getReleaseDate().length()>=4) && (movie.getOriginalLanguage().length()>0)) {
                return movie.getReleaseDate().substring(0, 4)  // we want the year only
                        + " | "
                        + movie.getOriginalLanguage().toUpperCase();
            } else {
                return ""  // we want the year only
                        + " | "
                        + "";
            }
        } else {
            return ""  // we want the year only
                    + " | "
                    + "";
        }
    }

    /**
     * Using Glide to handle image loading.
     * Learn more about Glide here:
     * <a href="http://blog.grafixartist.com/image-gallery-app-android-studio-1-4-glide/" />
     * <p>
     * //     * @param posterPath from {@link Movie#getPosterPath()}
     *
     * @return Glide builder
     */
//    private DrawableRequestBuilder<String> loadImage(@NonNull String posterPath) {
//        return Glide
//                .with(context)
//                .load(BASE_URL_IMG + posterPath)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
//                .centerCrop()
//                .crossFade();

//    }
    private void loadImageHero(@NonNull String posterPath, HeroVH heroVh) {
        Glide.with(context)
                .load(BASE_URL_IMG + posterPath)
                .centerCrop().into(heroVh.mPosterImg);;
    }

    private void loadImageMovie(@NonNull String posterPath, MovieVH movieVh) {
        Glide.with(context)
                .load(BASE_URL_IMG + posterPath)
                .centerCrop().into(movieVh.mPosterImg);;
    }



    /*
        Helpers - Pagination
   _________________________________________________________________________________________________
    */

    public void add(Movie r) {
        movieResults.add(r);
        notifyItemInserted(movieResults.size() - 1);
    }

    public void addAll(List<Movie> moveResults) {
        for (Movie movie : moveResults) {
            add(movie);
        }
    }

    public void remove(Movie r) {
        int position = movieResults.indexOf(r);
        if (position > -1) {
            movieResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Movie());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = movieResults.size() - 1;
        Movie movie = getItem(position);

        if (movie != null) {
            movieResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Movie getItem(int position) {
        return movieResults.get(position);
    }

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(movieResults.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Header ViewHolder
     */
    protected class HeroVH extends RecyclerView.ViewHolder {
        private TextView mMovieTitle;
        private TextView mMovieDesc;
        private TextView mYear; // displays "year | language"
        private ImageView mPosterImg;

        public HeroVH(View itemView) {
            super(itemView);

            mMovieTitle = itemView.findViewById(R.id.movie_title);
            mMovieDesc = itemView.findViewById(R.id.movie_desc);
            mYear = itemView.findViewById(R.id.movie_year);
            mPosterImg = itemView.findViewById(R.id.movie_poster);
        }
    }

    /**
     * Main list's content ViewHolder
     */
    protected class MovieVH extends RecyclerView.ViewHolder {
        private TextView mMovieTitle;
        private TextView mMovieDesc;
        private TextView mYear; // displays "year | language"
        private ImageView mPosterImg;
        private ProgressBar mProgress;

        public MovieVH(View itemView) {
            super(itemView);

            mMovieTitle = itemView.findViewById(R.id.movie_title);
            mMovieDesc = itemView.findViewById(R.id.movie_desc);
            mYear = itemView.findViewById(R.id.movie_year);
            mPosterImg = itemView.findViewById(R.id.movie_poster);
            mProgress = itemView.findViewById(R.id.movie_progress);
        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = itemView.findViewById(R.id.loadmore_errorlayout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:

                    showRetry(false, null);
                    mCallback.retryPageLoad();

                    break;
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        public ViewHolder(CardView itemView) {
            super(itemView);
            cardView = itemView;
        }
    }

    public interface Listener {
        void onClick(int position);
    }

    public void setListener(PaginationAdapterMovie.Listener listener) {
        this.listener = listener;
    }


}
