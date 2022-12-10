package com.capstone.jarvice.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.capstone.jarvice.db.BookmarkJobList
import com.capstone.jarvice.repository.BookmarkRepository

class DetailViewModel(application: Application, company: String) :
    AndroidViewModel(application) {
    private val mBookmarkRepository: BookmarkRepository =
        BookmarkRepository(application)

    val bookmarked: LiveData<Boolean> = mBookmarkRepository.check(company)

    fun insert(bookmarkJobList: BookmarkJobList) {
        mBookmarkRepository.insert(bookmarkJobList)
    }

    fun delete(bookmarkJobList: BookmarkJobList) {
        mBookmarkRepository.delete(bookmarkJobList)
    }

    fun checkBookmark(): Boolean? {
        return bookmarked.value
    }

}