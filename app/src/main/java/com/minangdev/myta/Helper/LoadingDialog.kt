package com.minangdev.myta.Helper

import android.app.Activity
import android.app.AlertDialog
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.minangdev.myta.R

class LoadingDialog {

    val activity : Activity
    val dialog: AlertDialog

    constructor(activity: Activity){
        this.activity = activity
        val builder = AlertDialog.Builder(activity)
        val mLayout = LayoutInflater.from(activity).inflate(R.layout.dialog_loading, null)
        builder.setView(mLayout)
        builder.setCancelable(false)
        this.dialog = builder.create()
    }

    fun showLoading(){
        dialog.show()
    }

    fun hideLoading(){
        dialog.dismiss()
    }


}