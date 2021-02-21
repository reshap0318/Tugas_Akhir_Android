package com.minangdev.m_dosen.View.home

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
import com.minangdev.m_dosen.Adapter.BimbinganHomeAdapter
import com.minangdev.m_dosen.Helper.LoadingDialog
import com.minangdev.m_dosen.Helper.SharePreferenceManager
import com.minangdev.m_dosen.R
import com.minangdev.m_dosen.View.bimbingan.BimbinganDetailChatActivity
import com.minangdev.m_dosen.View.bimbingan.BimbinganDetailGroupActivity
import com.minangdev.m_dosen.ViewModel.BimbinganViewModel
import com.minangdev.m_dosen.ViewModel.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.json.JSONObject

class HomeFragment : Fragment() {

    private lateinit var homeViewModel : ProfileViewModel
    private lateinit var bimbinganViewModel : BimbinganViewModel
    private lateinit var bimbinganAdapter: BimbinganHomeAdapter
    private lateinit var sharePreference : SharePreferenceManager
    lateinit var loadingDialog: LoadingDialog

    private lateinit var root : View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_home, container, false)
        sharePreference = SharePreferenceManager(root.context)
        sharePreference.isLogin()
        val token = sharePreference.getToken()

        homeSetData(name="", nip="")

        homeViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(ProfileViewModel::class.java)
        bimbinganViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(BimbinganViewModel::class.java)
        loadingDialog = LoadingDialog(activity!!)

        loadingDialog.showLoading()
        homeViewModel.setData(token)
        homeViewModel.getData().observe(this, Observer {data ->
            val name = data.getString("name")
            val nip = data.getString("username")
            val img = data.getString("avatar")
            homeSetData(name=name, nip=nip, img=img)
            loadingDialog.hideLoading()
        })

        bimbinganAdapter = BimbinganHomeAdapter {
            moveTodetailActivity(it)
        }
        val layoutManager = LinearLayoutManager(activity)
        root.rv_all_bimbingan_home.adapter = bimbinganAdapter
        root.rv_all_bimbingan_home.layoutManager = layoutManager

        loadingDialog.showLoading()
        bimbinganViewModel.loadLastSeenData(token)
        bimbinganViewModel.getLastSeenData().observe(this, Observer {
            bimbinganAdapter.setData(it)
            loadingDialog.hideLoading()
            root.refresh_last_seen_bimbingan_home.isRefreshing = false
        })

        loadingDialog.showLoading()
        bimbinganViewModel.setGroupData(token)
        bimbinganViewModel.getGroupData().observe(this, Observer { item ->
            root.tv_nama_group_bimbingan_home.text = item.getString("groupName")
            root.tv_topik_group_bimbingan_home.text = "Chanel " + item.getString("groupChanel")
            Glide.with(root)
                .load(item.getString("groupAvatar"))
                .fitCenter()
                .centerCrop()
                .into(root.img_avatar_group_bimbingan_home)
            root.row_group_chat_home.setOnClickListener {
                val intent = Intent(activity, BimbinganDetailGroupActivity::class.java)
                intent.putExtra("data", item.toString())
                startActivity(intent)
            }
            loadingDialog.hideLoading()
        })

        root.refresh_last_seen_bimbingan_home.setOnRefreshListener {
            bimbinganViewModel.loadLastSeenData(token)
        }

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

    private fun moveTodetailActivity(it: JSONObject) {
        val intent = Intent(activity, BimbinganDetailChatActivity::class.java)
        intent.putExtra("receiverId", it.getString("to"))
        intent.putExtra("topicPeriodId",it.getString("topicPeriodId"))
        intent.putExtra("receiverNama",it.getString("namaUser"))
        intent.putExtra("receiverAvatar",it.getString("avataUser"))
        val topicPeriod = it.getString("topic") + " - " + it.getString("period")
        intent.putExtra("topicPeriod", topicPeriod)
        startActivity(intent)
    }
}