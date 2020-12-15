package com.minangdev.m_mahasiswa.Adapter

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.minangdev.m_mahasiswa.R
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.message_item_left.view.*
import kotlinx.android.synthetic.main.message_item_right.view.*
import org.json.JSONArray

class BimbinganChatAdapter(
        mContext : Context,
        userId : String
) : RecyclerView.Adapter<BimbinganChatAdapter.viewHolder>()
{

    private val mContext: Context
    private val userId: String

    var mData = JSONArray()

    init {
        this.mContext = mContext
        this.userId = userId
    }

    fun setData(datas: JSONArray) {
        this.mData = datas
        notifyDataSetChanged()
    }

    inner class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var show_text_message : TextView? = null
        var img_view : ImageView? = null
        var text_seen : TextView? = null

        init {
            show_text_message = itemView.findViewById(R.id.tv_show_text_message)
            img_view = itemView.findViewById(R.id.img_message)
            text_seen = itemView.findViewById(R.id.tv_text_seen)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): viewHolder {
        val view = if(position==1){
            LayoutInflater.from(mContext).inflate(R.layout.message_item_right, parent, false)
        }else{
            LayoutInflater.from(mContext).inflate(R.layout.message_item_left, parent, false)
        }
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val oneData = mData.getJSONObject(position)
        if(oneData.getString("message").equals("sent you an image") && !oneData.getString("img").equals("")){
            val img = oneData.getString("img")
            holder.img_view!!.isVisible = true
            holder.show_text_message!!.isVisible = false
            Glide.with(mContext)
                    .load(img)
                    .fitCenter()
                    .centerCrop()
                    .into(holder.img_view!!)
        }else{
            holder.show_text_message!!.text = oneData.getString("message")
        }

        if (position == mData.length()-1){
            if(oneData.getString("isRead").equals(1)){
                holder.text_seen!!.text = "Seen"
            }else{
                holder.text_seen!!.text = "Sent"
            }
            if(oneData.getString("message").equals("sent you an image") && !oneData.getString("img").equals("")){
                val lp = holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                lp!!.addRule(RelativeLayout.BELOW, R.id.img_message)
                holder.text_seen!!.layoutParams = lp
            }
        }else{
            holder.text_seen!!.isVisible = false
        }
    }

    override fun getItemCount(): Int {
        return mData.length()
    }

    override fun getItemViewType(position: Int): Int {
        return if(mData.getJSONObject(position).getString("sender").equals(userId)){
            1
        }else{
            0
        }
    }

}