package com.example.workwiz.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workwiz.Job;
import com.example.workwiz.R;
import com.example.workwiz.util.JobUtil;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * RecyclerView adapter for a list of Restaurants.
 */
public class JobAdapter extends com.example.workwiz.adapter.FirestoreAdapter<JobAdapter.ViewHolder> {

    public interface OnRestaurantSelectedListener {

        void onJobSelected(DocumentSnapshot restaurant);

    }

    private OnRestaurantSelectedListener mListener;

    public JobAdapter(Query query, OnRestaurantSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_job, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameView;
        MaterialRatingBar ratingBar;
        TextView numRatingsView;
        TextView priceView;
        TextView categoryView;
        TextView cityView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.job_item_name);
            ratingBar = itemView.findViewById(R.id.job_item_rating);
            numRatingsView = itemView.findViewById(R.id.restaurant_item_num_ratings);
            priceView = itemView.findViewById(R.id.job_item_salary);
            categoryView = itemView.findViewById(R.id.job_item_category);
            cityView = itemView.findViewById(R.id.restaurant_item_city);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnRestaurantSelectedListener listener) {

            Job job = snapshot.toObject(Job.class);
            Resources resources = itemView.getResources();


            nameView.setText(job.getName());
            ratingBar.setRating((float) job.getAvgRating());
            cityView.setText(job.getCity());
            categoryView.setText(job.getCategory());
            numRatingsView.setText(resources.getString(R.string.fmt_num_ratings,
                    job.getNumRatings()));
            priceView.setText(JobUtil.getPriceString(job));

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onJobSelected(snapshot);
                    }
                }
            });
        }

    }
}
