package com.minangdev.m_dosen.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minangdev.m_dosen.R
import kotlinx.android.synthetic.main.row_notification.view.*
import org.json.JSONObject

class NotificationAdapter() : RecyclerView.Adapter<NotificationAdapter.viewHolder>() {

    var datas = ArrayList<HashMap<String, Any>>()

    fun setData(datas: ArrayList<HashMap<String, Any>>) {
        this.datas = datas
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): NotificationAdapter.viewHolder {
        val mView = LayoutInflater.from(viewGroup.context).inflate(R.layout.row_notification, viewGroup, false)
        return viewHolder(mView)
    }

    override fun onBindViewHolder(viewHolder: viewHolder, position: Int) {
        viewHolder.onBind(datas.get(position))
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    inner class viewHolder(private val view : View) : RecyclerView.ViewHolder(view){
        fun onBind (data: HashMap<String, Any>) = view.apply{
            tv_label_notification.text = data.get("title").toString()
            var date = data.get("tanggal").toString()
            var time = data.get("waktu").toString()
            if(date=="null"){
                date = ""
            }
            if(time=="null"){
                time = ""
            }
            tv_date_notification.text = date
            tv_time_notification.text = time
        }
    }
}