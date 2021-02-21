package com.minangdev.m_dosen.View.bimbingan

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
import com.minangdev.m_dosen.API.ApiBuilder
import com.minangdev.m_dosen.API.ApiInterface
import com.minangdev.m_dosen.Adapter.BimbinganAdapter
import com.minangdev.m_dosen.Helper.LoadingDialog
import com.minangdev.m_dosen.Helper.SharePreferenceManager
import com.minangdev.m_dosen.R
import com.minangdev.m_dosen.ViewModel.BimbinganViewModel
import com.minangdev.m_dosen.ViewModel.TopicViewModel
import kotlinx.android.synthetic.main.activity_bimbingan_detail_chat.*
import kotlinx.android.synthetic.main.fragment_bimbingan_detail.view.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class BimbinganDetailFragment : Fragment() {
    private lateinit var root : View
    private var nim: String? = null
    private var mhsId: String? = null
    private lateinit var token : String
    private lateinit var userId: String

    var nameTopicActiveList = ArrayList<String>()
    var idTopicActiveList = ArrayList<String>()

    var namePeriodList = ArrayList<String>()
    var idPeriodList = ArrayList<String>()

    private lateinit var sharePreference : SharePreferenceManager
    private lateinit var bimbinganAdapter: BimbinganAdapter
    private lateinit var bimbinganViewModel: BimbinganViewModel
    private lateinit var topicViewModel: TopicViewModel
    lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            nim = it.getString("nim")
            mhsId = it.getString("mhsid")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_bimbingan_detail, container, false)
        sharePreference = SharePreferenceManager(root.context)
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
        if(mhsId != ""){
            bimbinganViewModel.loadListData(token, mhsId!!)
            bimbinganViewModel.getListData().observe(this, Observer {
                bimbinganAdapter.setData(it)
                loadingDialog.hideLoading()
                root.refresh_bimbingan_detail.isRefreshing = false
            })
        }else{
            loadingDialog.hideLoading()
        }

        loadDataTopic()
        root.fab_add_chat_bimbingan.setOnClickListener{
            if(mhsId != ""){
                val mMenuDialog = AlertDialog.Builder(activity).setItems(nameTopicActiveList.toTypedArray(),
                    DialogInterface.OnClickListener{ dialog, which ->
                        createBimbingan(idTopicActiveList.get(which))
                    }
                )
                mMenuDialog.show()
            }else{
                Toast.makeText(activity, "This User Not Found In Database Pengembangan", Toast.LENGTH_SHORT).show()
            }
        }

        loadDataPeriod()
        root.fab_print_chat_bimbingan.setOnClickListener{
            if(mhsId != ""){
                val mMenuDialog = AlertDialog.Builder(activity).setItems(namePeriodList.toTypedArray(),
                        DialogInterface.OnClickListener{ dialog, which ->
                            printPeriod(idPeriodList.get(which))
                        }
                )
                mMenuDialog.show()
            }else{
                Toast.makeText(activity, "This User Not Found In Database Pengembangan", Toast.LENGTH_SHORT).show()
            }
        }

        root.refresh_bimbingan_detail.setOnRefreshListener {
            if(mhsId != ""){
                bimbinganViewModel.loadListData(token, mhsId!!)
            }else{
                Toast.makeText(activity, "This User Not Found In Database Pengembangan", Toast.LENGTH_SHORT).show()
                root.refresh_bimbingan_detail.isRefreshing = false
            }
        }

        return root
    }

    private fun moveTodetailActivity(it: JSONObject) {
        val intent = Intent(activity, BimbinganDetailChatActivity::class.java)
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

    fun loadDataPeriod(){
        topicViewModel.setDataPeriod(token)
        topicViewModel.getDataPeriod().observe(this, Observer {
            namePeriodList = ArrayList<String>()
            Log.e("aaaaa", it.toString())
            idPeriodList = ArrayList<String>()
            for (i in 0 until it.length()) {
                val mData = it.getJSONObject(i)
                namePeriodList.add(mData.getString("name"))
                idPeriodList.add(mData.getString("id"))
            }
        })
    }

    fun createBimbingan(topicPeriodId : String){
        loadingDialog.showLoading()
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val responseBody = apiBuilder.bimbinganCreate(token, mhsId!!, topicPeriodId)
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

    fun printPeriod(periodId: String){
        loadingDialog.showLoading()
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val responseBody = apiBuilder.bimbinganCetakPeriod(token, mhsId!!, periodId)
        responseBody.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    Toast.makeText(activity, "File Sent to Your Email", Toast.LENGTH_SHORT).show()
                } else if (response.code() == 422) {
                    Toast.makeText(activity, "Set Email Before Use This Fiture", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("R_donwloadPeriod", "Error Code : " + response.code().toString())
                }
                loadingDialog.hideLoading()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_donwloadPeriod", "onFailure: ERROR > " + t.toString());
            }

        });
    }

    companion object {
        @JvmStatic
        fun newInstance(nim: String, mhsId: String) =
            BimbinganDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("nim", nim)
                    putString("mhsid", mhsId)
                }
            }
    }
}