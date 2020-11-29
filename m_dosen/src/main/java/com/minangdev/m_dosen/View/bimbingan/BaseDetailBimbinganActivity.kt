package com.minangdev.m_dosen.View.bimbingan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.minangdev.m_dosen.Helper.SharePreferenceManager
import com.minangdev.m_dosen.R
import com.minangdev.m_dosen.View.MainActivity
import com.minangdev.m_dosen.ViewModel.MahasiswaBimbinganViewModel
import com.minangdev.m_dosen.ViewModel.SKSViewModel
import kotlinx.android.synthetic.main.actionbar_onlyback.*
import kotlinx.android.synthetic.main.activity_base_detail_bimbingan.*

class BaseDetailBimbinganActivity : AppCompatActivity() {
    private lateinit var nim: String
    private lateinit var token : String

    private lateinit var mahasiswaBimbinganViewModel: MahasiswaBimbinganViewModel
    private lateinit var mahasiswaBimbinganSksViewModel: SKSViewModel
    private lateinit var sharePreference : SharePreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_detail_bimbingan)
        btn_appbar_back.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(MainActivity.EXTRA_FRAGMENT, 2)
            startActivity(intent)
        }

        nim = intent.getStringExtra("nim").toString()
        setupViewPager(detail_bimbingan_view_pager)

        sharePreference = SharePreferenceManager(this)
        sharePreference.isLogin()
        token = sharePreference.getToken()


        mahasiswaBimbinganViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MahasiswaBimbinganViewModel::class.java)
        mahasiswaBimbinganViewModel.setDataDetail(token, nim!!)
        mahasiswaBimbinganViewModel.getDataDetail().observe(this, Observer { data ->
            tv_name_mahasiswa_bimbingan.text = data.getString("nama")
            tv_nim_mahasiswa_bimbingan.text = nim
            val img = data.getString("avatar")
            Glide.with(this)
                    .load(img)
                    .fitCenter()
                    .centerCrop()
                    .into(img_avatar_mahasiswa_bimbingan)
        })

        mahasiswaBimbinganSksViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(SKSViewModel::class.java)
        mahasiswaBimbinganSksViewModel.setData(token, nim)
        mahasiswaBimbinganSksViewModel.getData().observe(this, Observer { data ->
            tv_nim_mahasiswa_bimbingan.text = nim + " | " + data.getString("sks_diambil") + "/" + data.getString("total_sks")
        })

    }

    fun setupViewPager(viewPager: ViewPager){
        val adapter = BimbinganPagerAdapter(supportFragmentManager)
        adapter.addFragment(BimbinganDetailFragment.newInstance(nim), "Bimbingan")
        adapter.addFragment(BimbinganKrsFragment.newInstance(nim), "KRS")
        adapter.addFragment(BimbinganTranskripFragment.newInstance(nim), "Transkrip")
        adapter.addFragment(BimbinganGraficFragment.newInstance(nim), "Grafic")
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 4
        detail_bimbingan_tab.setupWithViewPager(viewPager)
    }
}