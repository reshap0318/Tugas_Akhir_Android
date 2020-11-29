package com.minangdev.m_mahasiswa.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.minangdev.m_mahasiswa.R
import kotlinx.android.synthetic.main.row_transkrip.view.*
import org.json.JSONArray
import org.json.JSONObject

class TranskripMainAdapter(private val context: Context) : RecyclerView.Adapter<TranskripMainAdapter.MainViewHolder>() {

    var datas = JSONArray()

    fun setData(datas: JSONArray) {
        this.datas = datas
        notifyDataSetChanged()
    }

    class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun onBind (jsonObject : JSONObject) = itemView.apply{
            val tahun = jsonObject.getJSONObject("semester").getString("tahun")
            val nama = jsonObject.getJSONObject("semester").getString("periode")
            tv_semester_transkrip.text = "${nama} ${tahun}"

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_transkrip, parent, false)
        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.onBind(datas.getJSONObject(position))
        setSubTranskrip(holder.itemView.rv_transkrip_sub, datas.getJSONObject(position).getJSONArray("matkul"))
    }

    override fun getItemCount() = datas.length()

    private fun setSubTranskrip(recyclerView: RecyclerView, mDatas: JSONArray){
        val item = TranskripSubAdapter(context, mDatas)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = item
    }
}