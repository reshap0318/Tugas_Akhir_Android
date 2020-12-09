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

    private lateinit var announcementViewModel: AnnouncementViewHolder
    private lateinit var action : String
    private lateinit var id : String
    private lateinit var token : String

    companion object {
        const val EXTRA_ACTION = "extra_action"
        const val EXTRA_ID = "extra_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_announcement)

        val sharePreference = SharePreferenceManager(this)
        sharePreference.isLogin()

        token = sharePreference.getToken()

        btn_appbar_back.setOnClickListener{
            onBackPressed()
        }

        action = intent.getStringExtra(EXTRA_ACTION)!!
        id = intent.getStringExtra(EXTRA_ID).toString()

        announcementViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(AnnouncementViewHolder::class.java)


        if(action == "detail"){
            getDetail()
        }
        else if(action == "edit"){
            edit()
        }

        btn_simpan_form_announcement.setOnClickListener{
            val title = title_form_announcement.editText?.text.toString()
            val description = description_form_announcement.editText?.text.toString()
            if(action=="edit"){
                update(title, description)
            }else{
                simpan(title, description)
            }
        }
    }

    fun getDetail(){
        announcementViewModel.setData(token, id)
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

    fun simpan(title: String, description: String){
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

    fun edit(){
        announcementViewModel.setEdit(token, id)
        announcementViewModel.getEdit().observe(this, Observer { data ->
            val title = data.getString("title")
            val description = data.getString("description")
            title_form_announcement.editText?.setText(title)
            description_form_announcement.editText?.setText(description)
        })
    }

    fun update(title: String, description: String){
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