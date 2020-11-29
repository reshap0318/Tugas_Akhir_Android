package com.minangdev.m_dosen.View.bimbingan

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class BimbinganPagerAdapter (fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    var fragmentList = arrayListOf<Fragment>()
    var titleList = arrayListOf<String>()

    fun addFragment(fragment : Fragment, title : String){
        fragmentList.add(fragment)
        titleList.add(title)
    }

    override fun getCount() = fragmentList.size

    override fun getItem(position: Int) = fragmentList[position]

    override fun getPageTitle(position: Int) = titleList[position]

}