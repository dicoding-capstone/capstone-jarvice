package com.capstone.jarvice.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.jarvice.model.UserModel
import com.capstone.jarvice.model.UserNetwork
import com.capstone.jarvice.model.UserPreference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class MainViewModel (private val pref: UserPreference) : ViewModel() {
    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

//    fun getUserNetwork(): LiveData<UserNetwork> =

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}