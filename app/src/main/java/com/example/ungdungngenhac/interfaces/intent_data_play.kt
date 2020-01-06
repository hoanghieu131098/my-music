package com.example.ungdungngenhac.interfaces

import com.example.ungdungngenhac.models.BaiHat

interface intent_data_play {
    interface online{
        fun onclick(position : Int,namefm:String)
        fun onLongclick(data: ArrayList<BaiHat>, position : Int,namefm:String)
    }
    interface favorite{
        fun onclick( position : Int,namefm:String)
        fun onLongclick(data: ArrayList<BaiHat>, position : Int,namefm:String)
    }
}