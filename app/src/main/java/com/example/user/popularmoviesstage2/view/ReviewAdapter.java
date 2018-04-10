package com.example.user.popularmoviesstage2.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.popularmoviesstage2.R;
import com.example.user.popularmoviesstage2.model.Review;

import java.util.List;

/**
 * Created by Andris on 027 27.03.18.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private List<Review> reviews;
    private Context context;

    public ReviewAdapter(List<Review> reviews, Context context) {
        this.reviews = reviews;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        Review review = reviews.get(position);

        holder.title.setText(review.getAuthor());
        holder.reviewText.setText(review.getContent());

    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView reviewText;
        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.reviewTitleTextView);
            reviewText = itemView.findViewById(R.id.reviewTextView);
        }
    }
}
