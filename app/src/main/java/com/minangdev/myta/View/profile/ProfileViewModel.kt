package com.minangdev.myta.View.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minangdev.myta.API.ApiBuilder
import com.minangdev.myta.API.ApiInterface
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class ProfileViewModel: ViewModel() {

    private val listData = MutableLiveData<JSONObject>()

    fun setData(token: String){
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val profile = apiBuilder.profile(token)
        profile.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.code()==200){
                    try {
                        val result = response.body()?.string()
                        val responseObject = JSONObject(result)
                        val item = responseObject.getJSONObject("data")
                        listData.postValue(item)
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }else{
                    Log.e("Response_Home", "Ada Error di server Code : "+response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Failure_Home", "onFailure: ERROR > " + t.toString());
            }

        });
    }

    fun getData(): LiveData<JSONObject>{
        return listData
    }
}