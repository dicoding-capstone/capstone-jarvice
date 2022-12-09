package com.capstone.jarvice.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class DetailViewModel(application: Application) :
    AndroidViewModel(application) {
//    private var bookmarkDao: BookmarkDao?
//    private var bookmarkDb: BookmarkDatabase?
//
//    init {
//        bookmarkDb = BookmarkDatabase.getDatabase(application)
//        bookmarkDao = bookmarkDb?.bookmarkDao()
//    }
//
//    fun addBookmark(
//        image: String,
//        name: String,
//        company: String,
//    ) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val bookmark = BookmarkList(
//                name, company, image
//            )
//            bookmarkDao?.addBookmark(bookmark)
//        }
//    }
//
//    suspend fun bookmarkCheck(name: String) = bookmarkDao?.checkBookmarkList(name)
//
//    fun removeBookmark(name: String) {
//        viewModelScope.launch {
//            removeBm(name)
//        }
//    }
//
//    private suspend fun removeBm(name: String) = withContext(Dispatchers.IO) {
//        bookmarkDao?.removeBookmarkList(name)
//    }

}