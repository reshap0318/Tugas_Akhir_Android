package com.minangdev.m_mahasiswa.Helper

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import com.minangdev.m_mahasiswa.R

class LoadingDialog {

    val activity : Activity
    val dialog: AlertDialog

    constructor(activity: Activity){
        this.activity = activity
        val builder = AlertDialog.Builder(activity)
        val mLayout = LayoutInflater.from(activity).inflate(R.layout.dialog_loading, null)
        builder.setView(mLayout)
        builder.setCancelable(true)
        this.dialog = builder.create()
    }

    fun showLoading(){
        dialog.show()
    }

    fun hideLoading(){
        dialog.dismiss()
    }


}