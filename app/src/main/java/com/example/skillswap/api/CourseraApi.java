package com.example.skillswap.api;

import com.example.skillswap.models.CourseraCourse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CourseraApi {
    @GET("courses")
    Call<List<CourseraCourse>> getPopularPaidCourses(@Query("is_paid") boolean isPaid);
}
