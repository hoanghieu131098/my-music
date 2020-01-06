package com.example.ungdungngenhac.API;

import com.example.ungdungngenhac.models.BaiHat;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.ArrayList;

public interface DataClient {
    @GET("SelectAll.php")
    Call<ArrayList<BaiHat>>SelectAll();


}
