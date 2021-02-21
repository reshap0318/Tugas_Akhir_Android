package com.minangdev.m_dosen.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minangdev.m_dosen.R
import kotlinx.android.synthetic.main.row_total_nilai.view.*
import org.json.JSONArray
import org.json.JSONObject

class TotalNilaiAdapter(private val onItemClickListener : onItemClick) : RecyclerView.Adapter<TotalNilaiAdapter.viewHolder>() {

    var datas = JSONArray()

    fun setData(datas: JSONArray) {
        this.datas = datas
        notifyDataSetChanged()
    }

    inner class viewHolder(private val view : View) : RecyclerView.ViewHolder(view){
        fun onBind (jsonObject : JSONObject) = view.apply{
            tv_total_nilai.text = jsonObject.getString("krsdtKodeNilai") + " : " + jsonObject.getString("total")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.row_total_nilai, parent, false)
        return viewHolder(mView)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.onBind(datas.getJSONObject(position))
    }

    override fun getItemCount(): Int {
        return datas.length()
    }
}