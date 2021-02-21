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
import com.minangdev.m_dosen.Helper.LoadingDialog
import com.minangdev.m_dosen.Helper.SharePreferenceManager
import com.minangdev.m_dosen.R
import com.minangdev.m_dosen.ViewModel.SKSViewModel
import com.minangdev.m_dosen.ViewModel.TranskripViewModel
import kotlinx.android.synthetic.main.fragment_bimbingan_transkrip.view.*
import java.text.DecimalFormat

class BimbinganTranskripFragment : Fragment() {
    private lateinit var root : View
    private var nim: String? = null
    private lateinit var token : String
    private lateinit var sharePreference : SharePreferenceManager
    private lateinit var transkripViewModel : TranskripViewModel
    private lateinit var transkripMainAdapter: TranskripMainAdapter
    private lateinit var sksViewModel : SKSViewModel
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
        root = inflater.inflate(R.layout.fragment_bimbingan_transkrip, container, false)
        sharePreference = SharePreferenceManager(root.context)
        sharePreference.isLogin()
        token = sharePreference.getToken()
        val semester = sharePreference.getSemesterActive().get(sharePreference.IDSEMESTER).toString()

        transkripViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(TranskripViewModel::class.java)
        sksViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(SKSViewModel::class.java)

        transkripMainAdapter = TranskripMainAdapter(context!!)
        val layoutManager = LinearLayoutManager(activity)
        root.rv_transkrip_bimbingan.adapter = transkripMainAdapter
        root.rv_transkrip_bimbingan.layoutManager = layoutManager

        loadingDialog = LoadingDialog(activity!!)
        loadingDialog.showLoading()
        transkripViewModel.setData(token, nim!!)
        transkripViewModel.getData().observe(this, Observer { datas ->
            transkripMainAdapter.setData(datas)
            loadingDialog.hideLoading()
            root.refresh_transkrip.isRefreshing = false
        })

        sksViewModel.setData(token, nim!!)
        val df = DecimalFormat("#.##")
        sksViewModel.getData().observe(this, Observer {data ->
            val ipk = data.getString("ipk").toFloat()
            root.tv_total_sks.text = "Total SKS : "+data.getString("total_sks")
            root.tv_ipk_transkrip.text = "IPK : "+df.format(ipk).toString()
        })

        root.refresh_transkrip.setOnRefreshListener {
            transkripViewModel.setData(token, nim!!)
        }

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