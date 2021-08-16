package com.masorone.mastopmoviesappores.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.masorone.mastopmoviesappores.R;
import com.masorone.mastopmoviesappores.data.Review;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private ArrayList<Review> reviews;

    @NonNull
    @NotNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext())
                .inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ReviewAdapter.ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.content.setText(review.getContent());
        holder.author.setText(review.getAuthor());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        private TextView author;
        private TextView content;

        public ReviewViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.text_view_author);
            content = itemView.findViewById(R.id.text_view_content);
        }
    }
}
