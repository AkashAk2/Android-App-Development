package com.example.skillswap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillswap.R;

import java.util.ArrayList;
import java.util.List;

public class TeachSkillAdapter extends RecyclerView.Adapter<TeachSkillAdapter.ViewHolder> {
    private List<String> mSkillsList;

    public TeachSkillAdapter(List<String> skills) {
        mSkillsList = skills;
    }

    public void setSkillsList(List<String> skillsList) {
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

        public void bind(String skill) {
            skillBtn.setText(skill);
        }
    }
}
