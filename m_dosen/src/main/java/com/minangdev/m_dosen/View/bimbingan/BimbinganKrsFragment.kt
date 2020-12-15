package com.minangdev.m_dosen.View.bimbingan

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.minangdev.m_dosen.API.ApiBuilder
import com.minangdev.m_dosen.API.ApiInterface
import com.minangdev.m_dosen.Adapter.KrsAdapter
import com.minangdev.m_dosen.Helper.LoadingDialog
import com.minangdev.m_dosen.Helper.SharePreferenceManager
import com.minangdev.m_dosen.R
import com.minangdev.m_dosen.ViewModel.KelasViewModel
import com.minangdev.m_dosen.ViewModel.KrsViewModel
import com.minangdev.m_dosen.ViewModel.MahasiswaBimbinganViewModel
import com.minangdev.m_dosen.ViewModel.SemesterViewModel
import kotlinx.android.synthetic.main.dialog_detail_kelas_krs.view.*
import kotlinx.android.synthetic.main.fragment_bimbingan_krs.view.*
import kotlinx.android.synthetic.main.row_krs.view.*
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BimbinganKrsFragment : Fragment() {
    private lateinit var root : View
    private var nim: String? = null
    private lateinit var token : String
    private lateinit var semester: String
    var isCanAcc = false
    var dataSpinner = ArrayList<String>()
    var idSpinner = ArrayList<String>()
    var idOnSelected = ArrayList<String>()

    private lateinit var sharePreference : SharePreferenceManager
    private lateinit var krsAdapter: KrsAdapter
    private lateinit var krsViewModel: KrsViewModel
    private lateinit var kelasViewModel: KelasViewModel
    private lateinit var semesterViewModel : SemesterViewModel

    lateinit var loadingDialog: LoadingDialog

    private lateinit var mDetailDialogView : View
    private lateinit var mDetailAlertDialog : AlertDialog

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
        semester = sharePreference.getSemesterActive().get(sharePreference.IDSEMESTER).toString()

        krsViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(KrsViewModel::class.java)
        kelasViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(KelasViewModel::class.java)
        semesterViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(SemesterViewModel::class.java)

        loadingDialog = LoadingDialog(activity!!)
        mDetailDialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_detail_kelas_krs, null)
        mDetailAlertDialog = AlertDialog.Builder(activity).setView(mDetailDialogView).create()

        krsViewModel.isCanChange(token)
        krsViewModel.canChange().observe(this, Observer {
            if(it){
                isCanAcc = true
            }else{
                isCanAcc = false
            }
        })

        loadSemester()
        initSpinner()

        krsAdapter = KrsAdapter {
            val krsdtId = it.getString("idKrs")
            val klsId = it.getJSONObject("kelas").getString("kelas_id")
            val mSemester = it.getString("semesterId")
            showDetailDialog(klsId, krsdtId, mSemester)
        }
        krsAdapter.setOnLongClick{ jsonObject, view ->
            val mSemester = jsonObject.getString("semesterId")
            if(isCanAcc && mSemester.equals(semester)) {
                val krsdtId = jsonObject.getString("idKrs")
                if(view.row_krs_layout.background.constantState == resources.getDrawable(R.drawable.bg_row_blue).constantState || view.row_krs_layout.background.constantState == resources.getDrawable(R.drawable.bg_row_red).constantState){
                    view.row_krs_layout.setBackgroundResource(R.drawable.bg_row_yellow)
                    idOnSelected.add(krsdtId)
                }else if(view.row_krs_layout.background.constantState == resources.getDrawable(R.drawable.bg_row_yellow).constantState){
                    if(jsonObject.getString("status")=="0"){
                        view.row_krs_layout.setBackgroundResource(R.drawable.bg_row_red)
                    }else{
                        view.row_krs_layout.setBackgroundResource(R.drawable.bg_row_blue)
                    }
                    idOnSelected.remove(krsdtId)
                }
                root.fab_change_status_krs.isVisible = idOnSelected.size>0
            }
            else{
                Toast.makeText(activity, "This KRS Can't Be Change", Toast.LENGTH_SHORT).show()
            }
        }
        root.rv_bimbingan_krs.adapter = krsAdapter
        val layoutManager = LinearLayoutManager(activity)
        root.rv_bimbingan_krs.layoutManager = layoutManager

        loadingDialog.showLoading()
        krsViewModel.setDataSemester(token, nim!!, semester)
        krsViewModel.getDataSemester().observe(this, Observer { data ->
            idOnSelected.clear()
            root.fab_change_status_krs.isVisible = idOnSelected.size>0
            krsAdapter.setData(data.getJSONArray("krs"))
            loadingDialog.hideLoading()
        })

        root.fab_change_status_krs.setOnClickListener{
            var mMenu : Array<String> = arrayOf("Setujui", "Tolak")
            AlertDialog.Builder(root.context)
                    .setTitle("Select Option")
                    .setItems(mMenu, DialogInterface.OnClickListener { dialog, which ->
                        if(idOnSelected.size>0){
                            when(which){
                                0 -> {
                                    //setujui
                                    setujuiKrs()
                                }
                                1 -> {
                                    //tolak
                                    tolakKrs()

                                }
                                else -> false
                            }
                        }
                    })
                    .show()
        }

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

    private fun showDetailDialog(klsId: String, krsdtid: String, mSemester : String) {
        mDetailAlertDialog.hide()
        loadingDialog.showLoading()
        mDetailDialogView.btn_tolak_krs.setOnClickListener{
            mDetailAlertDialog.hide()
            tolakOneKrs(krsdtid)
        }
        mDetailDialogView.btn_setujui_krs.setOnClickListener{
            mDetailAlertDialog.hide()
            setujuiOneKrs(krsdtid)
        }
        if(isCanAcc && mSemester.equals(semester)){
            mDetailDialogView.btn_setujui_krs.isVisible = true
            mDetailDialogView.btn_tolak_krs.isVisible = true
        }else{
            mDetailDialogView.btn_setujui_krs.isVisible = false
            mDetailDialogView.btn_tolak_krs.isVisible = false
        }

        kelasViewModel.setData(token, nim!!, klsId)
        kelasViewModel.getData().observe(this, Observer {data->
            mDetailDialogView.tv_kode_kelas.text = ": "+data.getJSONObject("kelas").getString("nama")
            mDetailDialogView.tv_sks_kelas.text = ": "+data.getJSONObject("matkul").getString("sks")
            mDetailDialogView.tv_matkul_kelas.text = ": "+data.getJSONObject("matkul").getString("nama")
            mDetailDialogView.tv_nilai_kelas.text = ": "+ if(data.getString("nilai").equals("null")){
                "-"
            }else{
                data.getString("nilai")
            }
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
            loadingDialog.hideLoading()
        })
    }

    private fun tolakOneKrs(krsdtId: String){
        idOnSelected.clear()
        idOnSelected.add(krsdtId)
        tolakKrs()
    }

    private fun tolakKrs(){
        loadingDialog.showLoading()
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val profile = apiBuilder.mahasiswaKrsTolak(token, nim!!, idOnSelected)
        profile.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    Toast.makeText(activity, "Berhasil Menolak KRS", Toast.LENGTH_SHORT).show()
                    krsViewModel.setDataSemester(token, nim!!, semester)
                    idOnSelected.clear()
                } else {
                    Toast.makeText(activity, "Gagal Menolak KRS", Toast.LENGTH_SHORT).show()
                    Log.e("Res_KRSTolak", "Ada Error di server Code : " + response.code().toString())
                }
                loadingDialog.hideLoading()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_KRSTolak", "onFailure: ERROR > " + t.toString());
            }

        });
    }

    private fun setujuiOneKrs(krsdtId: String){
        idOnSelected.clear()
        idOnSelected.add(krsdtId)
        setujuiKrs()
    }

    private fun setujuiKrs(){
        loadingDialog.showLoading()
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val profile = apiBuilder.mahasiswaKrsSetujui(token, nim!!, idOnSelected)
        profile.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    Toast.makeText(activity, "Berhasil Menyetujui KRS", Toast.LENGTH_SHORT).show()
                    krsViewModel.setDataSemester(token, nim!!, semester)
                    idOnSelected.clear()
                } else {
                    Toast.makeText(activity, "Gagal Menyetujui KRS", Toast.LENGTH_SHORT).show()
                    Log.e("Res_KRSSetuju", "Ada Error di server Code : " + response.code().toString())
                }
                loadingDialog.hideLoading()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_KRSSetuju", "onFailure: ERROR > " + t.toString());
            }

        });
    }

    private fun initSpinner() {
        val spinnerAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, dataSpinner)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        root.sp_semester_krs_tv_bimmbingan.setAdapter(spinnerAdapter)
        root.sp_semester_krs_tv_bimmbingan.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val index = dataSpinner.indexOf(p0.toString())
                if(index>=0){
                    val idSemester = idSpinner.get(index)
                    krsViewModel.setDataSemester(token, nim!!, idSemester)
                }
            }

        })
    }

    private fun loadSemester() {
        semesterViewModel.setData(token, nim!!)
        semesterViewModel.getData().observe(this, Observer {datas ->
            dataSpinner.clear()
            idSpinner.clear()
            for (i in 0 until datas.length()) {
                dataSpinner.add(datas.getJSONObject(i).getString("periode")+" "+ datas.getJSONObject(i).getString("tahun"))
                idSpinner.add(datas.getJSONObject(i).getString("id").toString())
            }
        })
    }

}