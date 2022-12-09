package com.capstone.jarvice.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class UserModel(
    val nameUser: String? = null,
    val isLogin: Boolean? = null,
    val token: String? = null,
    val uidKey: String? = null
)

@Parcelize
data class UserNetwork(
    val nameUser: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
    val keahlian: String? = null,
    val method:Boolean? = null
): Parcelable