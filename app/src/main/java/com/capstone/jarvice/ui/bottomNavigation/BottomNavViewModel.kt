package com.capstone.jarvice.ui.bottomNavigation

import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.capstone.jarvice.model.UserNetwork
import com.capstone.jarvice.model.UserPreference
import com.capstone.jarvice.ui.ViewModelFactory
import com.capstone.jarvice.ui.main.MainViewModel
import com.google.firebase.database.FirebaseDatabase

class ExploreViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Explore Fragment"
    }
    val text: LiveData<String> = _text
}

class BookmarkViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Bookmark Fragment"
    }
    val text: LiveData<String> = _text
}