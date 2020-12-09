package com.minangdev.m_mahasiswa.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minangdev.m_mahasiswa.R
import kotlinx.android.synthetic.main.row_krs.view.*
import org.json.JSONArray
import org.json.JSONObject

class KrsAdapter(private val onItemClickListener : onItemClick) : RecyclerView.Adapter<KrsAdapter.viewHolder>() {

    var datas = JSONArray()

    fun setData(datas: JSONArray) {
        this.datas = datas
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KrsAdapter.viewHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.row_krs, parent, false)
        return viewHolder(mView)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.onBind(datas.getJSONObject(position))
    }

    override fun getItemCount(): Int {
        return datas.length()
    }

    inner class viewHolder(private val view : View) : RecyclerView.ViewHolder(view){
        fun onBind (jsonObject : JSONObject) = view.apply{
            tv_kode_matkul_krs.text = jsonObject.getJSONObject("kelas").getString("kode_matkul")
            tv_nama_matkul_krs.text = jsonObject.getJSONObject("kelas").getString("nama_matkul")
            tv_sks_matkul_krs.text = jsonObject.getJSONObject("kelas").getString("sks_matkul") + " SKS"
            var status = "Disetujui"
            if(jsonObject.getString("status")=="0"){
                status = "DiTolak"
                row_krs_layout.setBackgroundResource(R.drawable.bg_red_krs_tolak)
            }else{
                row_krs_layout.setBackgroundResource(R.drawable.bg_row_blue)
            }
            tv_status_krs.text = status
            var jadwal = ""
            val jadwalJson = jsonObject.getJSONObject("kelas").getJSONArray("jadwal")
            for(i in 0 until jadwalJson.length()){
                val jadwalObject = jadwalJson.getJSONObject(i)
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
}