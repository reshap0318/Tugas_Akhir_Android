package com.minangdev.myta.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minangdev.myta.R
import kotlinx.android.synthetic.main.row_one.view.*
import org.json.JSONArray
import org.json.JSONObject

class OneAdapter(private val onItemClickListener : onItemClick) : RecyclerView.Adapter<OneAdapter.viewHolder>() {

    var datas = JSONArray()

    fun setData(datas: JSONArray) {
        this.datas = datas
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): OneAdapter.viewHolder {
        val mView = LayoutInflater.from(viewGroup.context).inflate(R.layout.row_one, viewGroup, false)
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
            tv_name_topic.text = jsonObject.getString("name")
            setOnClickListener{
                onItemClickListener(jsonObject)
            }
        }
    }

}

