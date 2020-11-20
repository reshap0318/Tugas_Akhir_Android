package com.minangdev.myta.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.minangdev.myta.Helper.SharePreferenceManager
import com.minangdev.myta.R
import com.minangdev.myta.View.annaouncement.AnnouncementFragment
import com.minangdev.myta.View.home.HomeFragment
import com.minangdev.myta.View.profile.ProfileFragment
import com.minangdev.myta.View.topic.TopicFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.actionbar_main.*

class MainActivity : AppCompatActivity() {

    private var mFrameLayout: FrameLayout? = null

    companion object {
        const val EXTRA_FRAGMENT = "extra_fragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharePreference = SharePreferenceManager(this)
        sharePreference.isLogin()
        semester.text = sharePreference.getSemester();

        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        menu_notification.setOnClickListener{
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }
        val idFragment = intent.getIntExtra(EXTRA_FRAGMENT, 0)
        if(idFragment>0){
            changeFragment(idFragment)
        }else{
            changeFragment(1)
        }

    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val fragment = HomeFragment()
                addFragment(fragment)
                true
            }
            R.id.navigation_announcement -> {
                val fragment = AnnouncementFragment()
                addFragment(fragment)
                true
            }
            R.id.navigation_topic -> {
                val fragment = TopicFragment()
                addFragment(fragment)
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

    private fun changeFragment(idFragment:Int){
        when(idFragment){
            1 -> {
                bottom_navigation.selectedItemId = R.id.navigation_home
                true
            }
            2 -> {
                bottom_navigation.selectedItemId = R.id.navigation_announcement
                true
            }
            3 -> {
                bottom_navigation.selectedItemId = R.id.navigation_topic
                true
            }
            4 -> {
                bottom_navigation.selectedItemId = R.id.navigation_profile
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