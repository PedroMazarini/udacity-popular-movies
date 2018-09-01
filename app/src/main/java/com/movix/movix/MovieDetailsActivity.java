package com.movix.movix;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.movix.movix.Entities.Movie;
import com.movix.movix.Entities.Review;
import com.movix.movix.Entities.Trailer;
import com.movix.movix.local.MovieLocal;
import com.movix.movix.local.MovixDatabase;
import com.movix.movix.utilities.NetworkUtils;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<String> {

    @BindView(R.id.txt_release_date)
    TextView txtReleaseDate;
    @BindView(R.id.txt_duration)
    TextView txtDuration;
    @BindView(R.id.txt_avaliation)
    TextView txtAvaliation;
    @BindView(R.id.txt_movie_description)
    TextView txtMovieDescription;
    @BindView(R.id.btn_favorite)
    LottieAnimationView btnFavorite;
    @BindView(R.id.img_movie_banner)
    ImageView imgMovieBanner;
    @BindView(R.id.rotateloading)
    RotateLoading rotateLoading;
    @BindView(R.id.rotateloading_trailers)
    RotateLoading rotateLoadingTrailers;
    @BindView(R.id.layout_failed)
    LinearLayout failedLayout;
    @BindView(R.id.btn_retry_details)
    Button btnRetryDetails;
    @BindView(R.id.btn_retry_trailers)
    Button btnRetryVideos;
    @BindView(R.id.layout_movie_traleirs)
    LinearLayout trailersLayout;
    @BindView(R.id.layout_failed_trailers)
    LinearLayout failedTrailersLayout;
    @BindView(R.id.recycler_trailers)
    RecyclerView recyclerTrailers;
    @BindView(R.id.rotateloading_reviews)
    RotateLoading rotateLoadingReviews;
    @BindView(R.id.btn_retry_reviews)
    Button btnRetryReviews;
    @BindView(R.id.layout_movie_reviews)
    LinearLayout reviewsLayout;
    @BindView(R.id.layout_failed_reviews)
    LinearLayout failedReviewsLayout;
    @BindView(R.id.recycler_reviews)
    RecyclerView recyclerReviews;
    Boolean isFavorite = false;
    Integer movieId;
    Boolean video;
    String title;
    String movieBannerURL;
    TrailersListAdapter adapter;
    ReviewsListAdapter reviewsListAdapter;
    String poster_path;

    SimpleDateFormat outDateFormat = new SimpleDateFormat("MM/yyyy");
    SimpleDateFormat inDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    MovixDatabase movixDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        movieBannerURL = extras.getString("movie_banner_url");
        poster_path = extras.getString("poster_path");
        movieId = extras.getInt("movie_id");
        video = extras.getBoolean("video");
        title = extras.getString("movie_title");
        getSupportActionBar().setTitle(title);
        Glide.with(this).load(movieBannerURL).placeholder(R.drawable.movie_placeholder).into(imgMovieBanner);
        loadMovieDetails(movieId);
        loadMovieTrailers(movieId);
        loadMovieReviews(movieId);
        btnFavorite.setProgress(0.25f);

        btnRetryDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMovieDetails(movieId);
                rotateLoading.setVisibility(View.VISIBLE);
                failedLayout.setVisibility(View.GONE);
            }
        });
        btnRetryVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMovieTrailers(movieId);
                rotateLoadingTrailers.setVisibility(View.VISIBLE);
                failedTrailersLayout.setVisibility(View.GONE);
            }
        });
        btnRetryReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMovieReviews(movieId);
                rotateLoadingReviews.setVisibility(View.VISIBLE);
                failedReviewsLayout.setVisibility(View.GONE);
            }
        });
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFavorite){
                    btnFavorite.pauseAnimation();
                    btnFavorite.setProgress(0.25f);
                }else{
                    btnFavorite.playAnimation();
                }
                toggleFavoriteMovie();
            }
        });
        btnFavorite.setEnabled(false);
        checkForFavorite();
    }

    private void checkForFavorite() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                movixDatabase = MovixDatabase.getInstance(MovieDetailsActivity.this);
                MovieLocal movieLocal = movixDatabase.getMovieDao().selectMovie(movieId);
                if(movieLocal != null){
                    isFavorite = true;
                    playAnimation();
                }
                enableButton();
            }
        });
    }

    private void playAnimation() {
        Handler mainHandler = new Handler(Looper.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {btnFavorite.playAnimation();} // This is your code
        };
        mainHandler.post(myRunnable);

    }

    private void enableButton(){
        Handler mainHandler = new Handler(Looper.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                btnFavorite.setEnabled(true);} // This is your code
        };
        mainHandler.post(myRunnable);
    }
    private void toggleFavoriteMovie() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                movixDatabase = MovixDatabase.getInstance(MovieDetailsActivity.this);
                if(isFavorite) {
                    movixDatabase.getMovieDao().remove(movieId);
                }else{
                    MovieLocal movieLocal = new MovieLocal();
                    movieLocal.setId(movieId);
                    movieLocal.setPoster_path(poster_path);
                    movieLocal.setTitle(title);
                    movieLocal.setVideo(video);
                    movixDatabase.getMovieDao().insertMovie(movieLocal);
                }
                isFavorite = !isFavorite;
            }
        });
    }

    private void loadMovieTrailers(Integer movieId) {
        trailersLayout.setVisibility(View.VISIBLE);
        adapter = new TrailersListAdapter(this);
        recyclerTrailers.setLayoutManager(new LinearLayoutManager(this));
        recyclerTrailers.setAdapter(adapter);
        new FetchMovieTrailersTask().execute(movieId);
    }

    private void loadMovieReviews(Integer movieId) {
        reviewsLayout.setVisibility(View.VISIBLE);
        reviewsListAdapter = new ReviewsListAdapter(this);
        recyclerReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerReviews.setAdapter(reviewsListAdapter);
        new FetchMovieReviewsTask().execute(movieId);
    }

    public void watchYoutubeVideo(String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            this.startActivity(Intent.createChooser(appIntent, getString(R.string.choose_a_player)));
        } catch (ActivityNotFoundException ex) {
            this.startActivity(webIntent);
        }
    }

    private void loadMovieDetails(Integer movieId) {
        new FetchMovieDetailsTask().execute(movieId);
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    public class FetchMovieDetailsTask extends AsyncTask<Integer, Void, String> {

        // COMPLETED (6) Override the doInBackground method to perform your network requests
        @Override
        protected String doInBackground(Integer... params) {

            Integer movieId = params[0];
            URL moviesRequestURL = NetworkUtils.buildDetailsURL(getApplicationContext(), movieId);

            try {
                String movieTrailersJSON = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestURL);
                return movieTrailersJSON;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        // COMPLETED (7) Override the onPostExecute method to display the results of the network request
        @Override
        protected void onPostExecute(String moviesJSON) {
            if (moviesJSON != null) {
                try {
                    JSONObject result = new JSONObject(moviesJSON);
                    Gson gson = new Gson();
                    Type movieType = new TypeToken<Movie>(){}.getType();
                    Movie movie = gson.fromJson(result.toString(), movieType);
                    rotateLoading.setVisibility(View.GONE);
                    setMovieDetails(movie);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(getApplicationContext(), R.string.failed_load,Toast.LENGTH_LONG).show();
                rotateLoading.setVisibility(View.GONE);
                failedLayout.setVisibility(View.VISIBLE);
            }
        }


    }
    public void setMovieDetails(Movie movie){
        btnFavorite.setVisibility(View.VISIBLE);
        trailersLayout.setVisibility(View.VISIBLE);
        txtMovieDescription.setText(movie.getOverview());
        txtAvaliation.setText(String.valueOf(movie.getVote_average())+"/10");
        txtDuration.setText(String.valueOf(movie.getRuntime()+"min"));
        try {
            txtReleaseDate.setText(outDateFormat.format(inDateFormat.parse(movie.getRelease_date())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public class FetchMovieTrailersTask extends AsyncTask<Integer, Void, String> {

        // COMPLETED (6) Override the doInBackground method to perform your network requests
        @Override
        protected String doInBackground(Integer... params) {

            Integer movieId = params[0];
            URL moviesRequestURL = NetworkUtils.buildTraillersURL(getApplicationContext(), movieId);

            try {
                String movieTrailersJSON = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestURL);
                return movieTrailersJSON;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        // COMPLETED (7) Override the onPostExecute method to display the results of the network request
        @Override
        protected void onPostExecute(String moviesJSON) {
            if (moviesJSON != null) {
                try {
                    JSONObject result = new JSONObject(moviesJSON);
                    Gson gson = new Gson();
                    Type trailerListType = new TypeToken< ArrayList<Trailer>>(){}.getType();
                    List<Trailer> trailerList = gson.fromJson(result.getJSONArray("results").toString(), trailerListType);
                    rotateLoadingTrailers.setVisibility(View.GONE);
                    trailersLayout.setVisibility(View.VISIBLE);
                    List<Trailer> youtubeOnly = new ArrayList<>();
                    for (Trailer trailer : trailerList) {
                        if(trailer.getSite().equals("YouTube")) youtubeOnly.add(trailer);
                    }
                    if(youtubeOnly.size()>0) {
                        adapter.setList(youtubeOnly);
                    }else{
                        trailersLayout.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    trailersLayout.setVisibility(View.GONE);
                }
            }else{
                Toast.makeText(getApplicationContext(), R.string.failed_load,Toast.LENGTH_LONG).show();
                rotateLoadingTrailers.setVisibility(View.GONE);
                failedTrailersLayout.setVisibility(View.VISIBLE);
            }
        }


    }

    public class FetchMovieReviewsTask extends AsyncTask<Integer, Void, String> {

        // COMPLETED (6) Override the doInBackground method to perform your network requests
        @Override
        protected String doInBackground(Integer... params) {

            Integer movieId = params[0];
            URL moviesRequestURL = NetworkUtils.buildReviewsURL(getApplicationContext(), movieId);

            try {
                String movieReviewsJSON = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestURL);
                return movieReviewsJSON;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        // COMPLETED (7) Override the onPostExecute method to display the results of the network request
        @Override
        protected void onPostExecute(String reviewsJSON) {
            if (reviewsJSON != null) {
                try {
                    JSONObject result = new JSONObject(reviewsJSON);
                    Gson gson = new Gson();
                    Type reviewListType = new TypeToken< ArrayList<Review>>(){}.getType();
                    List<Review> reviewsList = gson.fromJson(result.getJSONArray("results").toString(), reviewListType);
                    rotateLoadingReviews.setVisibility(View.GONE);
                    reviewsLayout.setVisibility(View.VISIBLE);
                    reviewsListAdapter.setList(reviewsList);
                } catch (JSONException e) {
                    e.printStackTrace();
                    trailersLayout.setVisibility(View.GONE);
                }
            }else{
                Toast.makeText(getApplicationContext(), R.string.failed_load,Toast.LENGTH_LONG).show();
                rotateLoadingTrailers.setVisibility(View.GONE);
                failedTrailersLayout.setVisibility(View.VISIBLE);
            }
        }


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
