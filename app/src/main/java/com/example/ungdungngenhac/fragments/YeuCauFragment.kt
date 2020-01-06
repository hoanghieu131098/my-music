package com.example.ungdungngenhac.fragments


import android.content.Intent
import androidx.fragment.app.Fragment
import android.widget.Toast
import com.example.ungdungngenhac.Base.BaseFragment

import com.example.ungdungngenhac.R
import com.example.ungdungngenhac.notification.Notifications
import com.example.ungdungngenhac.notification.service.NotificationService
import kotlinx.android.synthetic.main.fragment_yeu_cau.*


/**
 * A simple [Fragment] subclass.
 *
 */
class YeuCauFragment : BaseFragment() {

    override val layoutID: Int
        get() = R.layout.fragment_yeu_cau
    override val title: String
        get() = "Ofline"
    override fun init() {

          btn_startService.setOnClickListener {
              if(ed_input_notification.text.equals("")){
                  Toast.makeText(activity,"Ban phai nhap vao !",Toast.LENGTH_SHORT).show()
              }else{
                  val input = ed_input_notification.text.toString()
                  val serviceIntent: Intent = Intent(activity,NotificationService::class.java)
                  serviceIntent.putExtra("InputMessage",input)
                  if (activity != null) {
                      activity!!.startService(serviceIntent)
                  }

              }




          }
        btn_stopService.setOnClickListener {
//            val serviceIntent: Intent = Intent(activity,NotificationService::class.java)
//            if (activity != null) {
//                activity!!.stopService(serviceIntent)
//            }
        }

    }


}
