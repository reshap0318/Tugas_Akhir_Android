package com.minangdev.m_mahasiswa.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minangdev.m_mahasiswa.R
import kotlinx.android.synthetic.main.row_transkrip_sub.view.*
import org.json.JSONArray
import org.json.JSONObject

class TranskripSubAdapter(private val context: Context, private val datas: JSONArray) : RecyclerView.Adapter<TranskripSubAdapter.SubViewHolder>() {

    class SubViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        fun onBind (jsonObject : JSONObject) = itemView.apply{
            val matkulKode = jsonObject.getString("kode")
            val sks = jsonObject.getString("sks")
            tv_matkul_transkrip.text = jsonObject.getString("nama")
            tv_kode_matkul_transkrip.text = "${matkulKode} - ${sks} SKS"
            var nilai = "-"
            if(jsonObject.getString("nilaiAngka")!="null"){
                nilai = jsonObject.getString("nilaiAngka")
            }
            if(nilai.equals("E") || nilai.equals("D") || nilai.equals("C-")){
                row_transkrip_sub_layout.setBackgroundResource(R.drawable.bg_red_transkrip_e_cmin)
            }else if(nilai.equals("C") || nilai.equals("C+") || nilai.equals("B-")){
                row_transkrip_sub_layout.setBackgroundResource(R.drawable.bg_yellow_transkrip_c_bmin)
            }else{
                row_transkrip_sub_layout.setBackgroundResource(R.drawable.bg_row_blue)
            }
            tv_nilai_matkul_transkrip.text = nilai
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_transkrip_sub, parent, false)
        return SubViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubViewHolder, position: Int) {
        holder.onBind(datas.getJSONObject(position))
    }

    override fun getItemCount() = datas.length()

}