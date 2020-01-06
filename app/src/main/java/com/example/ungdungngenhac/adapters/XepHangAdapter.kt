package com.example.ungdungngenhac.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.ungdungngenhac.R
import com.example.ungdungngenhac.interfaces.onItemClickListener
import com.example.ungdungngenhac.models.BaiHat

class XepHangAdapter(val context: Context) : RecyclerView.Adapter<XepHangAdapter.Viewholder>() {
    private var arr: ArrayList<BaiHat>? = null

    fun setData(arr:ArrayList<BaiHat>){
        this.arr=arr
        notifyDataSetChanged()
    }
    private var databackup: ArrayList<BaiHat> = ArrayList()
    fun addAllDataSearch() {
        this.databackup.addAll(arr!!)
    }

    //TODO onclick va onclongclick item
    private var listener: onItemClickListener.online? = null

    fun setListeners(itemclick: onItemClickListener.online) {
        this.listener = itemclick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        return Viewholder(LayoutInflater.from(parent.context).inflate(R.layout.item_music, parent, false))
    }

    override fun getItemCount(): Int {
        if(arr==null){
            return 0
        }
        return arr!!.size ?: 0
    }

    fun filter(s: String) {
        arr!!.clear()
        if (s.isEmpty()) {
            arr!!.addAll(databackup)
        } else {
            for (i in databackup.indices) {
                if (databackup[i].tenBaiHat.toLowerCase().contains(s.toLowerCase())) {
                    arr!!.add(databackup[i])
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        if (holder != null) {
            holder.tv_item_music_title.text = arr!!.get(position).tenBaiHat
            holder.tv_item_music_namecasi.text = arr!!.get(position).tenCaSi

            Glide.with(context).load(arr!!.get(position).anhBaiHat).transforms(CenterCrop(), RoundedCorners(30))
                .transition(DrawableTransitionOptions.withCrossFade()).into(holder.img_item_music)

            //TODO:Onclick
            holder.itemView.setOnClickListener {
                listener!!.setOnClickListener(position)

            }
            holder.itemView.setOnLongClickListener {
                listener!!.setOnLongClickListener(position)

                // Neu la false thì sau khi gọi longclick sẽ gọi lại click
                return@setOnLongClickListener true
            }

        }
    }

    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img_item_music: ImageView = itemView.findViewById(R.id.img_item_music)
        var tv_item_music_title: TextView = itemView.findViewById(R.id.tv_item_music_title)
        var tv_item_music_namecasi: TextView = itemView.findViewById(R.id.tv_item_music_namecasi)

    }
}


