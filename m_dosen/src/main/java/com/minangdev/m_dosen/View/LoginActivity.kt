package com.minangdev.m_dosen.View

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.iid.FirebaseInstanceId
import com.minangdev.m_dosen.API.ApiBuilder
import com.minangdev.m_dosen.API.ApiInterface
import com.minangdev.m_dosen.Helper.SharePreferenceManager
import com.minangdev.m_dosen.R
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btn_login.setOnClickListener(this)

        if(isLogin()){
            moveActifity()
        }
    }

    fun moveActifity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.btn_login -> {
                submitLogin()
            }
        }
    }

    private fun submitLogin() {
        login_admin.error = ""
        password_admin.error = ""
        val token = FirebaseInstanceId.getInstance().getToken().toString()
        val username = login_admin.editText?.text.toString().trim()
        val password = password_admin.editText?.text.toString().trim()
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val login = apiBuilder.login(username, password, token)
        login.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    val dataJson = JSONObject(response.body()?.string())
                    val token = dataJson.getJSONObject("data").getString("token")
                    val semester = dataJson.getJSONObject("data").getString("semester_aktif")
                    val sharePreferece = SharePreferenceManager(this@LoginActivity)
                    sharePreferece.SaveToken(token, semester)
                    moveActifity()
                } else if (response.code() == 422) {
                        try {
                            val dataError = JSONObject(response.errorBody()?.string())
                            if(dataError.getJSONObject("errors").has("login")){
                                val loginError = dataError.getJSONObject("errors").getJSONArray("login")
                                for (i in 0 until loginError.length()) {
                                    login_admin.error = loginError.getString(i).toString()
                                }
                            }
                            if(dataError.getJSONObject("errors").has("password")){
                                val passwordError = dataError.getJSONObject("errors").getJSONArray("password")
                                for (i in 0 until passwordError.length()) {
                                    password_admin.error = passwordError.getString(i).toString()
                                }
                            }

                        } catch (e: Exception) {
                            Log.e("422", e.toString())
                        }
                } else {
                    Log.e("submitLogin", "Error Code : "+response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("debug", "onFailure: ERROR > " + t.toString())
            }

        })
    }

    fun isLogin():Boolean{
        val sharePreference = SharePreferenceManager(this)
        val token = sharePreference.getToken()
        if(token.equals("")){
            return false
        }
        return true
    }

}