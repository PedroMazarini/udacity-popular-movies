package com.movix.movix;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.movix.movix.Entities.Movie;
import com.movix.movix.utilities.NetworkUtils;
import com.victor.loading.newton.NewtonCradleLoading;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.movix.movix.utilities.Constants.SORT_ORDER_TOP_RATED;

public class MoviesListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<String>  {

    @BindView(R.id.recycler_movies)
    RecyclerView recyclerMovies;
    @BindView(R.id.progress_movies_list)
    NewtonCradleLoading newtonCradleLoading;
    @BindView(R.id.fab_filter)
    FloatingActionButton fabFilter;
    MoviesListAdapter adapter;

    private static final String SORT_ORDER_EXTRA = "sort_order";
    private static final int FETCH_LOADER = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);
        ButterKnife.bind(this);
        adapter = new MoviesListAdapter(this);
        recyclerMovies.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerMovies.setAdapter(adapter);
        loadMoviesData(SORT_ORDER_TOP_RATED);

        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[] {getString(R.string.popular),getString(R.string.top_rated)};
                AlertDialog.Builder builder = new AlertDialog.Builder(MoviesListActivity.this);
                builder.setTitle(R.string.sort_movies)
                        .setItems(options, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        loadMoviesData("popular");
                                        break;
                                    case 1:
                                        loadMoviesData("top_rated");
                                        break;
                                }
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    // COMPLETED (8) Create a method that will get the user's preferred location and execute your new AsyncTask and call it loadWeatherData
    /**
     * This method will get the user's preferred location for weather, and then tell some
     * background method to get the weather data in the background.
     */
    private void loadMoviesData(String sortOrder) {
        adapter.clear();
        newtonCradleLoading.setVisibility(View.VISIBLE);
        newtonCradleLoading.start();
        Bundle queryBundle = new Bundle();
        queryBundle.putString(SORT_ORDER_EXTRA, sortOrder);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> githubSearchLoader = loaderManager.getLoader(FETCH_LOADER);
        if (githubSearchLoader == null) {
            loaderManager.initLoader(FETCH_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(FETCH_LOADER, queryBundle, this);
        }
    }

    public void callMovieDetails(Integer movieId, String movieBannerURL, Boolean video, String title, View view){
        view.setTransitionName("img_movie_banner");
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("movie_id",movieId);
        intent.putExtra("video",video);
        intent.putExtra("movie_banner_url", movieBannerURL);
        intent.putExtra("movie_title", title);
        ActivityOptionsCompat activityOptions =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, view.getTransitionName());
        ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            String moviesJSON;
            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                }
                if (moviesJSON != null) {
                    deliverResult(moviesJSON);
                } else {
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public String loadInBackground() {
                String sortOrder = args.getString(SORT_ORDER_EXTRA);
                URL moviesRequestURL = NetworkUtils.buildUrl(getApplicationContext(), sortOrder);

                try {
                    String moviesJSON = NetworkUtils
                            .getResponseFromHttpUrl(moviesRequestURL);
                    return moviesJSON;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            @Override
            public void deliverResult(String moviesJSON) {
                moviesJSON = moviesJSON;
                super.deliverResult(moviesJSON);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String moviesJSON) {
        if (moviesJSON != null) {
            try {
                JSONObject result = new JSONObject(moviesJSON);
                Gson gson = new Gson();
                Type movieListType = new TypeToken< ArrayList<Movie>>(){}.getType();
                List<Movie> movieList = gson.fromJson(result.getJSONArray("results").toString(), movieListType);
                newtonCradleLoading.stop();
                newtonCradleLoading.setVisibility(View.GONE);
                adapter.setList(movieList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(), R.string.failed_load,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}