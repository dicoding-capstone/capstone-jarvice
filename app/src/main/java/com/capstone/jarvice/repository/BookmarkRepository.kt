package com.capstone.jarvice.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.capstone.jarvice.db.BookmarkDao
import com.capstone.jarvice.db.BookmarkDatabase
import com.capstone.jarvice.db.BookmarkJobList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BookmarkRepository(application: Application) {
    private var bookmarkDao: BookmarkDao?
    private var bookmarkDb: BookmarkDatabase?
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        bookmarkDb = BookmarkDatabase.getDatabase(application)
        bookmarkDao = bookmarkDb?.bookmarkDao()
    }

    fun getAllBookmark(): LiveData<List<BookmarkJobList>> = bookmarkDao!!.getBookmark()

    fun insert(bookmarkJobList: BookmarkJobList) {
        executorService.execute {bookmarkDao?.addBookmark(bookmarkJobList)}
    }

    fun delete(bookmarkJobList: BookmarkJobList) {
        executorService.execute {bookmarkDao?.delete(bookmarkJobList)}
    }

    fun check(company: String): LiveData<Boolean> =
        bookmarkDao!!.checkBookmark(company)
}