package com.minangdev.myta.View.topic

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.minangdev.myta.API.ApiBuilder
import com.minangdev.myta.API.ApiInterface
import com.minangdev.myta.Adapter.OneAdapter
import com.minangdev.myta.Helper.SharePreferenceManager
import com.minangdev.myta.R
import com.minangdev.myta.ViewModel.TopicViewModel
import kotlinx.android.synthetic.main.dialog_form_topic.view.*
import kotlinx.android.synthetic.main.fragment_topic_active.view.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class TopicActiveFragment : Fragment() {

    private lateinit var topicAdapter: OneAdapter
    private lateinit var topicViewModel: TopicViewModel

    private lateinit var root : View
    private lateinit var sharePreference : SharePreferenceManager
    private lateinit var token: String

    private var initialCheckedItems =  mutableListOf<Boolean>()
    private var dataDeactive = ArrayList<String>()
    private var idDeactive = ArrayList<String>()
    private var mCheckData = ArrayList<String>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        root = inflater.inflate(R.layout.fragment_topic_active, container, false)
        sharePreference = SharePreferenceManager(root.context)
        sharePreference.isLogin()
        token = sharePreference.getToken()
        topicViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(TopicViewModel::class.java)

        initDataDeactive()

        root.fab_add_topic_active.setOnClickListener{
            if(dataDeactive.size > 0){
                val checkedItems = initialCheckedItems.toBooleanArray()
                MaterialAlertDialogBuilder(context!!)
                .setMultiChoiceItems(dataDeactive.toTypedArray(), checkedItems) { dialog, which, isChecked ->
                    if(isChecked){
                        if(!mCheckData.contains(idDeactive.get(which))){
                            mCheckData.add(idDeactive.get(which))
                        }else{
                            mCheckData.remove(idDeactive.get(which))
                        }
                    }else{
                        mCheckData.remove(idDeactive.get(which))
                    }
                }
                .setPositiveButton(resources.getString(R.string.save)) { dialog, which ->
                    simpanData()
                }
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                    // Respond to neutral button press
                }
                .show()
            }else{
                Toast.makeText(activity, "All Topic In Period", Toast.LENGTH_SHORT).show()
            }
        }

        topicAdapter = OneAdapter {
            val periodId = it.getString("period_id")
            val topicId = it.getString("topic_id")
            MaterialAlertDialogBuilder(context!!)
            .setTitle("Delete This?")
            .setNegativeButton("No") { dialog, which ->

            }
            .setPositiveButton("Yes") { dialog, which ->
                deleteData(periodId, topicId)
            }
            .show()
        }
        topicAdapter.notifyDataSetChanged()
        val layoutManager = LinearLayoutManager(activity)
        root.rv_topic_active.adapter = topicAdapter
        root.rv_topic_active.layoutManager = layoutManager
        loadData()
        return root
    }

    private fun initDataDeactive() {
        topicViewModel.setListDataDeactive(token)
        topicViewModel.getListDataDeactive().observe(this, Observer { datas ->
            dataDeactive = arrayListOf()
            idDeactive = arrayListOf()
            initialCheckedItems = arrayListOf()

            for (i in 0 until datas.length()) {
                dataDeactive.add(datas.getJSONObject(i).getString("name").toString())
                idDeactive.add(datas.getJSONObject(i).getString("id").toString())
                initialCheckedItems.add(false)
            }
        })
    }

    private fun simpanData(){
        if(mCheckData.size>0){
            val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
            val semester = sharePreference.getSemesterActive().get(sharePreference.IDSEMESTER).toString()
            val mBuilder = apiBuilder.periodAddTopic(token, semester, mCheckData)
            mBuilder.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.code() == 200) {
                        try {
                            Toast.makeText(activity, "Berhasil Menambahkan Data", Toast.LENGTH_SHORT).show()
                            loadData()
                            initDataDeactive()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (response.code() == 422) {
                        val mDataError = JSONObject(response.errorBody()?.string())
                        Toast.makeText(activity, mDataError.toString(), Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("Resp_TopicS", "Ada Error di server Code : " + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("Fail_TopicS", "onFailure: ERROR > " + t.toString());
                }

            })
        }else{
            Toast.makeText(activity, "No Data Added", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteData(periodId: String, topicId: String) {
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val announcement = apiBuilder.periodDeleteTopic(token, periodId, topicId)
        announcement.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    try {
                        Toast.makeText(activity, "Berhasil Menghapus Data", Toast.LENGTH_SHORT).show()
                        loadData()
                        initDataDeactive()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Log.e("Res_TopicD", "Ada Error di server Code : " + response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_TopicD", "onFailure: ERROR > " + t.toString());
            }

        })
    }

    private fun loadData() {
        topicViewModel.setListDataActive(token)
        topicViewModel.getListDataActive().observe(this, Observer { datas ->
            topicAdapter.setData(datas)
        })
    }


}