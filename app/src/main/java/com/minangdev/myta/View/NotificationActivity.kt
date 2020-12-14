package com.minangdev.myta.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.minangdev.myta.Adapter.NotificationAdapter
import com.minangdev.myta.Helper.LoadingDialog
import com.minangdev.myta.Helper.SharePreferenceManager
import com.minangdev.myta.R
import com.minangdev.myta.View.annaouncement.FormAnnouncementActivity
import com.minangdev.myta.ViewModel.AnnouncementViewHolder
import com.minangdev.myta.ViewModel.FirebaseViewModel
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
        btn_appbar_back.setOnClickListener{
            onBackPressed()
        }

        sharePreference = SharePreferenceManager(this)
        sharePreference.isLogin()
        token = sharePreference.getToken()
        announcementViewHolder = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(AnnouncementViewHolder::class.java)

        notificationAdapter = NotificationAdapter{jsonObject->
            val intent = Intent(this, FormAnnouncementActivity::class.java)
            intent.putExtra(FormAnnouncementActivity.EXTRA_ID,jsonObject.getString("id"))
            intent.putExtra(FormAnnouncementActivity.EXTRA_ACTION, "detail")
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
    }
}