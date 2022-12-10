package com.capstone.jarvice.ui.bottomNavigation.home

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.capstone.jarvice.R
import com.capstone.jarvice.network.*
import retrofit2.Call
import retrofit2.Callback

class HomeViewModel : ViewModel() {
    private val _job = MutableLiveData<List<JobsItem>>()
    val job: LiveData<List<JobsItem>> = _job

    private val _listJob = MutableLiveData<List<ListJobsItem>>()
    val listJob: LiveData<List<ListJobsItem>> = _listJob

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _toast = MutableLiveData<String>()
    val toast: LiveData<String> = _toast

    private val _jobSearch = MutableLiveData<String>()
    val jobSearch: LiveData<String> = _jobSearch

    private val _listSearch = MutableLiveData<MutableList<ListJobsItem>>()
    val listSearch: LiveData<MutableList<ListJobsItem>> = _listSearch

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

    fun getListJob() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getListJob()
        client.enqueue(object : Callback<ListJobResponse>{
            override fun onResponse(
                call: Call<ListJobResponse>,
                response: retrofit2.Response<ListJobResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listJob.value = response.body()?.jobs
                }
            }

            override fun onFailure(call: Call<ListJobResponse>, t: Throwable) {
                _toast.value = t.message.toString()
            }

        })
    }

    fun getSearchJob(search: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getSearchJob("\"nama\"", "\"$search\"")
        client.enqueue(object : Callback<MutableList<ListJobsItem>>{
            override fun onResponse(
                call: Call<MutableList<ListJobsItem>>,
                response: retrofit2.Response<MutableList<ListJobsItem>>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listSearch.value = response.body()
                }
            }

            override fun onFailure(call: Call<MutableList<ListJobsItem>>, t: Throwable) {
                _toast.value = t.message.toString()
            }

        })
    }

    fun searchJob(search: String, fragment: Fragment) {
        findNavController(fragment).navigate(R.id.navigation_explore)
        _jobSearch.value = search
    }

    companion object {
        const val SENDING_JOB_NAME = "sending job name"
    }
}