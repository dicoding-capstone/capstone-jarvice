package com.capstone.jarvice.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.jarvice.model.UserPreference
import com.capstone.jarvice.ui.detail.DetailViewModel
import com.capstone.jarvice.ui.login.LoginViewModel
import com.capstone.jarvice.ui.main.MainViewModel

class ViewModelFactory(
    private val pref: UserPreference,
    private val application: Application,
    private val company: String
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(application, company) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}