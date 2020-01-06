package com.example.ungdungngenhac.Base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ungdungngenhac.R

abstract class BaseFragment :Fragment() {
    protected  abstract val layoutID:Int
    abstract  val title:String
    abstract  fun init()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      val view:View= inflater.inflate(layoutID, container, false)
        return view
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }



}