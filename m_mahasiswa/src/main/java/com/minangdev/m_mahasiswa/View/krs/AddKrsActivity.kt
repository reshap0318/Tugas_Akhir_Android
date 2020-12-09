package com.minangdev.m_mahasiswa.View.krs

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.minangdev.m_mahasiswa.Adapter.AddKrsAdapter
import com.minangdev.m_mahasiswa.Helper.SharePreferenceManager
import com.minangdev.m_mahasiswa.R
import com.minangdev.m_mahasiswa.View.MainActivity
import com.minangdev.m_mahasiswa.ViewModel.KelasViewModel
import com.minangdev.m_mahasiswa.ViewModel.KrsViewModel
import com.minangdev.m_mahasiswa.ViewModel.SKSViewModel
import kotlinx.android.synthetic.main.actionbar_onlyback.*
import kotlinx.android.synthetic.main.activity_add_krs.*
import kotlinx.android.synthetic.main.dialog_detail_kelas_add_krs.view.*
import com.minangdev.m_mahasiswa.API.ApiBuilder
import com.minangdev.m_mahasiswa.API.ApiInterface
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddKrsActivity : AppCompatActivity() {

    private lateinit var kelasViewModel: KelasViewModel
    private lateinit var sksViewModel : SKSViewModel
    private lateinit var krsViewModel : KrsViewModel
    private lateinit var sharePreference : SharePreferenceManager
    private lateinit var addKrsAdapter: AddKrsAdapter
    lateinit var token: String

    private lateinit var mDetailDialogView : View
    private lateinit var mDetailAlertDialog : AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_krs)

        sharePreference = SharePreferenceManager(this)
        sharePreference.isLogin()
        token = sharePreference.getToken()

        kelasViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(KelasViewModel::class.java)
        sksViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(SKSViewModel::class.java)
        krsViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(KrsViewModel::class.java)
        krsViewModel.isCanEntry(token)
        krsViewModel.canEntry().observe(this, Observer {
            if(!it){
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(MainActivity.EXTRA_FRAGMENT, 3)
                startActivity(intent)
            }
        })

        addKrsAdapter = AddKrsAdapter {
            showDetailDialog(it.getString("kelas_id"))
        }
        addKrsAdapter.notifyDataSetChanged()
        val layoutManager = LinearLayoutManager(this)
        rv_add_krs.adapter = addKrsAdapter
        rv_add_krs.layoutManager = layoutManager

        kelasViewModel.setDatas(token)
        kelasViewModel.getDatas().observe(this, Observer { datas ->
            addKrsAdapter.setData(datas)
        })

        sksViewModel.setData(token)
        sksViewModel.getData().observe(this, Observer { data ->
            tv_max_sks_add_krs.text = data.getString("jatah_sks")
            tv_sks_active_add_krs.text = data.getString("sks_diambil")
            tv_total_sks_add_krs.text = data.getString("total_sks")
        })

        btn_appbar_back.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(MainActivity.EXTRA_FRAGMENT, 3)
            startActivity(intent)
        }

    }

    private fun showDetailDialog(id: String) {
        mDetailDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_detail_kelas_add_krs, null)
        mDetailAlertDialog = AlertDialog.Builder(this).setView(mDetailDialogView).create()
        mDetailDialogView.btn_cancel_add_krs.setOnClickListener{
            mDetailAlertDialog.hide()
        }

        kelasViewModel.setData(token, id)
        kelasViewModel.getData().observe(this, Observer {data->
            mDetailDialogView.tv_kode_kelas_add_krs.text = ": "+data.getJSONObject("kelas").getString("nama")
            mDetailDialogView.tv_sks_kelas_add_krs.text = ": "+data.getJSONObject("matkul").getString("sks")
            mDetailDialogView.tv_matkul_kelas_add_krs.text = ": "+data.getJSONObject("matkul").getString("nama")
            mDetailDialogView.tv_nilai_kelas_add_krs.text = ": "+data.getString("nilai")
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
            mDetailDialogView.tv_dosen_kelas_add_krs.text = ": "+dosen
            mDetailDialogView.tv_waktu_kelas_add_krs.text = ": "+jadwal
            mDetailDialogView.btn_confirm_add_krs.setOnClickListener{
                submitKrsData(data.getJSONObject("kelas").getString("id"))
                mDetailAlertDialog.hide()
            }
            mDetailAlertDialog.show()
        })
    }

    private fun submitKrsData(id: String) {
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val profile = apiBuilder.entry(token, id)
        profile.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    Toast.makeText(this@AddKrsActivity, "Berhasil Menambahkan Data", Toast.LENGTH_SHORT).show()
                    kelasViewModel.setDatas(token)
                    krsViewModel.isCanEntry(token)
                    sksViewModel.setData(token)
                } else {
                    Toast.makeText(this@AddKrsActivity, "Gagal Menambahkan Data", Toast.LENGTH_SHORT).show()
                    Log.e("Res_KRSEntry", "Ada Error di server Code : " + response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_KRSEntry", "onFailure: ERROR > " + t.toString());
            }

        });
    }
}