package com.minangdev.m_dosen.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.minangdev.m_dosen.R
import kotlinx.android.synthetic.main.row_bimbingan_home.view.*
import org.json.JSONArray
import org.json.JSONObject

class BimbinganHomeAdapter(private val onItemClickListener : onItemClick) : RecyclerView.Adapter<BimbinganHomeAdapter.viewHolder>() {

    var datas = JSONArray()

    fun setData(datas: JSONArray) {
        this.datas = datas
        notifyDataSetChanged()
    }

    inner class viewHolder(private val view : View) : RecyclerView.ViewHolder(view){
        fun onBind (jsonObject : JSONObject) = view.apply{
            tv_nama_mahasiswa_bimbingan_home.text = jsonObject.getString("namaUser")
            tv_topik_mahasiswa_bimbingan_home.text = jsonObject.getString("topic") + " " + jsonObject.getString("period")
            val img = jsonObject.getString("avataUser")
            Glide.with(context)
                    .load(img)
                    .fitCenter()
                    .centerCrop()
                    .into(img_avatar_mahasiswa_bimbingan_home)
            setOnClickListener{
                onItemClickListener(jsonObject)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.row_bimbingan_home, parent, false)
        return viewHolder(mView)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.onBind(datas.getJSONObject(position))
    }

    override fun getItemCount(): Int {
        return datas.length()
    }
}