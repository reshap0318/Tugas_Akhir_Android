package com.minangdev.m_mahasiswa.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minangdev.m_mahasiswa.R
import kotlinx.android.synthetic.main.row_another_notification.view.*
import org.json.JSONArray
import org.json.JSONObject

class AnotherNotificationAdapter: RecyclerView.Adapter<AnotherNotificationAdapter.viewHolder>() {

    var datas = JSONArray()

    fun setData(datas: JSONArray) {
        this.datas = datas
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): viewHolder {
        val mView = LayoutInflater.from(viewGroup.context).inflate(R.layout.row_another_notification, viewGroup, false)
        return viewHolder(mView)
    }

    override fun onBindViewHolder(viewHolder: viewHolder, position: Int) {
        viewHolder.onBind(datas.getJSONObject(position))
    }

    override fun getItemCount(): Int {
        return datas.length()
    }

    inner class viewHolder(private val view : View) : RecyclerView.ViewHolder(view){
        fun onBind (jsonObject : JSONObject) = view.apply{
            tv_label_another_notification.text = jsonObject.getString("title")
            tv_time_another_notification.text = jsonObject.getString("waktu")
            tv_date_another_notification.text = jsonObject.getString("tanggal")
            tv_body_another_notification.text = jsonObject.getString("body")
        }
    }

}