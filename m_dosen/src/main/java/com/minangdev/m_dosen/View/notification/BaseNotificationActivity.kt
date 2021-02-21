package com.minangdev.m_dosen.View.notification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.minangdev.m_dosen.Helper.LoadingDialog
import com.minangdev.m_dosen.Helper.SharePreferenceManager
import com.minangdev.m_dosen.R
import com.minangdev.m_dosen.View.bimbingan.*
import kotlinx.android.synthetic.main.actionbar_onlyback.*
import kotlinx.android.synthetic.main.activity_base_notification.*

class BaseNotificationActivity : AppCompatActivity() {
    private lateinit var token : String
    private lateinit var sharePreference : SharePreferenceManager
    lateinit var loadingDialog: LoadingDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_notification)

        mToolbarBlank.setTitle("Notification")
        setSupportActionBar(mToolbarBlank)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mToolbarBlank.setNavigationOnClickListener{
            onBackPressed()
        }

        sharePreference = SharePreferenceManager(this)
        sharePreference.isLogin()
        token = sharePreference.getToken()

        setupViewPager(notification_viewPagger)

    }

    fun setupViewPager(viewPager: ViewPager){
        val adapter = BimbinganPagerAdapter(supportFragmentManager)
        adapter.addFragment(AnnouncementFragment(), "Announcement")
        adapter.addFragment(AnotherNotificationFragment(), "KRS")
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 2
        notification_tab.setupWithViewPager(viewPager)
    }
}