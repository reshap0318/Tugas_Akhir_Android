package com.minangdev.myta.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import org.json.JSONArray
import org.json.JSONObject

class FirebaseViewModel: ViewModel(){

    lateinit var ref: DatabaseReference
    private val listData = MutableLiveData<JSONArray>()

    fun setDataNotification(api_token: String){
        val mData = JSONArray()
        ref = FirebaseDatabase.getInstance().getReference("Users").child(api_token).child("Notification")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot!!.exists()){
                    for (i in snapshot.children){
                        val data: HashMap<String, String> = i.value as HashMap<String, String>
                        val ndata = JSONObject(data as Map<*, *>)
                        mData.put(ndata)
                    }
                    listData.postValue(mData)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun getDataNotification(): LiveData<JSONArray> = listData
}