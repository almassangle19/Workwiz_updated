package com.example.workwiz1;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workwiz1.adapter.JobAdapter;
import com.example.workwiz1.util.JobUtil;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;



public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {


    private Context mCtx;
    private List<com.example.workwiz1.Job> jobsArrayList;
    Resources resources;


    public interface OnJobSelectedListener {

        void onJobSelected(DocumentSnapshot restaurant);

    }
    private OnJobSelectedListener listener;
   // private JobAdapter.OnRestaurantSelectedListener mListener;


    public interface mClickListener {
        public void  mClick(DocumentSnapshot snapshot);
    }

    public CustomAdapter(Context mCtx, List<com.example.workwiz1.Job> jobsArrayList) {


        this.mCtx = mCtx;
        this.jobsArrayList = jobsArrayList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View v = inflater.inflate(R.layout.item_job, null);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        com.example.workwiz1.Job job = jobsArrayList.get(position);
        holder.mName.setText(job.getName());
        holder.mCity.setText(job.getCity());
        holder.mCategory.setText(job.getCategory());
        holder.mPrice.setText(JobUtil.getPriceString(job));
        holder.mAvgRating.setText(resources.getString(R.string.fmt_num_ratings,
                job.getNumRatings()));
        holder.ratingBar.setRating((float) job.getAvgRating());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             d
            }
        });
    }


    @Override
    public int getItemCount() {
        return jobsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mName, mCity, mCategory, mPrice, mAvgRating;
        MaterialRatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            resources = itemView.getResources();
            mName = itemView.findViewById(R.id.job_item_name);
            mCity = itemView.findViewById(R.id.restaurant_item_city);
            mCategory = itemView.findViewById(R.id.job_item_category);
            mPrice = itemView.findViewById(R.id.job_item_salary);
            mAvgRating = itemView.findViewById(R.id.restaurant_item_num_ratings);
            ratingBar = itemView.findViewById(R.id.job_item_rating);
        }
            }
        }

}