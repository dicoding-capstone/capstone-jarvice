package com.capstone.jarvice.model

data class UserModel(
    val nameUser: String? = null,
    val isLogin: Boolean? = null,
    val token: String? = null,
    val uidKey: String? = null
)

data class UserNetwork(
    val nameUser: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
    val keahlian: String? = null
)