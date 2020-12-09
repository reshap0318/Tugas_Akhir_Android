package com.minangdev.m_mahasiswa.View.krs

import android.app.AlertDialog
import android.content.Intent
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
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.minangdev.m_mahasiswa.API.ApiBuilder
import com.minangdev.m_mahasiswa.API.ApiInterface
import com.minangdev.m_mahasiswa.Adapter.KrsAdapter
import com.minangdev.m_mahasiswa.Helper.SharePreferenceManager
import com.minangdev.m_mahasiswa.R
import com.minangdev.m_mahasiswa.ViewModel.KelasViewModel
import com.minangdev.m_mahasiswa.ViewModel.KrsViewModel
import com.minangdev.m_mahasiswa.ViewModel.SemesterViewModel
import kotlinx.android.synthetic.main.dialog_detail_kelas.view.*
import kotlinx.android.synthetic.main.fragment_krs.*
import kotlinx.android.synthetic.main.fragment_krs.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import kotlin.collections.ArrayList

class KrsFragment : Fragment() {

    private lateinit var root : View
    private lateinit var sharePreference : SharePreferenceManager

    private lateinit var krsAdapter : KrsAdapter

    private lateinit var semesterViewModel : SemesterViewModel
    private lateinit var krsViewModel : KrsViewModel
    private lateinit var kelasViewModel: KelasViewModel

    private lateinit var mDetailDialogView : View
    private lateinit var mDetailAlertDialog : AlertDialog

    private var isLoaded = false
    private var isVisibleToUser = true
    var isCanDelete = false
    var dataSpinner = ArrayList<String>()
    var idSpinner = ArrayList<String>()
    lateinit var token: String
    lateinit var semesterActive: String

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
        semesterActive = sharePreference.getSemesterActive().get(sharePreference.IDSEMESTER).toString()
        semesterViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(SemesterViewModel::class.java)
        kelasViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(KelasViewModel::class.java)


        if(isVisibleToUser && (!isLoaded)){
            loadSemester();
            isLoaded=true;
        }
        initSpinner()

        krsAdapter = KrsAdapter {
            val klsId =  it.getJSONObject("kelas").getString("kelas_id")
            val krsdtid = it.getString("idKrs")
            val semester = it.getString("semesterId")
            showDetailDialog(klsId, krsdtid, semester)
        }
        krsAdapter.notifyDataSetChanged()
        val layoutManager = LinearLayoutManager(activity)
        root.rv_krs.adapter = krsAdapter
        root.rv_krs.layoutManager = layoutManager

        krsViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(KrsViewModel::class.java)

        krsViewModel.setDataSemester(token, semesterActive)
        krsViewModel.getDataSemester().observe(this, Observer { datas ->
            root.tv_sks_krs.text = datas.getJSONObject("sks").getString("sks_diambil")+"/"+datas.getJSONObject("sks").getString("jatah_sks")
            krsAdapter.setData(datas.getJSONArray("krs"))
        })

        krsViewModel.isCanEntry(token)
        krsViewModel.canEntry().observe(this, Observer {
            if(!it){
                isCanDelete = false
                root.fab_add_krs.isVisible = false
            }else{
                isCanDelete = true
                root.fab_add_krs.isVisible = true
            }
        })

        root.fab_add_krs.setOnClickListener{
            val intent = Intent(activity, AddKrsActivity::class.java)
            startActivity(intent)
        }
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

    private fun deleteKrs(krsdtId: String) {
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val announcement = apiBuilder.krsDelete(token, krsdtId)
        announcement.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    try {
                        Toast.makeText(activity, "Berhasil Menghapus Data", Toast.LENGTH_SHORT).show()
                        krsViewModel.setDataSemester(token, semesterActive)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Log.e("Res_TopicD", "Ada Error di server Code : " + response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_TopicD", "onFailure: ERROR > " + t.toString());
            }

        })
    }

    private fun showDetailDialog(klsId: String, krsdtid: String, semester: String) {
        mDetailDialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_detail_kelas, null)
        mDetailAlertDialog = AlertDialog.Builder(activity).setView(mDetailDialogView).create()
        mDetailDialogView.btn_hapus_krs.setOnClickListener{
            deleteKrs(krsdtid)
            mDetailAlertDialog.hide()
        }

        if(isCanDelete && semester.equals(semesterActive)){
            mDetailDialogView.btn_hapus_krs.isVisible = true
        }

        kelasViewModel.setData(token, klsId)
        kelasViewModel.getData().observe(this, Observer {data->
            mDetailDialogView.tv_kode_kelas.text = ": "+data.getJSONObject("kelas").getString("nama")
            mDetailDialogView.tv_sks_kelas.text = ": "+data.getJSONObject("matkul").getString("sks")
            mDetailDialogView.tv_matkul_kelas.text = ": "+data.getJSONObject("matkul").getString("nama")
            val jadwalJson = data.getJSONArray("jadwal")
            var jadwal = ""
            for(i in 0 until jadwalJson.length()){
                val jadwalObject = jadwalJson.getJSONObject(i)
                val hari = jadwalObject.getString("hari")
                val waktu_mulai = jadwalObject.getString("waktu_mulai")
                val waktu_selesai = jadwalObject.getString("waktu_selesai")
                val ruangan = jadwalObject.getString("ruangan")
                val mdata = hari +", "+waktu_mulai+" - "+waktu_selesai+" ("+ruangan+")"
                if(i==0){
                    jadwal += mdata
                }else{
                    jadwal += " & "+mdata
                }
            }
            val dosenJson = data.getJSONArray("dosen")
            var dosen = ""
            for (i in 0 until dosenJson.length()){
                val namaDosen = dosenJson.getJSONObject(i).getString("nama")
                if(i==0){
                    dosen += namaDosen
                }else{
                    dosen += " & "+namaDosen
                }
            }
            mDetailDialogView.tv_dosen_kelas.text = ": "+dosen
            mDetailDialogView.tv_waktu_kelas.text = ": "+jadwal
            mDetailAlertDialog.show()
        })
    }
}