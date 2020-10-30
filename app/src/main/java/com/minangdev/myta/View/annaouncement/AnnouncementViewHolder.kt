package com.minangdev.myta.View.annaouncement

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minangdev.myta.API.ApiBuilder
import com.minangdev.myta.API.ApiInterface
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class AnnouncementViewHolder: ViewModel() {

    private val listData = MutableLiveData<JSONArray>()
    private val data = MutableLiveData<JSONObject>()
    private val edit = MutableLiveData<JSONObject>()

    fun setListData(token : String){
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val announcement = apiBuilder.announcements(token)
        announcement.enqueue(object: Callback<ResponseBody> {
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
                    Log.e("Response_AnnouncementL", "Ada Error di server Code : "+response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Failure_AnnouncementL", "onFailure: ERROR > " + t.toString());
            }

        })
    }

    fun setData(token: String, id: String){
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val announcement = apiBuilder.announcement(token, id)
        announcement.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.code()==200){
                    try {
                        val result = response.body()?.string()
                        val responseObject = JSONObject(result)
                        val item = responseObject.getJSONObject("data")
                        data.postValue(item)
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }else{
                    Log.e("Response_Announcement", "Ada Error di server Code : "+response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Failure_Announcement", "onFailure: ERROR > " + t.toString());
            }

        })
    }

    fun setedit(token: String, id: String){
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val announcement = apiBuilder.announcementEdit(token, id)
        announcement.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.code()==200){
                    try {
                        val result = response.body()?.string()
                        val responseObject = JSONObject(result)
                        val item = responseObject.getJSONObject("data")
                        data.postValue(item)
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }else{
                    Log.e("Response_AnnouncementE", "Ada Error di server Code : "+response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Failure_AnnouncementE", "onFailure: ERROR > " + t.toString());
            }

        })
    }

    fun getListData() : LiveData<JSONArray> = listData

    fun getData() : LiveData<JSONObject> = data

    fun getEdit() : LiveData<JSONObject> = edit

}