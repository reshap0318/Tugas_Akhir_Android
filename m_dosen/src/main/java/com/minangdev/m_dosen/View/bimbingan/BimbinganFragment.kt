package com.minangdev.m_dosen.View.bimbingan

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
import com.minangdev.m_dosen.Adapter.MahasiswaBimbinganAdapter
import com.minangdev.m_dosen.Helper.LoadingDialog
import com.minangdev.m_dosen.Helper.SharePreferenceManager
import com.minangdev.m_dosen.R
import com.minangdev.m_dosen.ViewModel.MahasiswaBimbinganViewModel
import kotlinx.android.synthetic.main.fragment_bimbingan.view.*

class BimbinganFragment : Fragment() {

    private lateinit var root : View
    private lateinit var sharePreference : SharePreferenceManager
    private lateinit var mahasiswaBimbinganAdapter: MahasiswaBimbinganAdapter
    private lateinit var mahasiswaBimbinganViewModel: MahasiswaBimbinganViewModel
    lateinit var loadingDialog: LoadingDialog

    lateinit var token: String

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        root = inflater.inflate(R.layout.fragment_bimbingan, container, false)
        sharePreference = SharePreferenceManager(root.context)
        sharePreference.isLogin()
        token = sharePreference.getToken()

        mahasiswaBimbinganAdapter = MahasiswaBimbinganAdapter {
            val intent = Intent(activity, BaseDetailBimbinganActivity::class.java)
            intent.putExtra("nim", it.getString("nim"))
            activity?.startActivity(intent)
        }
        val layoutManager = LinearLayoutManager(activity)
        root.rv_bimbingan.adapter = mahasiswaBimbinganAdapter
        root.rv_bimbingan.layoutManager = layoutManager

        mahasiswaBimbinganViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MahasiswaBimbinganViewModel::class.java)

        loadingDialog = LoadingDialog(activity!!)
        loadingDialog.showLoading()
        mahasiswaBimbinganViewModel.setData(token)
        mahasiswaBimbinganViewModel.getData().observe(this, Observer { datas ->
          mahasiswaBimbinganAdapter.setData(datas)
          loadingDialog.hideLoading()
        })
        return root
    }
}