package com.minangdev.m_dosen.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minangdev.m_dosen.API.ApiBuilder
import com.minangdev.m_dosen.API.ApiInterface
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class KelasViewModel : ViewModel() {


    private val mData = MutableLiveData<JSONObject>()

    fun setData(token: String, nim: String, klsId: String){
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val profile = apiBuilder.mahasiswaKelasDetail(token, nim,klsId)
        profile.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.code()==200){
                    try {
                        val result = response.body()?.string()
                        val responseObject = JSONObject(result)
                        val item = responseObject.getJSONObject("data")
                        mData.postValue(item)
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }else{
                    Log.e("Res_transkrip", "Ada Error di server Code : "+response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_transkrip", "onFailure: ERROR > " + t.toString());
            }

        });
    }

    fun getData(): LiveData<JSONObject> = mData

}