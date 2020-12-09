package com.minangdev.myta.View.annaouncement

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.minangdev.myta.API.ApiBuilder
import com.minangdev.myta.API.ApiInterface
import com.minangdev.myta.Adapter.NotificationAdapter
import com.minangdev.myta.Helper.LoadingDialog
import com.minangdev.myta.Helper.SharePreferenceManager
import com.minangdev.myta.R
import com.minangdev.myta.ViewModel.AnnouncementViewHolder
import kotlinx.android.synthetic.main.dialog_form_announcement.view.*
import kotlinx.android.synthetic.main.fragment_announcement.view.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class AnnouncementFragment : Fragment() {

    private lateinit var announcementAdapter: NotificationAdapter
    private lateinit var announcementViewModel: AnnouncementViewHolder
    private lateinit var sharePreference : SharePreferenceManager
    private lateinit var root : View

    private lateinit var mCreateDialogView : View
    private lateinit var mCreate : AlertDialog
    private lateinit var loadingDialog: LoadingDialog

    var announcement_id : String? = null
    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_announcement, container, false)
        sharePreference = SharePreferenceManager(root.context)
        sharePreference.isLogin()
        token = sharePreference.getToken()

        initDialogCreate()

        root.fab_add_pengumuman.setOnClickListener{
            announcement_id = null
            mCreateDialogView.btn_simpan_form_announcement.isVisible = true
            mCreateDialogView.tv_date_news.isVisible = false
            mCreateDialogView.tv_sending_news.isVisible = false
            mCreateDialogView.title_form_announcement.error = ""
            mCreateDialogView.description_form_announcement.error = ""
            mCreateDialogView.title_form_announcement.editText?.setText("")
            mCreateDialogView.description_form_announcement.editText?.setText("")
            mCreate.show()
        }

        //adapter
        val layoutManager = LinearLayoutManager(activity)
        announcementAdapter= NotificationAdapter {jsonObject ->
            initRowMenu(jsonObject)
        }
        announcementAdapter.notifyDataSetChanged()
        root.rv_pengumuman.adapter = announcementAdapter
        root.rv_pengumuman.layoutManager = layoutManager

        //load data
        announcementViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(AnnouncementViewHolder::class.java)
        loadingDialog = LoadingDialog(activity!!)
        loadData()
        return root
    }

    private fun initRowMenu(jsonObject: JSONObject) {
        var mMenu : Array<String> = arrayOf("Detail", "Edit", "Delete")
        //kondisi
        val unitId = sharePreference.getUnitId()
        if(!jsonObject.getString("unit_id").equals(unitId)){
            mMenu = arrayOf("Detail")
        }
        val sender = "By Admin "+jsonObject.getString("unit")
        AlertDialog.Builder(root.context)
        .setTitle("Select Option")
        .setItems(mMenu, DialogInterface.OnClickListener { dialog, which ->
            when(which){
                0 -> {
                    mCreateDialogView.title_form_announcement.error = ""
                    mCreateDialogView.description_form_announcement.error = ""
                    mCreateDialogView.title_form_announcement.editText?.setText(jsonObject.getString("title"))
                    mCreateDialogView.description_form_announcement.editText?.setText(jsonObject.getString("description"))
                    mCreateDialogView.tv_date_news.text = jsonObject.getString("tanggal")
                    mCreateDialogView.tv_sending_news.text = sender
                    mCreateDialogView.btn_simpan_form_announcement.isVisible = false
                    mCreateDialogView.tv_date_news.isVisible = true
                    mCreateDialogView.tv_sending_news.isVisible = true
                    mCreateDialogView.et_description_form_announcement.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    mCreate.show()
                }
                1 -> {
                    mCreateDialogView.btn_simpan_form_announcement.isVisible = true
                    mCreateDialogView.tv_date_news.isVisible = false
                    mCreateDialogView.tv_sending_news.isVisible = false
                    announcement_id = jsonObject.getString("id")
                    mCreateDialogView.title_form_announcement.error = ""
                    mCreateDialogView.description_form_announcement.error = ""
                    mCreateDialogView.title_form_announcement.editText?.setText(jsonObject.getString("title"))
                    mCreateDialogView.description_form_announcement.editText?.setText(jsonObject.getString("description"))
                    mCreate.show()
                }
                2 -> {
                    delete(jsonObject.getString("id"))
                }
                else -> false
            }
        })
        .show()
    }

    private fun initDialogCreate() {
        mCreateDialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_form_announcement, null)
        val mCreateDialog = AlertDialog.Builder(activity)
                .setView(mCreateDialogView)
        mCreate = mCreateDialog.create()
        mCreateDialogView.btn_cancel_form_announcement.setOnClickListener{
            mCreate.dismiss()
        }
        mCreateDialogView.btn_simpan_form_announcement.setOnClickListener{
            val title = mCreateDialogView.title_form_announcement.editText?.text.toString()
            val description = mCreateDialogView.description_form_announcement.editText?.text.toString()
            if(announcement_id==null){
                simpan(title, description)
            }else{
                update(announcement_id.toString(), title, description)
            }
            announcement_id = null

        }
    }

    fun update(id : String, title : String, description : String){
        loadingDialog.showLoading()
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val announcement = apiBuilder.announcementUpdate(token, id, title, description)
        announcement.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    try {
                        Toast.makeText(activity, "Berhasil Merubah Data", Toast.LENGTH_SHORT).show()
                        loadData()
                        mCreate.dismiss()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (response.code() == 422) {
                    val dataError = JSONObject(response.errorBody()?.string())
                    validationForm(dataError)
                } else {
                    Log.e("Response_AnnouncementE", "Ada Error di server Code : " + response.code().toString())
                }
                loadingDialog.hideLoading()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Failure_AnnouncementE", "onFailure: ERROR > " + t.toString());
            }

        })
    }

    fun simpan(title : String, description : String){
        loadingDialog.showLoading()
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val announcement = apiBuilder.announcementSave(token, title, description)
        announcement.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    try {
                        Toast.makeText(activity, "Berhasil Menambahkan Data", Toast.LENGTH_SHORT).show()
                        loadData()
                        mCreate.dismiss()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (response.code() == 422) {
                    val dataError = JSONObject(response.errorBody()?.string())
                    validationForm(dataError)
                } else {
                    Log.e("Response_AnnouncementS", "Ada Error di server Code : " + response.code().toString())
                }
                loadingDialog.hideLoading()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Failure_AnnouncementS", "onFailure: ERROR > " + t.toString());
            }

        })
    }

    fun loadData(){
        loadingDialog.showLoading()
        announcementViewModel.setListData(token)
        announcementViewModel.getListData().observe(this, Observer {datas ->
            announcementAdapter.setData(datas)
            loadingDialog.hideLoading()
        })
    }

    fun delete(id: String){
        loadingDialog.showLoading()
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val announcement = apiBuilder.announcementDelete(token, id)
        announcement.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    try {
                        Toast.makeText(activity, "Berhasil Menghapus Data Data", Toast.LENGTH_SHORT).show()
                        loadData()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Log.e("Res_AnnouncementD", "Ada Error di server Code : " + response.code().toString())
                }
                loadingDialog.hideLoading()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_AnnouncementD", "onFailure: ERROR > " + t.toString());
            }

        })
    }

    private fun validationForm(dataError: JSONObject) {
        if(dataError.getJSONObject("errors").has("title")){
            val titleError = dataError.getJSONObject("errors").getJSONArray("title")
            for (i in 0 until titleError.length()) {
                mCreateDialogView.title_form_announcement.error = titleError.getString(i).toString()
            }
        }

        if(dataError.getJSONObject("errors").has("description")){
            val descriptionError = dataError.getJSONObject("errors").getJSONArray("description")
            for (i in 0 until descriptionError.length()) {
                mCreateDialogView.description_form_announcement.error = descriptionError.getString(i).toString()
            }
        }
    }
}