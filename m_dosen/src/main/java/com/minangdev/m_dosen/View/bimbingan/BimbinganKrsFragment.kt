package com.minangdev.m_dosen.View.bimbingan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.minangdev.m_dosen.Adapter.KrsAdapter
import com.minangdev.m_dosen.Helper.SharePreferenceManager
import com.minangdev.m_dosen.R
import com.minangdev.m_dosen.ViewModel.KrsViewModel
import com.minangdev.m_dosen.ViewModel.MahasiswaBimbinganViewModel
import kotlinx.android.synthetic.main.fragment_bimbingan_krs.view.*

class BimbinganKrsFragment : Fragment() {
    private lateinit var root : View
    private var nim: String? = null
    private lateinit var token : String
    private lateinit var sharePreference : SharePreferenceManager
    private lateinit var krsAdapter: KrsAdapter
    private lateinit var krsViewModel: KrsViewModel

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
        root = inflater.inflate(R.layout.fragment_bimbingan_krs, container, false)
        sharePreference = SharePreferenceManager(root.context)
        sharePreference.isLogin()
        token = sharePreference.getToken()
        val semester = sharePreference.getSemesterActive().get(sharePreference.IDSEMESTER).toString()
        krsAdapter = KrsAdapter {

        }
        root.rv_bimbingan_krs.adapter = krsAdapter
        val layoutManager = LinearLayoutManager(activity)
        root.rv_bimbingan_krs.layoutManager = layoutManager

        krsViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(KrsViewModel::class.java)
        krsViewModel.setDataSemester(token, nim!!, semester)
        krsViewModel.getDataSemester().observe(this, Observer { data ->
            krsAdapter.setData(data.getJSONArray("krs"))
        })

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(nim: String) =
            BimbinganKrsFragment().apply {
                arguments = Bundle().apply {
                    putString("nim", nim)
                }
            }
    }
}