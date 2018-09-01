package com.movix.movix;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.movix.movix.Entities.Review;
import com.movix.movix.Entities.Trailer;
import com.movix.movix.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ReviewsListAdapter extends RecyclerView.Adapter<ReviewsListAdapter.MyViewHolder> {
    List<Review> reviewList;
    Context mContext;
    MovieDetailsActivity mActivity;

    public ReviewsListAdapter(MovieDetailsActivity mActivity) {
        reviewList = new ArrayList<>();
        this.mContext = mActivity.getApplicationContext();
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.review_item_layout, viewGroup, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, final int i) {
        viewHolder.txtReviewContent.setText(reviewList.get(i).getContent());
        viewHolder.txtReviewerName.setText(reviewList.get(i).getAuthor());
    }



    public void setList(List<Review> reviews){
        this.reviewList = reviews;
        this.notifyDataSetChanged();
    }

    public void clear(){
        reviewList.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_reviewer_name)
        TextView txtReviewerName;
        @BindView(R.id.txt_review)
        TextView txtReviewContent;


        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
