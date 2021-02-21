package com.minangdev.m_mahasiswa.View.notification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.minangdev.m_mahasiswa.R
import com.minangdev.m_mahasiswa.Helper.LoadingDialog
import com.minangdev.m_mahasiswa.Helper.SharePreferenceManager
import com.minangdev.m_mahasiswa.ViewModel.AnnouncementViewHolder
import kotlinx.android.synthetic.main.actionbar_onlyback.*
import kotlinx.android.synthetic.main.activity_notification_detail.*

class NotificationDetailActivity : AppCompatActivity() {

    private lateinit var sharePreference : SharePreferenceManager
    private lateinit var announcementViewHolder: AnnouncementViewHolder
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var token : String
    private lateinit var id : String

    companion object {
        const val EXTRA_ID = "extra_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_detail)
        sharePreference = SharePreferenceManager(this)
        sharePreference.isLogin()
        token = sharePreference.getToken()
        announcementViewHolder = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(AnnouncementViewHolder::class.java)
        id = intent.getStringExtra(EXTRA_ID).toString()

        loadingDialog = LoadingDialog(this)

        loadingDialog.showLoading()
        announcementViewHolder.setData(token, id)
        announcementViewHolder.getData().observe(this, Observer {
            title_form_announcement.editText?.setText(it.getString("title"))
            description_form_announcement.editText?.setText(it.getString("description"))
            tv_sending_announcement.text = "By Admin "+it.getString("unit")
            tv_date_announcement.text = it.getString("tanggal")
            loadingDialog.hideLoading()
        })

        mToolbarBlank.setTitle("Detail Notification")
        setSupportActionBar(mToolbarBlank)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mToolbarBlank.setNavigationOnClickListener{
            onBackPressed()
        }
    }
}