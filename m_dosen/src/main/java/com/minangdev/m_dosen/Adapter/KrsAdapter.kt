package com.minangdev.m_dosen.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.minangdev.m_dosen.R
import kotlinx.android.synthetic.main.row_krs.view.*
import org.json.JSONArray
import org.json.JSONObject

class KrsAdapter(private val onItemClickListener : onItemClick) : RecyclerView.Adapter<KrsAdapter.viewHolder>() {

    var datas = JSONArray()
    private var onLongClickListener : ((JSONObject, View) -> Unit)? = null

    fun setData(datas: JSONArray) {
        this.datas = datas
        notifyDataSetChanged()
    }

    fun setOnLongClick(onLongClickListener : (JSONObject, View) -> Unit){
        this.onLongClickListener = onLongClickListener
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
            tv_kode_matkul_krs_bimbingan.text = jsonObject.getJSONObject("kelas").getString("kode_matkul")
            tv_nama_matkul_krs_bimbingan.text = jsonObject.getJSONObject("kelas").getString("nama_matkul")
            tv_sks_matkul_krs_bimbingan.text = jsonObject.getJSONObject("kelas").getString("sks_matkul") + " SKS"
            var nilai = "-"
            if(!jsonObject.getString("nilai").equals("null")){
                nilai = jsonObject.getString("nilai")
            }
            tv_nilai_matkul_krs_bimbingan.text = nilai
            var status = "Disetujui"
            if(jsonObject.getString("status")=="0"){
                status = "DiTolak"
                row_krs_layout.setBackgroundResource(R.drawable.bgkrsred)
                layout_nilai_krs.setBackgroundResource(R.drawable.baseline_circle_red)
            }else{
                row_krs_layout.setBackgroundResource(R.drawable.bgrowmahasiswa)
                layout_nilai_krs.setBackgroundResource(R.drawable.baseline_circle_green)
            }
            tv_status_krs_bimbingan.text = status
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
            }
            tv_jadwal_matkul_krs_bimbingan.text = jadwal

            setOnClickListener{
                onItemClickListener(jsonObject)
            }

            setOnLongClickListener{
                onLongClickListener?.invoke(jsonObject, this)
                true
            }
        }
    }
}