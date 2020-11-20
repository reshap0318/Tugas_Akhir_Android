package com.minangdev.m_dosen.Helper

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.minangdev.m_dosen.API.ApiBuilder
import com.minangdev.m_dosen.API.ApiInterface
import com.minangdev.m_dosen.View.LoginActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SharePreferenceManager {

    lateinit var sharedPreferences : SharedPreferences
    var context : Context
    val PREFERANCENAME: String = "setting"
    val TOKEN : String = "token"
    val SEMESTER : String = "semester"

    constructor(context: Context){
        this.context = context
        this.sharedPreferences = context.getSharedPreferences(this.PREFERANCENAME, Context.MODE_PRIVATE)
    }

    fun SaveToken(token: String, semester : String){
        val editor = sharedPreferences!!.edit()
        editor.putString(this.TOKEN, "Bearer "+token)
        editor.putString(this.SEMESTER, semester)
        editor.commit()
    }

    fun getToken(): String {
        return sharedPreferences!!.getString(this.TOKEN, "").toString()
    }

    fun getSemester(): String {
        return sharedPreferences!!.getString(this.SEMESTER, "").toString()
    }

    fun isLogin(){
        val token = sharedPreferences!!.getString(this.TOKEN, "").toString()
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val isLogin = apiBuilder.isLogin(token)
        isLogin.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.code()!=200){
                    val intent = Intent( context, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    logout()
                    context.startActivity(intent)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("onfailur", t.message.toString())
            }

        })
    }

    fun logout(){
        val editor = sharedPreferences!!.edit()
        editor.clear()
        editor.commit()
    }

}