package com.minangdev.myta.Helper

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.minangdev.myta.API.ApiBuilder
import com.minangdev.myta.API.ApiInterface
import com.minangdev.myta.View.LoginActivity
import com.minangdev.myta.View.MainActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SharePreferenceManager {

    lateinit var sharedPreferences : SharedPreferences
    var context : Context
    val PREFERANCENAME: String = "setting"
    val TOKEN : String = "token"
    var ISLOGIN : Boolean = false

    constructor(context: Context){
        this.context = context
        this.sharedPreferences = context.getSharedPreferences(this.PREFERANCENAME, Context.MODE_PRIVATE)
    }

    fun SaveToken(token: String){
        val editor = sharedPreferences!!.edit()
        editor.putString(this.TOKEN, "Bearer "+token)
        editor.commit()
    }

    fun getToken(): String {
        return sharedPreferences!!.getString(this.TOKEN, "").toString()
    }

    fun isLogin(){
        val token = sharedPreferences!!.getString(this.TOKEN, "").toString()
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val isLogin = apiBuilder.isLogin(token)
        isLogin.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.code()!=200){
                    val intent = Intent( context, LoginActivity::class.java)
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