package com.capstone.jarvice.ui.bottomNavigation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.jarvice.network.ApiConfig
import com.capstone.jarvice.network.JobsItem
import com.capstone.jarvice.network.Response
import retrofit2.Call
import retrofit2.Callback

class HomeViewModel : ViewModel() {
    private val _job = MutableLiveData<List<JobsItem>>()
    val job: LiveData<List<JobsItem>> = _job

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _toast = MutableLiveData<String>()
    val toast: LiveData<String> = _toast

    fun getBannerJob() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getBannerJob()
        client.enqueue(object : Callback<Response>{
            override fun onResponse(
                call: Call<Response>,
                response: retrofit2.Response<Response>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _job.value = response.body()?.jobs
                }
            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                _toast.value = t.message.toString()
            }

        })
    }

}