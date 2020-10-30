package com.minangdev.myta.View.annaouncement

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
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.minangdev.myta.API.ApiBuilder
import com.minangdev.myta.API.ApiInterface
import com.minangdev.myta.Adapter.NotificationAdapter
import com.minangdev.myta.Helper.SharePreferenceManager
import com.minangdev.myta.R
import kotlinx.android.synthetic.main.fragment_announcement.view.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class AnnouncementFragment : Fragment() {

    private lateinit var announcementAdapter: NotificationAdapter
    private lateinit var announcementViewModel: AnnouncementViewHolder

    val mMenu : Array<String> = arrayOf("Edit", "Detail", "Delete")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_announcement, container, false)
        val sharePreference = activity?.let { SharePreferenceManager(it) }
        sharePreference?.isLogin()
        val token = sharePreference?.getToken()

        root.fab_add_pengumuman.setOnClickListener{
            val intent = Intent(activity, FormAnnouncementActivity::class.java)
            startActivity(intent)
        }

        val layoutManager = LinearLayoutManager(activity)
        announcementAdapter= NotificationAdapter {jsonObject ->
            activity?.let {mActivity ->
                val menuBuilder = AlertDialog.Builder(mActivity)
                menuBuilder.setTitle("Select Option").setItems(mMenu,
                        DialogInterface.OnClickListener { dialog, which ->
                            when(which){
                                0 -> {
                                    val intent = Intent(mActivity, FormAnnouncementActivity::class.java)
                                    intent.putExtra(FormAnnouncementActivity.EXTRA_ID, jsonObject.getString("id"))
                                    startActivity(intent)
                                }
                                2 -> {
                                    if (token != null) {
                                        delete(token, jsonObject.getString("id"))
                                    }
                                }
                                else -> false
                            }
                        })
                val mMenuBuilder = menuBuilder.create()
                mMenuBuilder.show()
            }
        }

        announcementAdapter.notifyDataSetChanged()
        root.rv_pengumuman.adapter = announcementAdapter
        root.rv_pengumuman.layoutManager = layoutManager
        announcementViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(AnnouncementViewHolder::class.java)

        if (token != null) {
            loadData(token)
        }

        return root
    }

    fun loadData(token:String){
        announcementViewModel.setListData(token)
        announcementViewModel.getListData().observe(this, Observer {datas ->
            announcementAdapter.setData(datas)
        })
    }

    fun delete(token: String, id: String){
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val announcement = apiBuilder.announcementDelete(token, id)
        announcement.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    try {
                        Toast.makeText(activity, "Berhasil Menghapus Data Data", Toast.LENGTH_SHORT).show()
                        loadData(token)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Log.e("Response_AnnouncementE", "Ada Error di server Code : " + response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Failure_AnnouncementE", "onFailure: ERROR > " + t.toString());
            }

        })
    }
}