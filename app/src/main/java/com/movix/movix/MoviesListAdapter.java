package com.movix.movix;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.movix.movix.Entities.Movie;
import com.movix.movix.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
public class MoviesListAdapter extends RecyclerView.Adapter<MoviesListAdapter.MyViewHolder> {
    List<Movie> movieList;
    Context mContext;
    MoviesListActivity mActivity;

    public MoviesListAdapter(MoviesListActivity mActivity) {
        movieList = new ArrayList<>();
        this.mContext = mActivity.getApplicationContext();
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.movie_item_layout, viewGroup, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, final int i) {
        final Movie movie = movieList.get(i);
        final String movieBannerUrl = NetworkUtils.buildBannerUrl(movie.getPoster_path());
        Glide.with(mContext).load(movieBannerUrl).placeholder(R.drawable.movie_placeholder).into(viewHolder.imgMovieBanner);
        viewHolder.imgMovieBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.callMovieDetails(movieList.get(i).getId(),movieBannerUrl, movieList.get(i).getVideo(), movieList.get(i).getTitle(), view);
            }
        });
    }

    public void setList(List<Movie> movieList){
        this.movieList = movieList;
        this.notifyDataSetChanged();
    }

    public void clear(){
        movieList.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_movie_banner)
        ImageView imgMovieBanner;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
