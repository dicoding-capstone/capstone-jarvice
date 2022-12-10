package com.capstone.jarvice.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addBookmark(bookmarkJobList: BookmarkJobList)

    @Query("SELECT * FROM bookmark_job_list")
    fun getBookmark(): LiveData<List<BookmarkJobList>>

    @Delete
    fun delete(bookmarkJobList: BookmarkJobList)

    @Query("SELECT EXISTS(SELECT * FROM bookmark_job_list WHERE company = :company)")
    fun checkBookmark(company: String): LiveData<Boolean>
}