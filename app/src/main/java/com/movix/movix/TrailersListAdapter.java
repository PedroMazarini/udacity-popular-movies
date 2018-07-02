package com.movix.movix;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.movix.movix.Entities.Movie;
import com.movix.movix.Entities.Trailer;
import com.movix.movix.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TrailersListAdapter extends RecyclerView.Adapter<TrailersListAdapter.MyViewHolder> {
    List<Trailer> trailerList;
    Context mContext;
    MovieDetailsActivity mActivity;

    public TrailersListAdapter(MovieDetailsActivity mActivity) {
        trailerList = new ArrayList<>();
        this.mContext = mActivity.getApplicationContext();
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.trailer_item_layout, viewGroup, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, final int i) {
        final String trailerThumbUrl = NetworkUtils.buildTrailerThumbUrl(trailerList.get(i).getKey());
        viewHolder.txtTrailerTitle.setText(trailerList.get(i).getName());
        Glide.with(mContext).load(trailerThumbUrl).into(viewHolder.imgTrailerThumb);
        viewHolder.trailerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.watchYoutubeVideo(trailerList.get(i).getKey());
            }
        });
    }



    public void setList(List<Trailer> trailerList){
        this.trailerList = trailerList;
        this.notifyDataSetChanged();
    }

    public void clear(){
        trailerList.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_trailer_thumb)
        ImageView imgTrailerThumb;
        @BindView(R.id.txt_trailer_title)
        TextView txtTrailerTitle;
        @BindView(R.id.layout_trailer)
        LinearLayout trailerLayout;


        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
