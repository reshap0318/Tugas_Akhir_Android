package com.minangdev.myta.View.home

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
import com.minangdev.myta.API.ApiBuilder
import com.minangdev.myta.API.ApiInterface
import com.minangdev.myta.Adapter.NotificationAdapter
import com.minangdev.myta.Helper.LoadingDialog
import com.minangdev.myta.Helper.SharePreferenceManager
import com.minangdev.myta.R
import com.minangdev.myta.View.annaouncement.FormAnnouncementActivity
import com.minangdev.myta.ViewModel.AnnouncementViewHolder
import com.minangdev.myta.ViewModel.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_home.view.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class HomeFragment : Fragment() {

    private lateinit var announcementAdapter: NotificationAdapter

    private lateinit var homeViewModel : ProfileViewModel
    private lateinit var announcementViewModel: AnnouncementViewHolder
    private lateinit var sharePreference : SharePreferenceManager

    private lateinit var root : View

    private lateinit var loadingDialog: LoadingDialog

    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_home, container, false)
        sharePreference = SharePreferenceManager(root.context)
        sharePreference.isLogin()
        token = sharePreference.getToken()

        homeSetData(name="", nip="")

        root.fab_add_pengumuman.setOnClickListener{
            val intent = Intent(activity, FormAnnouncementActivity::class.java)
            intent.putExtra(FormAnnouncementActivity.EXTRA_ID,"0")
            intent.putExtra(FormAnnouncementActivity.EXTRA_ACTION, "create")
            startActivity(intent)
        }

        //adapter
        val layoutManager = LinearLayoutManager(activity)
        announcementAdapter= NotificationAdapter {jsonObject ->
            initRowMenu(jsonObject)
        }
        announcementAdapter.notifyDataSetChanged()
        root.rv_pengumuman.adapter = announcementAdapter
        root.rv_pengumuman.layoutManager = layoutManager

        //load data
        announcementViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(AnnouncementViewHolder::class.java)
        loadingDialog = LoadingDialog(activity!!)
        loadData()

        homeViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(ProfileViewModel::class.java)
        homeViewModel.setData(token)
        homeViewModel.getData().observe(this, Observer {data ->
            val name = data.getString("name")
            val nip = data.getString("username")
            val img = data.getString("avatar")
            homeSetData(name=name, nip=nip, img=img)
        })

        root.refresh_announcement_home.setOnRefreshListener {
            announcementViewModel.setListData(token)
        }
        return root
    }

    private fun homeSetData(name:String, nip:String, img:String? = null) {
        root.tv_name_home.text = name
        root.tv_nip_home.text = nip
        Glide.with(root)
            .load(img)
            .fitCenter()
            .centerCrop()
            .into(root.img_profile_home)
    }

    private fun initRowMenu(jsonObject: JSONObject) {
        var mMenu : Array<String> = arrayOf("Detail")

        //kondisi
        val unitId = sharePreference.getUnitId()

        if(jsonObject.getString("unit_id").equals(unitId)){
            mMenu = arrayOf("Detail", "Edit", "Delete")
        }
        AlertDialog.Builder(root.context)
                .setTitle("Select Option")
                .setItems(mMenu, DialogInterface.OnClickListener { dialog, which ->
                    when(which){
                        0 -> {
                            //detail
                            val intent = Intent(activity, FormAnnouncementActivity::class.java)
                            intent.putExtra(FormAnnouncementActivity.EXTRA_ID,jsonObject.getString("id"))
                            intent.putExtra(FormAnnouncementActivity.EXTRA_ACTION, "detail")
                            startActivity(intent)
                        }
                        1 -> {
                            //edit
                            val intent = Intent(activity, FormAnnouncementActivity::class.java)
                            intent.putExtra(FormAnnouncementActivity.EXTRA_ID,jsonObject.getString("id"))
                            intent.putExtra(FormAnnouncementActivity.EXTRA_ACTION, "edit")
                            startActivity(intent)
                        }
                        2 -> {
                            delete(jsonObject.getString("id"))
                        }
                        else -> false
                    }
                })
                .show()
    }

    fun loadData(){
        loadingDialog.showLoading()
        announcementViewModel.setListData(token)
        announcementViewModel.getListData().observe(this, Observer {datas ->
            announcementAdapter.setData(datas)
            loadingDialog.hideLoading()
            root.refresh_announcement_home.isRefreshing = false
        })
    }

    fun delete(id: String){
        loadingDialog.showLoading()
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val announcement = apiBuilder.announcementDelete(token, id)
        announcement.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    try {
                        Toast.makeText(activity, "Berhasil Menghapus Data Data", Toast.LENGTH_SHORT).show()
                        loadData()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Log.e("Res_AnnouncementD", "Ada Error di server Code : " + response.code().toString())
                }
                loadingDialog.hideLoading()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_AnnouncementD", "onFailure: ERROR > " + t.toString());
            }

        })
    }
}