package com.minangdev.m_mahasiswa.View.bimbingan

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
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
import com.bumptech.glide.Glide
import com.minangdev.m_mahasiswa.API.ApiBuilder
import com.minangdev.m_mahasiswa.API.ApiInterface
import com.minangdev.m_mahasiswa.Adapter.BimbinganAdapter
import com.minangdev.m_mahasiswa.Helper.LoadingDialog
import com.minangdev.m_mahasiswa.Helper.SharePreferenceManager
import com.minangdev.m_mahasiswa.R
import com.minangdev.m_mahasiswa.ViewModel.BimbinganViewModel
import com.minangdev.m_mahasiswa.ViewModel.TopicViewModel
import kotlinx.android.synthetic.main.fragment_bimbingan.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class BimbinganFragment : Fragment() {

    private lateinit var sharePreference : SharePreferenceManager
    private lateinit var bimbinganAdapter: BimbinganAdapter
    private lateinit var bimbinganViewModel: BimbinganViewModel
    private lateinit var topicViewModel: TopicViewModel
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var root : View

    private lateinit var token: String
    private lateinit var userId: String

    var nameTopicActiveList = ArrayList<String>()
    var idTopicActiveList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_bimbingan, container, false)
        sharePreference = SharePreferenceManager(activity!!)
        sharePreference.isLogin()
        token = sharePreference.getToken()
        userId = sharePreference.getUserId()
        loadingDialog = LoadingDialog(activity!!)

        bimbinganViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(BimbinganViewModel::class.java)
        topicViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(TopicViewModel::class.java)

        bimbinganAdapter = BimbinganAdapter {
            moveTodetailActivity(it)
        }
        val layoutManager = LinearLayoutManager(activity)
        root.rv_chat_bimbingan.adapter = bimbinganAdapter
        root.rv_chat_bimbingan.layoutManager = layoutManager

        loadingDialog.showLoading()
        bimbinganViewModel.loadListData(token)
        bimbinganViewModel.getListData().observe(this, Observer {
            bimbinganAdapter.setData(it)
            loadingDialog.hideLoading()
        })

        loadDataTopic()
        root.fab_add_chat_bimbingan.setOnClickListener{
            val mMenuDialog = AlertDialog.Builder(activity).setItems(nameTopicActiveList.toTypedArray(),
                    DialogInterface.OnClickListener{ dialog, which ->
                        createBimbingan(idTopicActiveList.get(which))
                    }
            )
            mMenuDialog.show()
        }


        loadingDialog.showLoading()
        bimbinganViewModel.setGroupData(token)
        bimbinganViewModel.getGroupData().observe(this, Observer { item ->
            root.tv_nama_group_home.text = item.getString("groupName")
            root.tv_topik_group_bimbingan_home.text = "Chanel " + item.getString("groupChanel")
            Glide.with(root)
                    .load(item.getString("groupAvatar"))
                    .fitCenter()
                    .centerCrop()
                    .into(root.img_avatar_group_home)
            root.row_group_chat_home.setOnClickListener {
                val intent = Intent(activity, BimbinganGroupActivity::class.java)
                intent.putExtra("data", item.toString())
                startActivity(intent)
            }
            loadingDialog.hideLoading()
        })

        return root
    }

    private fun moveTodetailActivity(it: JSONObject) {
        val intent = Intent(activity, BimbinganDetailActivity::class.java)
        intent.putExtra("receiverId", it.getString("to"))
        intent.putExtra("topicPeriodId",it.getString("topicPeriodId"))
        intent.putExtra("receiverNama",it.getString("namaUser"))
        intent.putExtra("receiverAvatar",it.getString("avataUser"))
        val topicPeriod = it.getString("topic") + " - " + it.getString("period")
        intent.putExtra("topicPeriod", topicPeriod)
        startActivity(intent)
    }

    fun loadDataTopic(){
        topicViewModel.setData(token)
        topicViewModel.getData().observe(this, Observer {
            nameTopicActiveList = ArrayList<String>()
            idTopicActiveList = ArrayList<String>()

            for (i in 0 until it.length()){
                val mData = it.getJSONObject(i)
                nameTopicActiveList.add(mData.getString("name"))
                idTopicActiveList.add(mData.getString("id"))
            }
        })
    }

    fun createBimbingan(topicPeriodId : String){
        loadingDialog.showLoading()
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val responseBody = apiBuilder.bimbinganCreate(token, topicPeriodId)
        responseBody.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    try {
                        val result = response.body()?.string()
                        val responseObject = JSONObject(result)
                        val item = responseObject.getJSONObject("data")
                        moveTodetailActivity(item)
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(activity, "Gagal Membuat Bimbingan", Toast.LENGTH_SHORT).show()
                    Log.e("Res_BimbinganC", "Ada Error di server Code : " + response.code().toString())
                }
                loadingDialog.hideLoading()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_BimbinganC", "onFailure: ERROR > " + t.toString());
            }

        });
    }


}