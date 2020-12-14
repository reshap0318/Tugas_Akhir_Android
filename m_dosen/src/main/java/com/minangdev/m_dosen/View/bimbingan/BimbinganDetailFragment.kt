package com.minangdev.m_dosen.View.bimbingan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.minangdev.m_dosen.Helper.LoadingDialog
import com.minangdev.m_dosen.Helper.SharePreferenceManager
import com.minangdev.m_dosen.R
import com.minangdev.m_dosen.ViewModel.MahasiswaBimbinganViewModel
import kotlinx.android.synthetic.main.activity_base_detail_bimbingan.*
import kotlinx.android.synthetic.main.row_mahasiswa.view.*


class BimbinganDetailFragment : Fragment() {
    private lateinit var root : View
    private var nim: String? = null
    private lateinit var token : String

    private lateinit var sharePreference : SharePreferenceManager
    lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            nim = it.getString("nim")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_bimbingan_detail, container, false)
        sharePreference = SharePreferenceManager(root.context)
        sharePreference.isLogin()
        token = sharePreference.getToken()

        loadingDialog = LoadingDialog(activity!!)
        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(nim: String) =
            BimbinganDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("nim", nim)
                }
            }
    }
}