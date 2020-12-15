package com.minangdev.m_dosen.View

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.minangdev.m_dosen.API.ApiBuilder
import com.minangdev.m_dosen.API.ApiInterface
import com.minangdev.m_dosen.Helper.LoadingDialog
import com.minangdev.m_dosen.Helper.SharePreferenceManager
import com.minangdev.m_dosen.R
import kotlinx.android.synthetic.main.actionbar_onlyback.*
import kotlinx.android.synthetic.main.activity_change_password.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {

    lateinit var sharePreference : SharePreferenceManager
    lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        sharePreference = SharePreferenceManager(this)
        sharePreference.isLogin()

        val token = sharePreference.getToken()

        btn_change_password.setOnClickListener{
            changePassword(token)
        }

        btn_appbar_back.setOnClickListener{
            onBackPressed()
        }
        loadingDialog = LoadingDialog(this)
    }

    fun changePassword(token : String){
        loadingDialog.showLoading()
        val oldPassword = old_password_change.editText?.text.toString().trim()
        val newPassword = new_password_change.editText?.text.toString().trim()
        val confirmPassword = confirm_password_change.editText?.text.toString().trim()

        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val profile = apiBuilder.changePassword(token, oldPassword, newPassword, confirmPassword)
        profile.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    Toast.makeText(this@ChangePasswordActivity, "Berhasil Mengganti Password", Toast.LENGTH_SHORT).show()
                    sharePreference.isLogin()
                }  else if (response.code() == 422) {
                    val dataError = JSONObject(response.errorBody()?.string())
                    validationForm(dataError)
                } else {
                    Log.e("Res_Change_Password", "Ada Error di server Code : " + response.code().toString())
                }
                loadingDialog.hideLoading()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_Change_Password", "onFailure: ERROR > " + t.toString());
            }
        })
    }

    private fun validationForm(dataError: JSONObject) {
        if(dataError.getJSONObject("errors").has("old_password")){
            val oldError = dataError.getJSONObject("errors").getJSONArray("old_password")
            for (i in 0 until oldError.length()) {
                old_password_change.error = oldError.getString(i).toString()
            }
        }

        if(dataError.getJSONObject("errors").has("new_password")){
            val newError = dataError.getJSONObject("errors").getJSONArray("new_password")
            for (i in 0 until newError.length()) {
                new_password_change.error = newError.getString(i).toString()
            }
        }

        if(dataError.getJSONObject("errors").has("confirm_password")){
            val confirmError = dataError.getJSONObject("errors").getJSONArray("confirm_password")
            for (i in 0 until confirmError.length()) {
                confirm_password_change.error = confirmError.getString(i).toString()
            }
        }
    }
}