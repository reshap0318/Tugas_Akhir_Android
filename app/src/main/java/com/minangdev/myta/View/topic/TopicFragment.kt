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
import com.minangdev.myta.API.ApiBuilder
import com.minangdev.myta.API.ApiInterface
import com.minangdev.myta.Adapter.OneAdapter
import com.minangdev.myta.Helper.SharePreferenceManager
import com.minangdev.myta.R
import com.minangdev.myta.ViewModel.TopicViewModel
import kotlinx.android.synthetic.main.dialog_form_topic.view.*
import kotlinx.android.synthetic.main.fragment_topic.view.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class TopicFragment : Fragment() {

    private lateinit var topicAdapter: OneAdapter
    private lateinit var topicViewModel: TopicViewModel

    private lateinit var root : View
    private lateinit var sharePreference : SharePreferenceManager
    private lateinit var mCreate : AlertDialog

    private lateinit var mCreateDialogView : View

    val mMenuArray : Array<String> = arrayOf("Edit", "Delete")
    var topic_id : String? = null
    private lateinit var token: String

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_topic, container, false)
        sharePreference = SharePreferenceManager(root.context)
        sharePreference.isLogin()
        token = sharePreference.getToken()

        //create with dialog
        //dialog create
        initDialogCreate()

        //set btn to open dialog
        root.fab_add_topic.setOnClickListener{
            mCreateDialogView.name_form_topic.error = ""
            mCreateDialogView.name_form_topic.editText?.setText("")
            mCreate.show()
        }

        //adapter
        topicAdapter = OneAdapter { jsonObject ->
            topic_id = jsonObject.getString("id")
            val mMenuDialog = AlertDialog.Builder(activity).setItems(mMenuArray,
                    DialogInterface.OnClickListener{ dialog, which ->
                        when(which){
                            0 -> {
                                mCreateDialogView.name_form_topic.error = ""
                                mCreateDialogView.name_form_topic.editText?.setText(jsonObject.getString("name"))
                                mCreate.show()
                            }
                            1 -> {
                                delete(topic_id.toString())
                                topic_id = null
                            }
                            else -> false
                        }
                    }
            )
            val mMenu = mMenuDialog.create()
            mMenu.show()
        }
        topicAdapter.notifyDataSetChanged()
        val layoutManager = LinearLayoutManager(activity)
        root.rv_topic.adapter = topicAdapter
        root.rv_topic.layoutManager = layoutManager

        //load data
        topicViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(TopicViewModel::class.java)
        loadData()
        return root
    }

    private fun initDialogCreate() {
        mCreateDialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_form_topic, null)
        val mCreateDialog = AlertDialog.Builder(activity)
                .setView(mCreateDialogView)
        mCreate = mCreateDialog.create()
        mCreateDialogView.btn_cancel_form_topic.setOnClickListener{
            mCreate.dismiss()
        }
        mCreateDialogView.btn_simpan_form_topic.setOnClickListener{
            val name = mCreateDialogView.name_form_topic.editText?.text.toString()
            if(topic_id==null){
                simpanData(name)
            }else{
                updateData(topic_id.toString(), name)
                topic_id = null
            }

        }
    }

    private fun simpanData(name: String) {
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val topic = apiBuilder.topicSave(token, name)
        topic.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    try {
                        Toast.makeText(activity, "Berhasil Menambahkan Data", Toast.LENGTH_SHORT).show()
                        loadData()
                        mCreate.dismiss()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (response.code() == 422) {
                    val mDataError = JSONObject(response.errorBody()?.string())
                    validationForm(mDataError)
                } else {
                    Log.e("Resp_TopicS", "Ada Error di server Code : " + response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_TopicS", "onFailure: ERROR > " + t.toString());
            }

        })
    }

    private fun updateData(topicId: String, name: String) {
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val topic = apiBuilder.topicUpdate(token, topicId, name)
        topic.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    try {
                        Toast.makeText(activity, "Berhasil Merubah Data", Toast.LENGTH_SHORT).show()
                        loadData()
                        mCreate.dismiss()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (response.code() == 422) {
                    val dataError = JSONObject(response.errorBody()?.string())
                    validationForm(dataError)
                } else {
                    Log.e("Res_TopicE", "Ada Error di server Code : " + response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_TopicE", "onFailure: ERROR > " + t.toString());
            }

        })
    }

    private fun validationForm(dataError : JSONObject){
        if(dataError.getJSONObject("errors").has("name")){
            val nameError = dataError.getJSONObject("errors").getJSONArray("name")
            for (i in 0 until nameError.length()) {
                mCreateDialogView.name_form_topic.error = nameError.getString(i).toString()
            }
        }
    }

    private fun loadData() {
        topicViewModel.setListData(token)
        topicViewModel.getListData().observe(this, Observer { datas ->
            topicAdapter.setData(datas)
        })
    }

    private fun delete(id: String) {
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val announcement = apiBuilder.topicDelete(token, id)
        announcement.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    try {
                        Toast.makeText(activity, "Berhasil Menghapus Data", Toast.LENGTH_SHORT).show()
                        loadData()
                        mCreate.dismiss()
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
}