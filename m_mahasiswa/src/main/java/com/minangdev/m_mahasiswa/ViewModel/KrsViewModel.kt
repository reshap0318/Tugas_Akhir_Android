package com.minangdev.m_mahasiswa.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minangdev.m_mahasiswa.API.ApiBuilder
import com.minangdev.m_mahasiswa.API.ApiInterface
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class KrsViewModel: ViewModel()  {
    private val listData = MutableLiveData<JSONArray>()
    private val listDataSemester = MutableLiveData<JSONObject>()
    private val isCanEntry = MutableLiveData<Boolean>()

    fun setData(token: String){
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val profile = apiBuilder.krs(token)
        profile.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.code()==200){
                    try {
                        val result = response.body()?.string()
                        val responseObject = JSONObject(result)
                        val items = responseObject.getJSONArray("data")
                        listData.postValue(items)
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }else{
                    Log.e("Res_KRS", "Ada Error di server Code : "+response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_KRS", "onFailure: ERROR > " + t.toString());
            }

        });
    }

    fun getData(): LiveData<JSONArray> = listData

    fun setDataSemester(token: String, semester: String){
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val profile = apiBuilder.krsSemester(token, semester)
        profile.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.code()==200){
                    try {
                        val result = response.body()?.string()
                        val responseObject = JSONObject(result)
                        val items = responseObject.getJSONObject("data")
                        listDataSemester.postValue(items)
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }else{
                    Log.e("Res_KRSD", "Ada Error di server Code : "+response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_KRSD", "onFailure: ERROR > " + t.toString());
            }

        });
    }

    fun getDataSemester(): LiveData<JSONObject> = listDataSemester

    fun isCanEntry(token:String){
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val profile = apiBuilder.isCanEntry(token)
        profile.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.code()==200){
                    try {
                        isCanEntry.postValue(true)
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }else{
                    Log.e("Res_KRSisEntry", "Ada Error di server Code : "+response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_KRSisEntry", "onFailure: ERROR > " + t.toString());
            }

        });
    }

    fun canEntry(): LiveData<Boolean> = isCanEntry


}