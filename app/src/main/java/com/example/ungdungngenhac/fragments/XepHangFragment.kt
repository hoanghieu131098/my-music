package com.example.ungdungngenhac.fragments


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat


import com.example.ungdungngenhac.adapters.XepHangAdapter
import com.example.ungdungngenhac.models.BaiHat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ungdungngenhac.R
import kotlinx.android.synthetic.main.fragment_xep_hang.*
import androidx.recyclerview.widget.DividerItemDecoration

import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import com.example.ungdungngenhac.API.APIUtils
import com.example.ungdungngenhac.API.DataClient
import com.example.ungdungngenhac.Base.BaseFragment
import com.example.ungdungngenhac.interfaces.Constants
import com.example.ungdungngenhac.interfaces.intent_data_play

import com.example.ungdungngenhac.interfaces.onItemClickListener
import com.example.ungdungngenhac.notification.service.NotificationService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Context.ACTIVITY_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.app.ActivityManager

import android.content.Context.ACTIVITY_SERVICE
import androidx.core.content.ContextCompat.getSystemService

import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import androidx.core.content.ContextCompat.getSystemService


class XepHangFragment : BaseFragment(), onItemClickListener.online {
    private var arrBaiHat: ArrayList<BaiHat>? = null
    public lateinit var xephangadapter: XepHangAdapter
    private var listenerData: intent_data_play.online? = null
    private var listenerDataSong: sendData? = null


    fun setlistenerDataSong(listenerDataSong: sendData) {
        this.listenerDataSong = listenerDataSong
    }

    fun setDataLisener(listenerData: intent_data_play.online) {
        this.listenerData = listenerData
    }


    override fun setOnClickListener(position: Int) {
        if (listenerData != null) {

            listenerData!!.onclick(position, Constants.IS_ONLINE)


        }
    }


    override fun onDestroyView() {
        super.onDestroyView()


    }

    override fun setOnLongClickListener(position: Int) {
        if (listenerData != null) {
            listenerData!!.onLongclick(arrBaiHat!!, position, "online")

        }
    }

    override val layoutID: Int
        get() = R.layout.fragment_xep_hang

    override val title: String
        get() = "Online"


    override fun init() {
        recycle_XepHang.layoutManager = LinearLayoutManager(activity!!)
        recycle_XepHang.setHasFixedSize(true)
        val itemDecor = DividerItemDecoration(requireContext(), VERTICAL)
        recycle_XepHang.addItemDecoration(itemDecor)
        loadDataServer()
        xephangadapter = XepHangAdapter(activity!!.application)
        recycle_XepHang.setAdapter(xephangadapter)
    }

    private fun loadDataServer() {
        var dataclient: DataClient = APIUtils.getData()
        var callback: Call<ArrayList<BaiHat>> = dataclient.SelectAll()
        callback.enqueue(object : Callback<java.util.ArrayList<BaiHat>> {
            override fun onResponse(
                call: Call<java.util.ArrayList<BaiHat>>,
                response: Response<java.util.ArrayList<BaiHat>>
            ) {
                var data: ArrayList<BaiHat> = arrayListOf()
                if (response.body() != null) {
                    data = response.body()!!
                }
                arrBaiHat = data
                xephangadapter.setData(arrBaiHat!!)
                //ADD data vào databackup để search
                xephangadapter.addAllDataSearch()
                if (listenerDataSong != null) {
                    listenerDataSong!!.setDataSong(data)
                }
                xephangadapter.setListeners(this@XepHangFragment)
                progressBar.hide()
            }

            override fun onFailure(call: Call<java.util.ArrayList<BaiHat>>, t: Throwable) {

            }
        })
    }


    interface sendData {
        fun setDataSong(listData: ArrayList<BaiHat>)
    }

}
