package com.example.skillswap.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skillswap.R;
import com.example.skillswap.models.CourseraCourse;

import java.util.List;

public class CourseraCoursesAdapter extends RecyclerView.Adapter<CourseraCoursesAdapter.ViewHolder> {

    private List<CourseraCourse> courseraCourses;
    private Context context;

    public CourseraCoursesAdapter(List<CourseraCourse> courseraCourses, Context context) {
        this.courseraCourses = courseraCourses;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CourseraCourse courseraCourse = courseraCourses.get(position);
        holder.courseTitle.setText(courseraCourse.getName());
        holder.courseProvider.setText(courseraCourse.getPartner());

        // Use Glide to load the image into the ImageView
        Glide.with(context)
                .load(courseraCourse.getPhotoUrl())
                .placeholder(R.drawable.ic_img_placeholder)
                .error(R.drawable.ic_error)
                .into(holder.courseImage);
    }

    @Override
    public int getItemCount() {
        return courseraCourses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView courseImage;
        TextView courseTitle;
        TextView courseProvider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseImage = itemView.findViewById(R.id.courseImage);
            courseTitle = itemView.findViewById(R.id.courseTitle);
            courseProvider = itemView.findViewById(R.id.courseProvider);
        }
    }

    public void updateData(List<CourseraCourse> newCourses) {
        courseraCourses.clear();
        courseraCourses.addAll(newCourses);
        notifyDataSetChanged();
    }
}
