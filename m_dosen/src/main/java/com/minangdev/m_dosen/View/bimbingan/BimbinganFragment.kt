package com.minangdev.m_dosen.View.bimbingan

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import kotlinx.android.synthetic.main.fragment_bimbingan.*
import kotlinx.android.synthetic.main.fragment_bimbingan.view.*
import org.json.JSONArray

class BimbinganFragment : Fragment() {

    private lateinit var root : View
    private lateinit var sharePreference : SharePreferenceManager
    private lateinit var mahasiswaBimbinganAdapter: MahasiswaBimbinganAdapter
    private lateinit var mahasiswaBimbinganViewModel: MahasiswaBimbinganViewModel
    lateinit var loadingDialog: LoadingDialog
    var mDataList = JSONArray()

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
            intent.putExtra("mhsid", it.getString("id"))
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
          mDataList = datas
        })

        root.et_search_bimbingan.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val filteredList = JSONArray()
                if(p0.toString() != ""){
                    for (i in 0 until mDataList.length()){
                        if(mDataList.getJSONObject(i).getString("nama").toLowerCase().contains(p0.toString().toLowerCase()) || mDataList.getJSONObject(i).getString("nim").toLowerCase().contains(p0.toString().toLowerCase())){
                            filteredList.put(mDataList.getJSONObject(i))
                        }
                    }
                    mahasiswaBimbinganAdapter.setData(filteredList)
                }else{
                    mahasiswaBimbinganAdapter.setData(mDataList)
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        return root
    }
}