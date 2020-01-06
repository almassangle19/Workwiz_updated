package com.example.workwiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workwiz.util.JobUtil;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private ArrayList<Job> jobsArrayList;
    private Context mCtx;

    public CustomAdapter(ArrayList<Job> jobsArrayList, Context mCtx) {
        this.jobsArrayList = jobsArrayList;
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job , parent , false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Job job = jobsArrayList.get(position);
        holder.mName.setText(job.getName());
        holder.mCity.setText(job.getCity());
        holder.mCategory.setText(job.getCategory());
        holder.mPrice.setText(job.getPrice());

    }

    @Override
    public int getItemCount() {
        return jobsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mName,mCity, mCategory, mPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.job_item_name);
            mCity = itemView.findViewById(R.id.restaurant_item_city);
            mCategory = itemView.findViewById(R.id.job_item_category);
            mPrice = itemView.findViewById(R.id.job_item_salary);


        }
    }
}