package com.minangdev.m_dosen.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.minangdev.m_dosen.API.ApiBuilder
import com.minangdev.m_dosen.API.ApiInterface
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class BimbinganViewModel: ViewModel() {

    lateinit var ref: DatabaseReference
    private val listData = MutableLiveData<JSONArray>()
    private val listDetailChat = MutableLiveData<JSONArray>()
    private val lisAlltDetailChat = MutableLiveData<JSONArray>()
    private val groupData = MutableLiveData<JSONObject>()
    private val listDetailChatGroup = MutableLiveData<JSONArray>()


    fun loadListData(token: String, mhsid: String){
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val announcement = apiBuilder.mahasiswaListBimbingan(token, mhsid)
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
                    Log.e("Res_BimbinganL", "Ada Error di server Code : "+response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_BimbinganL", "onFailure: ERROR > " + t.toString());
            }

        })
    }

    fun getListData(): LiveData<JSONArray> = listData

    fun loadDetailChat(receiverId: String, senderId: String, topicPeriodId: String){
        ref = FirebaseDatabase.getInstance().getReference("Chat")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mData = JSONArray()
                if (snapshot!!.exists()){
                    for (i in snapshot.children){
                        val data: HashMap<String, String> = i.value as HashMap<String, String>
                        if(
                                ( (data.get("receiver").equals(receiverId) && data.get("sender").equals(senderId)) ||
                                  (data.get("receiver").equals(senderId) && data.get("sender").equals(receiverId)) ) &&
                                data.get("topic_period").equals(topicPeriodId)
                        ){
                            val ndata = JSONObject(data as Map<*, *>)
                            mData.put(ndata)
                        }
                    }
                    listDetailChat.postValue(mData)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun detailChat(): LiveData<JSONArray> = listDetailChat

    fun loadLastSeenData(token: String){
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val announcement = apiBuilder.bimbinganLastSeen(token)
        announcement.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.code()==200){
                    try {
                        val result = response.body()?.string()
                        val responseObject = JSONObject(result)
                        val items = responseObject.getJSONArray("data")
                        lisAlltDetailChat.postValue(items)
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }else{
                    Log.e("Res_Bimbingan", "Ada Error di server Code : "+response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_BimbinganAD", "onFailure: ERROR > " + t.toString());
            }

        })
    }

    fun getLastSeenData(): LiveData<JSONArray> = lisAlltDetailChat

    fun setGroupData(token: String){
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val profile = apiBuilder.bimbinganListGroup(token)
        profile.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.code()==200){
                    try {
                        val result = response.body()?.string()
                        val responseObject = JSONObject(result)
                        val item = responseObject.getJSONObject("data")
                        groupData.postValue(item)
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }else{
                    Log.e("Res_groupData", "Ada Error di server Code : "+response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_groupData", "onFailure: ERROR > " + t.toString());
            }

        });
    }

    fun getGroupData(): LiveData<JSONObject> = groupData

    fun loadDetailChatGroup(groupChanel: String){
        ref = FirebaseDatabase.getInstance().getReference("Group-Chat").child(groupChanel)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mData = JSONArray()
                if (snapshot!!.exists()){
                    for (i in snapshot.children){
                        val data: HashMap<String, String> = i.value as HashMap<String, String>
                        val ndata = JSONObject(data as Map<*, *>)
                        mData.put(ndata)
                    }
                    listDetailChatGroup.postValue(mData)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun detailChatGroup(): LiveData<JSONArray> = listDetailChatGroup

}