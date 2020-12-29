package com.minangdev.m_dosen.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.minangdev.m_dosen.Adapter.NotificationAdapter
import com.minangdev.m_dosen.Helper.LoadingDialog
import com.minangdev.m_dosen.Helper.SharePreferenceManager
import com.minangdev.m_dosen.R
import com.minangdev.m_dosen.ViewModel.AnnouncementViewHolder
import com.minangdev.m_dosen.ViewModel.FirebaseViewModel
import kotlinx.android.synthetic.main.actionbar_onlyback.*
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : AppCompatActivity() {
    private lateinit var sharePreference : SharePreferenceManager
    private lateinit var announcementViewHolder: AnnouncementViewHolder
    private lateinit var notificationAdapter: NotificationAdapter
    lateinit var loadingDialog: LoadingDialog
    private lateinit var token : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        sharePreference = SharePreferenceManager(this)
        sharePreference.isLogin()
        token = sharePreference.getToken()
        announcementViewHolder = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(AnnouncementViewHolder::class.java)

        notificationAdapter = NotificationAdapter{
            val intent = Intent(this, NotificationDetailActivity::class.java)
            intent.putExtra(NotificationDetailActivity.EXTRA_ID,it.getString("id"))
            startActivity(intent)
        }
        val layoutManager = LinearLayoutManager(this)
        rv_notification.adapter = notificationAdapter
        rv_notification.layoutManager = layoutManager

        loadingDialog = LoadingDialog(this)
        loadingDialog.showLoading()

        announcementViewHolder.setListData(token)
        announcementViewHolder.getListData().observe(this, Observer { datas ->
            notificationAdapter.setData(datas)
            loadingDialog.hideLoading()
        })

        mToolbarBlank.setTitle("Notification")
        setSupportActionBar(mToolbarBlank)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mToolbarBlank.setNavigationOnClickListener{
            onBackPressed()
        }

    }
}