package com.example.myapplication;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("https://luisappgim.000webhostapp.com/appgim/vermensaje.php")
    Call<List<MyData>> getMyDataList();
}
