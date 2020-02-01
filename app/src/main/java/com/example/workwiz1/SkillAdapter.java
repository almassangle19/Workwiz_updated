package com.example.workwiz1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.skillViewHolder>{

    private Context mCtx;
    private List<SkillsList>skillsList;

    public SkillAdapter(Context mCtx, List<SkillsList> skillsList) {
        this.mCtx = mCtx;
        this.skillsList = skillsList;
    }

    @NonNull
    @Override
    public skillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.item_skills,null);
        skillViewHolder holder = new skillViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull skillViewHolder holder, int position) {
        SkillsList skills = skillsList.get(position);
        holder.skill.setText(skills.getSkill());

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return skillsList.size();
    }

    class skillViewHolder extends RecyclerView.ViewHolder{

        TextView skill;
        public skillViewHolder(@NonNull View itemView) {
            super(itemView);

            skill = itemView.findViewById(R.id.skill_item_name);

        }
    }
}
