package com.minangdev.m_dosen.View.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.minangdev.m_dosen.R
import com.minangdev.m_dosen.View.ChangePasswordActivity
import kotlinx.android.synthetic.main.fragment_profile.view.*


class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        root.btn_ganti_password.setOnClickListener { view ->
            val intent = Intent(activity, ChangePasswordActivity::class.java)
            startActivity(intent)
        }
        return root
    }
}