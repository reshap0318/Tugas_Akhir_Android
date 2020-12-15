package com.minangdev.m_dosen.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.minangdev.m_dosen.R
import kotlinx.android.synthetic.main.row_mahasiswa.view.*
import org.json.JSONArray
import org.json.JSONObject

class MahasiswaBimbinganAdapter(private val onItemClickListener : onItemClick) : RecyclerView.Adapter<MahasiswaBimbinganAdapter.viewHolder>() {

    var datas = JSONArray()

    fun setData(datas: JSONArray) {
        this.datas = datas
        notifyDataSetChanged()
    }

    inner class viewHolder(private val view : View) : RecyclerView.ViewHolder(view){
        fun onBind (jsonObject : JSONObject) = view.apply{
            tv_nama_mahasiswa.text = jsonObject.getString("nama")
            tv_nim_mahasiswa.text = jsonObject.getString("nim")
            val img = jsonObject.getString("avatar")
            Glide.with(context)
                    .load(img)
                    .fitCenter()
                    .centerCrop()
                    .into(img_avatar_mahasiswa)
            setOnClickListener{
                onItemClickListener(jsonObject)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.row_mahasiswa, parent, false)
        return viewHolder(mView)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.onBind(datas.getJSONObject(position))
    }

    override fun getItemCount() = datas.length()

}

typealias onItemClick = (JSONObject) -> Unit