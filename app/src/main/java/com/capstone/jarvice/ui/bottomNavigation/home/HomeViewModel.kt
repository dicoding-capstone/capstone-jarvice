package com.capstone.jarvice.ui.bottomNavigation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.jarvice.network.*
import com.google.firebase.database.FirebaseDatabase
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

    private val _listSearch = MutableLiveData<List<ListJobsItem>>()
    val listSearch: MutableLiveData<List<ListJobsItem>> = _listSearch

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

    fun firebaseSearch(search: String?) {
        _isLoading.value = true
        val keyword : String
        if (search == null) {
            keyword = "A"
        } else {
            keyword = search
        }
        val dataSearch = ArrayList<ListJobsItem>()
        FirebaseDatabase.getInstance().reference.child("jobsearch")
            .orderByChild("name").startAt(keyword.uppercase()).endAt(keyword.lowercase()+"\uf8ff")
            .get().addOnCompleteListener {
            it.result.children.forEach { snapshot ->
                val dataInsert: ListJobsItem? = snapshot.getValue(ListJobsItem::class.java)
                dataSearch.add(dataInsert!!)
            }
            _listSearch.value = dataSearch
        }

    }

//    fun searchJob(search: String?) {
//        if (search != null){
//            _jobSearch.value = search
//        }
//        Log.d("Data Search Model", jobSearch.value.toString())
//    }
}