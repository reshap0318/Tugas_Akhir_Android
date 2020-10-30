package com.minangdev.myta.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minangdev.myta.R
import kotlinx.android.synthetic.main.row_notification.view.*
import org.json.JSONArray
import org.json.JSONObject

class NotificationAdapter(private val onItemClickListener : onItemClick) : RecyclerView.Adapter<NotificationAdapter.viewHolder>() {

    var datas = JSONArray()

    fun setData(datas: JSONArray) {
        this.datas = datas
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): NotificationAdapter.viewHolder {
        val mView = LayoutInflater.from(viewGroup.context).inflate(R.layout.row_notification, viewGroup, false)
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
            tv_label_notification.text = jsonObject.getString("title")
            tv_date_notification.text = jsonObject.getString("tanggal")
            tv_time_notification.text = jsonObject.getString("waktu")
            setOnClickListener{
                onItemClickListener(jsonObject)
            }
        }
    }
}

typealias onItemClick = (JSONObject) -> Unit