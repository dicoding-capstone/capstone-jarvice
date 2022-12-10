package com.capstone.jarvice.ui.bottomNavigation.bookmark

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.capstone.jarvice.db.BookmarkJobList
import com.capstone.jarvice.repository.BookmarkRepository

class BookmarkViewModel(application: Application) : ViewModel() {
    private val mBookmarkRepository: BookmarkRepository =
        BookmarkRepository(application)

    val bookmarkList: LiveData<List<BookmarkJobList>> = mBookmarkRepository.getAllBookmark()
}