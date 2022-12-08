package com.capstone.jarvice.network

import com.google.gson.annotations.SerializedName

data class Response(

	@field:SerializedName("Jobs")
	val jobs: List<JobsItem>
)

data class JobsItem(

	@field:SerializedName("image")
	val image: String,

	@field:SerializedName("skills")
	val skills: List<String>? = null,

	@field:SerializedName("promopic")
	val promopic: String? = null,

	@field:SerializedName("web")
	val web: String? = null,

	@field:SerializedName("fulltime")
	val fulltime: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("company")
	val company: String? = null,

	@field:SerializedName("location")
	val location: String? = null,

	@field:SerializedName("category")
	val category: String? = null,

	@field:SerializedName("experience")
	val experience: String? = null,

	@field:SerializedName("salary")
	val salary: List<Int>? = null
)
