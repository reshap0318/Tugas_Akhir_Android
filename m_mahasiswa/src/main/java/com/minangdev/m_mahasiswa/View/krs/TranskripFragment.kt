package com.minangdev.m_mahasiswa.View.krs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.minangdev.m_mahasiswa.Adapter.TranskripMainAdapter
import com.minangdev.m_mahasiswa.Helper.SharePreferenceManager
import com.minangdev.m_mahasiswa.R
import com.minangdev.m_mahasiswa.ViewModel.SKSViewModel
import com.minangdev.m_mahasiswa.ViewModel.TranskripViewModel
import kotlinx.android.synthetic.main.fragment_transkrip.*
import kotlinx.android.synthetic.main.fragment_transkrip.view.*
import java.text.DecimalFormat

class TranskripFragment : Fragment() {

    private lateinit var root : View
    private lateinit var sharePreference : SharePreferenceManager
    private lateinit var transkripViewModel : TranskripViewModel
    private lateinit var sksViewModel : SKSViewModel
    private lateinit var transkripMainAdapter: TranskripMainAdapter
    lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_transkrip, container, false)
        sharePreference = SharePreferenceManager(root.context)
        sharePreference.isLogin()
        token = sharePreference.getToken()

        transkripViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(TranskripViewModel::class.java)
        sksViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(SKSViewModel::class.java)

        transkripMainAdapter = TranskripMainAdapter(context!!)
        val layoutManager = LinearLayoutManager(activity)
        root.rv_transkrip.adapter = transkripMainAdapter
        root.rv_transkrip.layoutManager = layoutManager

        transkripViewModel.setData(token)
        transkripViewModel.getData().observe(this, Observer { datas ->
            transkripMainAdapter.setData(datas)
        })

        sksViewModel.setData(token)
        val df = DecimalFormat("#.##")
        sksViewModel.getData().observe(this, Observer {data ->
            val ipk = data.getString("ipk").toFloat()
            tv_total_sks.text = "Total SKS : "+data.getString("total_sks")
            tv_ipk_transkrip.text = "IPK : "+df.format(ipk).toString()
        })

        return root
    }


}