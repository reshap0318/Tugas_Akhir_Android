package com.minangdev.m_mahasiswa.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.minangdev.m_mahasiswa.R
import com.minangdev.m_mahasiswa.View.annaouncement.AnnouncementFragment
import com.minangdev.m_mahasiswa.View.home.HomeFragment
import com.minangdev.m_mahasiswa.View.profile.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.actionbar_main.*

class MainActivity : AppCompatActivity() {

    private var mFrameLayout: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        menu_notification.setOnClickListener{
            val intent = Intent(this, notificationActivity::class.java)
            startActivity(intent)
        }

        val fragment = HomeFragment.newInstance()
        addFragment(fragment)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val fragment = HomeFragment.newInstance()
                addFragment(fragment)
                true
            }
            R.id.navigation_announcement -> {
                val fragment = AnnouncementFragment()
                addFragment(fragment)
                true
            }
            R.id.navigation_resource -> {
                Toast.makeText(this, "resource", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.navigation_profile -> {
                val fragment = ProfileFragment()
                addFragment(fragment)
                true
            }
            else -> false
        }
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                //.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.frame_layout, fragment, fragment.javaClass.getSimpleName())
                .commit()
    }
}