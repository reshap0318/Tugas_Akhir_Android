package com.minangdev.myta.ViewModel

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

class TopicViewModel : ViewModel() {

    private val listData = MutableLiveData<JSONArray>()
    private val listDataActive = MutableLiveData<JSONArray>()
    private val listDataDeactive = MutableLiveData<JSONArray>()

    fun setListData(token : String){
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val topic = apiBuilder.topics(token)
        topic.enqueue(object: Callback<ResponseBody> {
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
                    Log.e("Res_TopicL", "Ada Error di server Code : "+response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_TopicL", "onFailure: ERROR > " + t.toString());
            }

        })
    }

    fun setListDataActive(token : String){
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val topic = apiBuilder.topicActiv(token)
        topic.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.code()==200){
                    try {
                        val result = response.body()?.string()
                        val responseObject = JSONObject(result)
                        val items = responseObject.getJSONArray("data")
                        listDataActive.postValue(items)
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }else{
                    Log.e("Res_TopicL", "Ada Error di server Code : "+response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_TopicL", "onFailure: ERROR > " + t.toString());
            }

        })
    }

    fun setListDataDeactive(token : String){
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val topic = apiBuilder.topicDeactiv(token)
        topic.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.code()==200){
                    try {
                        val result = response.body()?.string()
                        val responseObject = JSONObject(result)
                        val items = responseObject.getJSONArray("data")
                        listDataDeactive.postValue(items)
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }else{
                    Log.e("Res_TopicL", "Ada Error di server Code : "+response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_TopicL", "onFailure: ERROR > " + t.toString());
            }

        })
    }

    fun getListData() : LiveData<JSONArray> = listData

    fun getListDataActive() : LiveData<JSONArray> = listDataActive

    fun getListDataDeactive() : LiveData<JSONArray> = listDataDeactive
}