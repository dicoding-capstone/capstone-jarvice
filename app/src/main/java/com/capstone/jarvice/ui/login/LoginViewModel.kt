package com.capstone.jarvice.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.jarvice.model.UserModel
import com.capstone.jarvice.model.UserPreference
import kotlinx.coroutines.launch

class LoginViewModel (private val pref: UserPreference) : ViewModel() {

    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }

}