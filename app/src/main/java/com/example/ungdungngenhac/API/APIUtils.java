package com.example.ungdungngenhac.API;

public class APIUtils {
    public static final String Base_Url = "http://apimp3savvy.000webhostapp.com/";

    public static DataClient getData(){

        return RetrofitClient.getClient(Base_Url).create(DataClient.class);
    }
}
