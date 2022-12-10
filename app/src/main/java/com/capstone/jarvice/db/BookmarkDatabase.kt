package com.capstone.jarvice.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BookmarkJobList::class], version = 1, exportSchema = false)
abstract class BookmarkDatabase : RoomDatabase() {
    companion object {
        @Volatile
        private var instance : BookmarkDatabase? = null

        fun getDatabase(context: Context): BookmarkDatabase? {
            if (instance == null) {
                synchronized(BookmarkDatabase::class) {
                    instance = Room.databaseBuilder(context.applicationContext, BookmarkDatabase::class.java, "bookmark_job").build()
                }
            }
            return instance
        }
    }
    abstract fun bookmarkDao(): BookmarkDao
}