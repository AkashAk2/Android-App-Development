package com.example.skillswap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillswap.R;

import java.util.ArrayList;
import java.util.List;

public class SearchSkillAdapter extends RecyclerView.Adapter<SearchSkillAdapter.ViewHolder> {
    private List<com.example.skillswap.Skill> mSkillList;
    private OnItemClickListener mOnItemClickListener;
    public SearchSkillAdapter(List<com.example.skillswap.Skill> skillList, OnItemClickListener listener) {
        mSkillList = skillList;
        mOnItemClickListener = listener;
    }

    public void setSkillList(List<com.example.skillswap.Skill> skillList){
        mSkillList = new ArrayList<>();
        mSkillList.addAll(skillList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_search_skill,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mSkillList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onSkillClicked(mSkillList.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mSkillList!=null)
            return mSkillList.size();
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView skillTv,categoryTv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            skillTv = itemView.findViewById(R.id.skillText);
            categoryTv = itemView.findViewById(R.id.categoryText);
        }

        public void bind(com.example.skillswap.Skill skill) {
            skillTv.setText(skill.getSkill());
            categoryTv.setText(skill.getCategory());
        }
    }

    public interface OnItemClickListener{
        void onSkillClicked(com.example.skillswap.Skill skill);
    }
}
