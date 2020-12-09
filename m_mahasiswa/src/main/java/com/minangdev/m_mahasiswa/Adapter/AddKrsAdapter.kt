package com.minangdev.m_mahasiswa.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.minangdev.m_mahasiswa.R
import kotlinx.android.synthetic.main.row_krs.view.*
import org.json.JSONArray
import org.json.JSONObject

class AddKrsAdapter(private val onItemClickListener : onItemClick) : RecyclerView.Adapter<AddKrsAdapter.viewHolder>() {

    var datas = JSONArray()

    fun setData(datas: JSONArray) {
        this.datas = datas
        notifyDataSetChanged()
    }

    inner class viewHolder(private val view : View) : RecyclerView.ViewHolder(view){
        fun onBind (jsonObject : JSONObject) = view.apply{
            tv_kode_matkul_krs.text = jsonObject.getString("kode_matkul")
            tv_nama_matkul_krs.text = jsonObject.getString("nama_matkul")
            tv_sks_matkul_krs.text = jsonObject.getString("sks_matkul") + " SKS"
            tv_status_krs.isVisible = false
            var jadwal = ""
            val jadwalJson = jsonObject.getJSONArray("jadwal")
            for(i in 0 until jadwalJson.length()){
                val jadwalObject = jadwalJson.getJSONObject(i)
                val hari = jadwalObject.getString("hari")
                val waktu_mulai = jadwalObject.getString("waktu_mulai")
                val waktu_selesai = jadwalObject.getString("waktu_selesai")
                val mdata = jadwalObject.getString("hari")+", "+jadwalObject.getString("waktu_mulai")+" - "+jadwalObject.getString("waktu_selesai")
                if(i==0){
                    jadwal += mdata
                }else{
                    jadwal += " & "+mdata
                }
//                jadwal += " #"+jadwalObject.getString("ruangan")
            }
            tv_jadwal_matkul_krs.text = jadwal

            setOnClickListener{
                onItemClickListener(jsonObject)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.row_krs, parent, false)
        return viewHolder(mView)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.onBind(datas.getJSONObject(position))
    }

    override fun getItemCount(): Int {
        return datas.length()
    }
}