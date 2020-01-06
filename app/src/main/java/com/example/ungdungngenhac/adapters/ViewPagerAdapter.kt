package com.example.ungdungngenhac.adapters


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.ungdungngenhac.Base.BaseFragment
import com.example.ungdungngenhac.fragments.XepHangFragment
import com.example.ungdungngenhac.fragments.YeuCauFragment
import com.example.ungdungngenhac.fragments.YeuThichFragment


class ViewPagerAdapter(var fm: FragmentManager,var fms:MutableList<BaseFragment>) : FragmentPagerAdapter(fm) {
    private  var fmss:MutableList<BaseFragment>?=null
    init {
        this.fmss=fms
    }

    override fun getItem(p0: Int): Fragment{
        return fmss!!.get(p0)
    }

    override fun getPageTitle(position: Int): CharSequence? {
       return fmss!!.get(position).title
    }

    override fun getCount(): Int= fmss!!.size?:0
}