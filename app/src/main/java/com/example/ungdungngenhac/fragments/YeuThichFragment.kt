package com.example.ungdungngenhac.fragments


import android.app.AlertDialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.util.Log
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ungdungngenhac.Base.BaseFragment
import com.example.ungdungngenhac.MainActivity

import com.example.ungdungngenhac.R
import com.example.ungdungngenhac.SQLite.BaiHatDB
import com.example.ungdungngenhac.adapters.FavoriteAdapter
import com.example.ungdungngenhac.interfaces.intent_data_play
import com.example.ungdungngenhac.interfaces.onItemClickListener
import com.example.ungdungngenhac.models.BaiHat
import kotlinx.android.synthetic.main.fragment_yeu_thich.*

import android.widget.Toast
import com.example.ungdungngenhac.interfaces.Constants


/**
 * A simple [Fragment] subclass.
 *
 */
class YeuThichFragment : BaseFragment(), onItemClickListener.favorite, MainActivity.sendDataBaiHat {


    //TODO:Receiver data from Main
    override fun sendDatafavorite(data: BaiHat) {
        if (checkDataBaihat(data)) {
            SQLiteBaiHat!!.addBaiHat(data, username!!)
            arrBaiHat.add(data)
            favoriteAdapter.notifyItemInserted(arrBaiHat.size - 1)
            favoriteAdapter.addAllDataSearch()
            var activity = activity as MainActivity
            activity.getDataSongLike().postValue(arrBaiHat)
        }
    }
    private var arrBaiHat: ArrayList<BaiHat> = ArrayList()

    lateinit var favoriteAdapter: FavoriteAdapter
    private var listenerData: intent_data_play.favorite? = null
    private var SQLiteBaiHat: BaiHatDB? = null
    private var username: String? = null

    private fun checkDataBaihat(bh: BaiHat): Boolean {
        var status = 0
        for (i in arrBaiHat.indices) {
            if (arrBaiHat[i].linkBaiHat.equals(bh.linkBaiHat)) {
                status++
            }
        }
        if (status == 0) {
            return true
        }
        return false
    }

    fun setDataLisener(listenerData: intent_data_play.favorite) {
        this.listenerData = listenerData
    }

    override fun setOnClickListener(position: Int) {
        if (listenerData != null) {
            listenerData!!.onclick( position, Constants.IS_LIKE)

        }
    }

    override fun setOnLongClickListener(position: Int) {
        Toast.makeText(activity!!, position.toString(), Toast.LENGTH_SHORT).show()
        val popupMenu =
            PopupMenu(activity!!.application, recycle_favorite.findViewHolderForAdapterPosition(position)!!.itemView)
        popupMenu.getMenuInflater().inflate(R.menu.option_menu_fm_favorite, popupMenu.getMenu())
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.itemDelete -> {
                    Log.d("Vitri: ", "" + position)
                    val builder = AlertDialog.Builder(activity!!)
                    builder.setMessage("Bạn có chắc chắn muốn xóa bài hát này?")
                    builder.setIcon(R.drawable.ic_delete_forever)
                    builder.setTitle("Thông báo")

                    builder.setPositiveButton("No",
                        DialogInterface.OnClickListener { dialogInterface, it -> })
                    builder.setNegativeButton("Yes", DialogInterface.OnClickListener { dialogInterface, it ->
                        Log.d("IDddddđ: ", "" + arrBaiHat.get(position).linkBaiHat)
                        SQLiteBaiHat!!.deleteBaiHat(arrBaiHat.get(position).linkBaiHat, username!!)
                        arrBaiHat.removeAt(position)
                        favoriteAdapter.notifyItemRemoved(position)
                        favoriteAdapter.notifyItemRangeRemoved(position, arrBaiHat.size)
                        favoriteAdapter.addAllDataSearch()
                    })
                    builder.show()
                    true
                }
                else -> false
            }

        }
        try {
            val fieldMPopup = android.widget.PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(popupMenu)
            mPopup.javaClass
                .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(mPopup, true)
        } catch (e: Exception) {
            Log.e("Main", "Error showing menu icons.", e)
        } finally {
            popupMenu.show()
        }
    }


    override val layoutID: Int
        get() = R.layout.fragment_yeu_thich
    override val title: String
        get() = "Favorite"

    override fun init() {
        recycle_favorite.layoutManager = LinearLayoutManager(activity)
        recycle_favorite.setHasFixedSize(true)
        val itemDecor = DividerItemDecoration(requireContext(), VERTICAL)
        recycle_favorite.addItemDecoration(itemDecor)
        //Lấy data bài hát được yêu thích
        //TODO:Lấy username account
        var pre: SharedPreferences =
            this.getActivity()!!.getSharedPreferences("my_account", AppCompatActivity.MODE_PRIVATE)
        username = pre.getString("username", "")
        //TODO :Khởi tạo SQLite

        var activity: MainActivity = getActivity() as MainActivity
        activity.setDataBaiHat(this@YeuThichFragment)

        SQLiteBaiHat = BaiHatDB(activity!!.application)
        arrBaiHat = SQLiteBaiHat!!.getAllBaiHat(username!!)
        activity.getDataSongLike().postValue(arrBaiHat)

        if (arrBaiHat != null) {
            favoriteAdapter = FavoriteAdapter(activity!!.application, arrBaiHat)
            recycle_favorite.setAdapter(favoriteAdapter)
            favoriteAdapter.notifyDataSetChanged()
            favoriteAdapter.addAllDataSearch()
            favoriteAdapter.setListeners(this@YeuThichFragment)
        } else {
            //tv_null.isVisible = true
        }


    }


}
