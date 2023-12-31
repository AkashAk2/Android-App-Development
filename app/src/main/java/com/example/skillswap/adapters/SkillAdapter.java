package com.example.skillswap.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillswap.R;
import com.example.skillswap.models.Skill;

import java.util.ArrayList;
import java.util.List;

public class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.ViewHolder> {
    private List<Skill> mSkillsList;
    private OnItemClickListener mOnItemClickListener;
    public SkillAdapter(List<Skill> skills, OnItemClickListener listener) {
        mSkillsList = skills;
        mOnItemClickListener = listener;
    }

    public void setSkillsList(List<Skill> skillsList) {
        mSkillsList = new ArrayList<>();
        mSkillsList.addAll(skillsList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_skill,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mSkillsList.get(position));
        holder.itemView.findViewById(R.id.skillBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onSkillClicked(mSkillsList.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mSkillsList!=null)
            return mSkillsList.size();
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final Button skillBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            skillBtn = itemView.findViewById(R.id.skillBtn);
        }

        public void bind(Skill skill) {
            skillBtn.setText(skill.getSkill());
        }
    }

    public interface OnItemClickListener{
        void onSkillClicked(Skill skill);
    }
}