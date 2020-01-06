package com.example.ungdungngenhac.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BaiHat(
    @SerializedName("Title") val tenBaiHat: String, @SerializedName("Author") val tenCaSi: String,
    @SerializedName("Avatar") val anhBaiHat: String, @SerializedName("Url") val linkBaiHat: String
) : Serializable