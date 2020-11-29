package com.minangdev.myta.View.topic

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
import com.minangdev.myta.ViewModel.PeriodViewModel
import kotlinx.android.synthetic.main.fragment_period.view.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class PeriodFragment : Fragment() {

    private lateinit var periodAdapter: OneAdapter
    private lateinit var periodViewModel : PeriodViewModel
    private lateinit var sharePreference : SharePreferenceManager

    private lateinit var root : View
    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_period, container, false)
        sharePreference = SharePreferenceManager(root.context)
        sharePreference.isLogin()
        token = sharePreference.getToken()

        root.fab_syc_period.setOnClickListener{
            synData()
        }

        periodAdapter = OneAdapter {  }
        periodAdapter.notifyDataSetChanged()
        val layoutManager = LinearLayoutManager(activity)
        root.rv_period.adapter = periodAdapter
        root.rv_period.layoutManager = layoutManager

        periodViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(PeriodViewModel::class.java)
        loadData()
        return root
    }

    private fun loadData() {
        periodViewModel.setListData(token)
        periodViewModel.getListData().observe(this, Observer { datas ->
            periodAdapter.setData(datas)
        })
    }

    private fun synData() {
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val topic = apiBuilder.periodSyn(token)
        topic.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    try {
                        val result = response.body()?.string()
                        val responseObject = JSONObject(result)
                        val pesan = responseObject.getString("data")
                        Toast.makeText(activity, pesan, Toast.LENGTH_SHORT).show()
                        if (!pesan.equals("No Data Syn")) {
                            loadData()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Log.e("Resp_TopicS", "Ada Error di server Code : " + response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_TopicS", "onFailure: ERROR > " + t.toString());
            }

        })
    }
}