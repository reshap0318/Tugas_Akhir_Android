package com.minangdev.m_dosen.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import org.json.JSONArray

class FirebaseViewModel: ViewModel(){

    lateinit var ref: DatabaseReference
    private val listData = MutableLiveData<ArrayList<HashMap<String, Any>>>()

    fun setData(api_token: String){
        val mData = ArrayList<HashMap<String, Any>>()
        ref = FirebaseDatabase.getInstance().getReference("User-Notification").child(api_token).child("Notification")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot!!.exists()){
                    for (i in snapshot.children){
                        val data: HashMap<String, Any> = i.value as HashMap<String, Any>
                        mData.add(data)
                    }
                    listData.postValue(mData)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun getData(): LiveData<ArrayList<HashMap<String, Any>>> = listData
}