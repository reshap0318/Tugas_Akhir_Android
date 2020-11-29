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
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SharePreferenceManager {

    lateinit var sharedPreferences : SharedPreferences
    var context : Context
    val PREFERANCENAME: String = "setting"
    val TOKEN : String = "token"
    val FCMID : String = "fcmToken"
    val UNITID : String = "unitId"
    val SEMESTER : String = "semester"
    val IDSEMESTER : String = "idSemester"
    val TAHUNSEMESTER : String = "tahunSemester"

    constructor(context: Context){
        this.context = context
        this.sharedPreferences = context.getSharedPreferences(this.PREFERANCENAME, Context.MODE_PRIVATE)
    }

    fun SaveToken(token: String, unitId: String, fcmToken: String){
        val editor = sharedPreferences!!.edit()
        editor.putString(this.TOKEN, "Bearer "+token)
        editor.putString(this.UNITID, unitId)
        editor.putString(this.FCMID, fcmToken)
        editor.commit()
    }

    fun getToken(): String {
        return sharedPreferences!!.getString(this.TOKEN, "").toString()
    }

    fun getUnitId(): String {
        return sharedPreferences!!.getString(this.UNITID, "").toString()
    }

    fun getFCMTOKEN(): String{
        return sharedPreferences!!.getString(this.FCMID, "").toString()
    }

    fun setSemesterActive(data: JSONObject){
        val editor = sharedPreferences!!.edit()
        editor.putString(this.SEMESTER, data.getString("periode"))
        editor.putString(this.IDSEMESTER, data.getString("id"))
        editor.putString(this.TAHUNSEMESTER, data.getString("tahun"))
        editor.commit()
    }

    fun getSemesterActive(): HashMap<String, String>{
        val data = HashMap<String, String>()
        data.put(this.SEMESTER, sharedPreferences!!.getString(this.SEMESTER, "").toString())
        data.put(this.IDSEMESTER, sharedPreferences!!.getString(this.IDSEMESTER, "").toString())
        data.put(this.TAHUNSEMESTER, sharedPreferences!!.getString(this.TAHUNSEMESTER, "").toString())
        return data
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