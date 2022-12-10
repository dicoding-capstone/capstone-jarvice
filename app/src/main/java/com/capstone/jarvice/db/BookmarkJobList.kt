package com.capstone.jarvice.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "bookmark_job_list")
class BookmarkJobList(
    @field:ColumnInfo(name = "company")
    @field:PrimaryKey
    val company: String,

    @field:ColumnInfo(name = "jobName")
    val jobName: String,

    @field:ColumnInfo(name = "image")
    val image: String,

    @field:ColumnInfo(name = "web")
    val web: String,

    @field:ColumnInfo(name = "fulltime")
    val fulltime: String,

    @field:ColumnInfo(name = "category")
    val category: String,

    @field:ColumnInfo(name = "experience")
    val experience: String,

    @field:ColumnInfo(name = "location")
    val location: String
) : Serializable
