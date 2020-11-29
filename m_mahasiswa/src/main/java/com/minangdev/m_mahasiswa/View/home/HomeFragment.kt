package com.minangdev.m_mahasiswa.View.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.minangdev.m_mahasiswa.Adapter.KrsAdapter
import com.minangdev.m_mahasiswa.Helper.SharePreferenceManager
import com.minangdev.m_mahasiswa.R
import com.minangdev.m_mahasiswa.View.NotificationActivity
import com.minangdev.m_mahasiswa.ViewModel.FirebaseViewModel
import com.minangdev.m_mahasiswa.ViewModel.KrsViewModel
import com.minangdev.m_mahasiswa.ViewModel.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_krs.view.*
import kotlinx.android.synthetic.main.row_notification.view.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel : ProfileViewModel
    private lateinit var root : View
    private lateinit var sharePreference : SharePreferenceManager
    private lateinit var krsViewModel: KrsViewModel
    private lateinit var krsAdapter : KrsAdapter
    private lateinit var firebaseViewModel: FirebaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_home, container, false)
        sharePreference = SharePreferenceManager(root.context)
        sharePreference.isLogin()
        val token = sharePreference.getToken()
        val semesterActive = sharePreference.getSemesterActive().get(sharePreference.IDSEMESTER)
        val api_fcm = sharePreference.getFCMTOKEN()

        homeSetData(name="", nip="")

        homeViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(ProfileViewModel::class.java)
        krsViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(KrsViewModel::class.java)
        firebaseViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(FirebaseViewModel::class.java)

        krsAdapter = KrsAdapter{  }
        krsAdapter.notifyDataSetChanged()
        val layoutManager = LinearLayoutManager(activity)
        root.rv_krs_home.adapter = krsAdapter
        root.rv_krs_home.layoutManager = layoutManager

        homeViewModel.setData(token)
        homeViewModel.getData().observe(this, Observer {data ->
            val name = data.getString("name")
            val nip = data.getString("username")
            val img = data.getString("avatar")
            homeSetData(name=name, nip=nip, img=img)
        })

        krsViewModel.setDataSemester(token, semesterActive!!)
        krsViewModel.getDataSemester().observe(this, Observer { datas ->
            krsAdapter.setData(datas.getJSONArray("krs"))
        })

        root.tv_announcement_home.setOnClickListener{
            val intent = Intent(activity, NotificationActivity::class.java)
            startActivity(intent)
        }

        root.tv_krs_home.setOnClickListener{
            Toast.makeText(activity, "Belum Berfungsi", Toast.LENGTH_SHORT).show()
        }

        firebaseViewModel.setData(api_fcm)
        firebaseViewModel.getData().observe(this, Observer { datas ->
            if(datas.length()>0){
                root.tv_label_notification.text = datas.getJSONObject(0).getString("title")
                root.tv_date_notification.text = datas.getJSONObject(0).getString("tanggal")
                root.tv_time_notification.text = datas.getJSONObject(0).getString("waktu")
            }
        })

        return root
    }

    private fun homeSetData(name:String, nip:String, img:String? = null) {
        root.tv_name_home.text = name
        root.tv_nip_home.text = nip
        Glide.with(root)
            .load(img)
            .fitCenter()
            .centerCrop()
            .into(root.img_profile_home)
    }
}