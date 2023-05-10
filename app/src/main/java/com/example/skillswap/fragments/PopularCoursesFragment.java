package com.example.skillswap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillswap.R;
import com.example.skillswap.adapters.CourseraCoursesAdapter;
import com.example.skillswap.api.CourseraApi;
import com.example.skillswap.models.CourseraCourse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PopularCoursesFragment extends Fragment {
    private RecyclerView courseraCoursesRecyclerView;
    private CourseraCoursesAdapter courseraCoursesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popular_courses, container, false);

        courseraCoursesRecyclerView = view.findViewById(R.id.courseraCoursesRecyclerView);
        courseraCoursesAdapter = new CourseraCoursesAdapter(new ArrayList<>(), getActivity());
        courseraCoursesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        courseraCoursesRecyclerView.setAdapter(courseraCoursesAdapter);

        fetchPopularPaidCourses();

        return view;
    }

    private void fetchPopularPaidCourses() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.coursera.org/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CourseraApi courseraApi = retrofit.create(CourseraApi.class);
        Call<List<CourseraCourse>> call = courseraApi.getPopularPaidCourses(true);

        call.enqueue(new Callback<List<CourseraCourse>>() {
            @Override
            public void onResponse(Call<List<CourseraCourse>> call, Response<List<CourseraCourse>> response) {
                if (response.isSuccessful()) {
                    List<CourseraCourse> courses = response.body();
                    courseraCoursesAdapter.updateData(courses);
                }
            }

            @Override
            public void onFailure(Call<List<CourseraCourse>> call, Throwable t) {
                // Handle error
            }
        });
    }
}

