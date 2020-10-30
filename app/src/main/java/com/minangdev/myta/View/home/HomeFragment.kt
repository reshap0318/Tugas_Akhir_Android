package com.minangdev.myta.View.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.minangdev.myta.Helper.SharePreferenceManager
import com.minangdev.myta.R
import com.minangdev.myta.View.profile.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel : ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        homeSetData(root, name="", nip="")
        val sharePreference = activity?.let { SharePreferenceManager(it) }
        sharePreference?.isLogin()
        val token = sharePreference?.getToken()
        homeViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(ProfileViewModel::class.java)

        if(token != null){
            homeViewModel.setData(token)
            homeViewModel.getData().observe(this, Observer {data ->
                val name = data.getString("name")
                val nip = data.getString("email")
                homeSetData(root, name=name, nip=nip)
            })
        }
        return root
    }

    private fun homeSetData(root: View, name:String, nip:String, img:String? = null) {
        root.tv_name_home.text = name
        root.tv_nip_home.text = nip
    }
}