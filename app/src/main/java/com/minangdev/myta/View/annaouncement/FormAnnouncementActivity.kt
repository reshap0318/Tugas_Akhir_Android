package com.minangdev.myta.View.annaouncement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.minangdev.myta.API.ApiBuilder
import com.minangdev.myta.API.ApiInterface
import com.minangdev.myta.Helper.SharePreferenceManager
import com.minangdev.myta.R
import com.minangdev.myta.View.MainActivity
import com.minangdev.myta.ViewModel.AnnouncementViewHolder
import kotlinx.android.synthetic.main.actionbar_onlyback.*
import kotlinx.android.synthetic.main.activity_form_announcement.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FormAnnouncementActivity : AppCompatActivity() {

    var edit : Boolean = false
    private lateinit var announcementViewModel: AnnouncementViewHolder

    companion object {
        const val EXTRA_EDIT = "extra_edit"
        const val EXTRA_DETAIL = "extra_detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_announcement)

        val sharePreference = SharePreferenceManager(this)
        sharePreference.isLogin()

        val token = sharePreference.getToken()

        btn_appbar_back.setOnClickListener{
            onBackPressed()
        }

        val edit_id = intent.getStringExtra(EXTRA_EDIT)
        val detail_id = intent.getStringExtra(EXTRA_DETAIL)
        announcementViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(AnnouncementViewHolder::class.java)

        if(detail_id != null){
            announcementViewModel.setData(token, detail_id)
            announcementViewModel.getData().observe(this, Observer { data ->
                val title = data.getString("title")
                val description = data.getString("description")
                title_form_announcement.editText?.setText(title)
                description_form_announcement.editText?.setText(description)
                title_form_announcement.isEnabled = false
                description_form_announcement.isEnabled = false
                btn_simpan_form_announcement.isVisible = false
            })
        }

        if(edit_id != null){
            edit = true
            announcementViewModel.setEdit(token, edit_id)
            announcementViewModel.getEdit().observe(this, Observer { data ->
                val title = data.getString("title")
                val description = data.getString("description")
                title_form_announcement.editText?.setText(title)
                description_form_announcement.editText?.setText(description)
            })
        }

        btn_simpan_form_announcement.setOnClickListener{
            val title = title_form_announcement.editText?.text.toString()
            val description = description_form_announcement.editText?.text.toString()
            if(edit){
                if (edit_id != null) {
                    update(token, edit_id, title, description)
                }
            }else{
                simpan(token, title, description)
            }
        }
    }

    fun simpan(token: String, title: String, description: String){
        title_form_announcement.error = ""
        description_form_announcement.error = ""
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val announcement = apiBuilder.announcementSave(token, title, description)
        announcement.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    try {
                        Toast.makeText(this@FormAnnouncementActivity, "Berhasil Menambahkan Data", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@FormAnnouncementActivity, MainActivity::class.java)
                        intent.putExtra(MainActivity.EXTRA_FRAGMENT, 2)
                        startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (response.code() == 422) {
                    val dataError = JSONObject(response.errorBody()?.string())
                    validationForm(dataError)
                } else {
                    Log.e("Response_AnnouncementE", "Ada Error di server Code : " + response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Failure_AnnouncementE", "onFailure: ERROR > " + t.toString());
            }

        })
    }

    fun update(token: String, id: String, title: String, description: String){
        title_form_announcement.error = ""
        description_form_announcement.error = ""
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val announcement = apiBuilder.announcementUpdate(token, id, title, description)
        announcement.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    try {
                        Toast.makeText(this@FormAnnouncementActivity, "Berhasil Merubah Data", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@FormAnnouncementActivity, MainActivity::class.java)
                        intent.putExtra(MainActivity.EXTRA_FRAGMENT, 2)
                        startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (response.code() == 422) {
                    val dataError = JSONObject(response.errorBody()?.string())
                    validationForm(dataError)
                } else {
                    Log.e("Response_AnnouncementE", "Ada Error di server Code : " + response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Failure_AnnouncementE", "onFailure: ERROR > " + t.toString());
            }

        })
    }

    private fun validationForm(dataError: JSONObject) {
        if(dataError.getJSONObject("errors").has("title")){
            val titleError = dataError.getJSONObject("errors").getJSONArray("title")
            for (i in 0 until titleError.length()) {
                title_form_announcement.error = titleError.getString(i).toString()
            }
        }

        if(dataError.getJSONObject("errors").has("description")){
            val descriptionError = dataError.getJSONObject("errors").getJSONArray("description")
            for (i in 0 until descriptionError.length()) {
                description_form_announcement.error = descriptionError.getString(i).toString()
            }
        }
    }
}