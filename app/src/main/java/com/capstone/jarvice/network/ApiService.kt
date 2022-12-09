package com.capstone.jarvice.network

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("imagelist.json")
    fun getBannerJob(
    ): Call<Response>

    @GET("joblist.json")
    fun getListJob(
    ): Call<ListJobResponse>
}