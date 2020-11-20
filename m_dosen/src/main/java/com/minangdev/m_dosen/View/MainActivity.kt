package com.minangdev.m_dosen.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.minangdev.m_dosen.Helper.SharePreferenceManager
import com.minangdev.m_dosen.R
import com.minangdev.m_dosen.View.NotificationActivity
import com.minangdev.m_dosen.View.home.HomeFragment
import com.minangdev.m_dosen.View.profile.ProfileFragment
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
        semester.text = sharePreference.getSemester()

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