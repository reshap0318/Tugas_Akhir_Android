package com.minangdev.m_mahasiswa.View.krs

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.minangdev.m_mahasiswa.Adapter.KrsAdapter
import com.minangdev.m_mahasiswa.Helper.SharePreferenceManager
import com.minangdev.m_mahasiswa.R
import com.minangdev.m_mahasiswa.ViewModel.KrsViewModel
import com.minangdev.m_mahasiswa.ViewModel.SemesterViewModel
import kotlinx.android.synthetic.main.fragment_krs.*
import kotlinx.android.synthetic.main.fragment_krs.view.*
import kotlin.collections.ArrayList

class KrsFragment : Fragment() {

    private lateinit var root : View
    private lateinit var sharePreference : SharePreferenceManager
    private lateinit var semesterViewModel : SemesterViewModel
    private lateinit var krsAdapter : KrsAdapter
    private lateinit var krsViewModel : KrsViewModel

    private var isLoaded = false
    private var isVisibleToUser = true

    var dataSpinner = ArrayList<String>()
    var idSpinner = ArrayList<String>()
    lateinit var token: String

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        this.isVisibleToUser=isVisibleToUser;
        if(isVisibleToUser && isAdded()){
            loadSemester()
            isLoaded = true
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        root = inflater.inflate(R.layout.fragment_krs, container, false)
        sharePreference = SharePreferenceManager(root.context)
        sharePreference.isLogin()
        token = sharePreference.getToken()
        val semesterActive = sharePreference.getSemesterActive().get(sharePreference.IDSEMESTER)
        semesterViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(SemesterViewModel::class.java)


        if(isVisibleToUser && (!isLoaded)){
            loadSemester();
            isLoaded=true;
        }
        initSpinner()

        krsAdapter = KrsAdapter {

        }
        krsAdapter.notifyDataSetChanged()
        val layoutManager = LinearLayoutManager(activity)
        root.rv_krs.adapter = krsAdapter
        root.rv_krs.layoutManager = layoutManager

        krsViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(KrsViewModel::class.java)

        krsViewModel.setDataSemester(token, semesterActive!!)
        krsViewModel.getDataSemester().observe(this, Observer { datas ->
            root.tv_sks_krs.text = datas.getJSONObject("sks").getString("sks_diambil")+"/"+datas.getJSONObject("sks").getString("jatah_sks")
            krsAdapter.setData(datas.getJSONArray("krs"))
        })
        return root
    }

    private fun initSpinner() {
        val spinnerAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, dataSpinner)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        root.sp_semester_krs_tv.setAdapter(spinnerAdapter)
        root.sp_semester_krs_tv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val index = dataSpinner.indexOf(p0.toString())
                if(index>=0){
                    val idSemester = idSpinner.get(index)
                    krsViewModel.setDataSemester(token, idSemester)
                }
            }

        })
    }

    private fun loadSemester() {
        semesterViewModel.setData(token)
        semesterViewModel.getData().observe(this, Observer {datas ->
            for (i in 0 until datas.length()) {
                dataSpinner.add(datas.getJSONObject(i).getString("periode")+" "+ datas.getJSONObject(i).getString("tahun"))
                idSpinner.add(datas.getJSONObject(i).getString("id").toString())
            }
        })
    }
}