package com.example.finalproject.ui.scan;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/predict-batik")
    Call<ImageResponse> predictBatik(@Body ImageRequest imageRequest);
}
