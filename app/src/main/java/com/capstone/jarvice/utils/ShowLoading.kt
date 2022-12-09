package com.capstone.jarvice.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.view.View
import com.capstone.jarvice.R

class ShowLoading {
    fun showLoading(isLoading: Boolean, progressBar: View){
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }
}

class LoadingDialog(private val mActivity: Activity) {
    private lateinit var isDialog: AlertDialog
    @SuppressLint("InflateParams")
    fun startLoading(){
        val inflater = mActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.loading_item, null)
        val builder = AlertDialog.Builder(mActivity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.show()
    }
    fun dismissLoading(){
        isDialog.dismiss()
    }
}