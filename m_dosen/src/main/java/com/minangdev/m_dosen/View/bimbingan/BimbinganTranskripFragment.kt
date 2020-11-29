package com.minangdev.m_dosen.View.bimbingan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.minangdev.m_dosen.Adapter.TranskripMainAdapter
import com.minangdev.m_dosen.Helper.SharePreferenceManager
import com.minangdev.m_dosen.R
import com.minangdev.m_dosen.ViewModel.TranskripViewModel
import kotlinx.android.synthetic.main.fragment_bimbingan_transkrip.view.*

class BimbinganTranskripFragment : Fragment() {
    private lateinit var root : View
    private var nim: String? = null
    private lateinit var token : String
    private lateinit var sharePreference : SharePreferenceManager
    private lateinit var transkripViewModel : TranskripViewModel
    private lateinit var transkripMainAdapter: TranskripMainAdapter

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
        root = inflater.inflate(R.layout.fragment_bimbingan_transkrip, container, false)
        sharePreference = SharePreferenceManager(root.context)
        sharePreference.isLogin()
        token = sharePreference.getToken()
        val semester = sharePreference.getSemesterActive().get(sharePreference.IDSEMESTER).toString()

        transkripViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(TranskripViewModel::class.java)
        transkripMainAdapter = TranskripMainAdapter(context!!)
        val layoutManager = LinearLayoutManager(activity)
        root.rv_transkrip_bimbingan.adapter = transkripMainAdapter
        root.rv_transkrip_bimbingan.layoutManager = layoutManager

        transkripViewModel.setData(token, nim!!)
        transkripViewModel.getData().observe(this, Observer { datas ->
            transkripMainAdapter.setData(datas)
        })

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(nim: String) =
            BimbinganTranskripFragment().apply {
                arguments = Bundle().apply {
                    putString("nim", nim)
                }
            }
    }
}