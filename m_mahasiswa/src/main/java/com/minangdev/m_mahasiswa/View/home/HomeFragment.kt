package com.minangdev.m_mahasiswa.View.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.minangdev.m_mahasiswa.Adapter.KrsAdapter
import com.minangdev.m_mahasiswa.Helper.LoadingDialog
import com.minangdev.m_mahasiswa.Helper.SharePreferenceManager
import com.minangdev.m_mahasiswa.R
import com.minangdev.m_mahasiswa.View.NotificationActivity
import com.minangdev.m_mahasiswa.ViewModel.*
import kotlinx.android.synthetic.main.dialog_detail_kelas.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_krs.view.*
import kotlinx.android.synthetic.main.row_notification.view.*

class HomeFragment : Fragment() {

    private lateinit var root : View

    private lateinit var sharePreference : SharePreferenceManager

    private lateinit var krsAdapter : KrsAdapter
    private lateinit var krsViewModel: KrsViewModel
    private lateinit var homeViewModel : ProfileViewModel
    private lateinit var announcementViewHolder: AnnouncementViewHolder
    private lateinit var kelasViewModel: KelasViewModel
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var mDetailDialogView : View
    private lateinit var mDetailAlertDialog : AlertDialog

    lateinit var token: String
    lateinit var semesterActive: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_home, container, false)
        sharePreference = SharePreferenceManager(root.context)
        sharePreference.isLogin()
        token = sharePreference.getToken()
        semesterActive = sharePreference.getSemesterActive().get(sharePreference.IDSEMESTER).toString()

        homeSetData(name="", nip="")

        homeViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(ProfileViewModel::class.java)
        krsViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(KrsViewModel::class.java)
        announcementViewHolder = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(AnnouncementViewHolder::class.java)
        kelasViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(KelasViewModel::class.java)

        loadingDialog = LoadingDialog(activity!!)

        krsAdapter = KrsAdapter{
            val klsId =  it.getJSONObject("kelas").getString("kelas_id")
            showDetailDialog(klsId)
        }
        krsAdapter.notifyDataSetChanged()
        val layoutManager = LinearLayoutManager(activity)
        root.rv_krs_home.adapter = krsAdapter
        root.rv_krs_home.layoutManager = layoutManager

        loadingDialog.showLoading()
        homeViewModel.setData(token)
        homeViewModel.getData().observe(this, Observer {data ->
            val name = data.getString("name")
            val nip = data.getString("username")
            val img = data.getString("avatar")
            homeSetData(name=name, nip=nip, img=img)
            loadingDialog.hideLoading()
        })

        loadingDialog.showLoading()
        krsViewModel.setDataSemester(token, semesterActive!!)
        krsViewModel.getDataSemester().observe(this, Observer { datas ->
            krsAdapter.setData(datas.getJSONArray("krs"))
            loadingDialog.hideLoading()
        })

        root.tv_announcement_home.setOnClickListener{
            val intent = Intent(activity, NotificationActivity::class.java)
            startActivity(intent)
        }

        root.tv_krs_home.setOnClickListener{
            Toast.makeText(activity, "Belum Berfungsi", Toast.LENGTH_SHORT).show()
        }

        root.row_notification_home.isVisible = false
        root.label_notification_home.isVisible = false
        loadingDialog.showLoading()
        announcementViewHolder.setListData(token)
        announcementViewHolder.getListData().observe(this, Observer { datas ->
            if(datas.length()>0){
                root.row_notification_home.isVisible = true
                root.label_notification_home.isVisible = true
                root.tv_label_notification.text = datas.getJSONObject(0).getString("title")
                root.tv_date_notification.text = datas.getJSONObject(0).getString("tanggal")
                root.tv_time_notification.text = datas.getJSONObject(0).getString("waktu")
                loadingDialog.hideLoading()
            }
        })

        return root
    }

    private fun homeSetData(name:String, nip:String, img:String? = null) {
        root.tv_name_home.text = name
        root.tv_nip_home.text = nip
        Glide.with(root)
            .load(img)
            .fitCenter()
            .centerCrop()
            .into(root.img_profile_home)
    }

    private fun showDetailDialog(klsId: String) {
        loadingDialog.showLoading()
        mDetailDialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_detail_kelas, null)
        mDetailAlertDialog = AlertDialog.Builder(activity).setView(mDetailDialogView).create()

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
            loadingDialog.hideLoading()
            mDetailAlertDialog.show()
        })
    }
}