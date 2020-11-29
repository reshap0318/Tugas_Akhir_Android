package com.minangdev.m_dosen.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.minangdev.m_dosen.Adapter.NotificationAdapter
import com.minangdev.m_dosen.Helper.SharePreferenceManager
import com.minangdev.m_dosen.R
import com.minangdev.m_dosen.ViewModel.FirebaseViewModel
import kotlinx.android.synthetic.main.actionbar_onlyback.*
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : AppCompatActivity() {
    private lateinit var sharePreference : SharePreferenceManager
    private lateinit var firebaseViewModel: FirebaseViewModel
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var api_fcm : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        sharePreference = SharePreferenceManager(this)
        sharePreference.isLogin()
        api_fcm = sharePreference.getFCMTOKEN()
        firebaseViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(FirebaseViewModel::class.java)

        notificationAdapter = NotificationAdapter()
        val layoutManager = LinearLayoutManager(this)
        rv_notification.adapter = notificationAdapter
        rv_notification.layoutManager = layoutManager

        firebaseViewModel.setData(api_fcm)
        firebaseViewModel.getData().observe(this, Observer { datas ->
            notificationAdapter.setData(datas)
        })

        btn_appbar_back.setOnClickListener{
            onBackPressed()
        }

    }
}