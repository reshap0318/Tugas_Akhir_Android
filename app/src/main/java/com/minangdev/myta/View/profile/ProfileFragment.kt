package com.minangdev.myta.View.profile

import android.content.Intent
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
import com.minangdev.myta.View.ChangePasswordActivity
import kotlinx.android.synthetic.main.fragment_profile.view.*


class ProfileFragment : Fragment() {

    private lateinit var profileViewModel : ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        root.btn_ganti_password_profile.setOnClickListener { view ->
            val intent = Intent(activity, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        profileSetData(root, name="", nip="")
        val sharePreference = activity?.let { SharePreferenceManager(it) }
        sharePreference?.isLogin()
        val token = sharePreference?.getToken()
        profileViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(ProfileViewModel::class.java)

        if(token != null){
            profileViewModel.setData(token)
            profileViewModel.getData().observe(this, Observer {data ->
                Log.e("home fragment","test")
                val name = data.getString("name")
                val nip = data.getString("email")
                profileSetData(root, name=name, nip=nip)
            })
        }

        return root
    }

    private fun profileSetData(root: View, name:String, nip:String, img:String? = null) {
        root.tv_name_profile.text = name
        root.tv_nip_profile.text = nip
    }
}