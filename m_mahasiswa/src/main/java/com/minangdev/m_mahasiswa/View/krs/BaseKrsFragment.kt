package com.minangdev.m_mahasiswa.View.krs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.minangdev.m_mahasiswa.R
import kotlinx.android.synthetic.main.fragment_base_krs.view.*

class BaseKrsFragment : Fragment() {

    private lateinit var root : View

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        root = inflater.inflate(R.layout.fragment_base_krs, container, false)
        setupViewPager(root.krs_view_pager)
        return root
    }

    fun setupViewPager(viewPager: ViewPager){
        val adapter = KrsPagerAdapter(getChildFragmentManager())
        adapter.addFragment(KrsFragment(), "KRS")
        adapter.addFragment(TranskripFragment(), "Transkrip")
        adapter.addFragment(GrafikFragment(), "Grafic")
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 3
        root.krs_tab.setupWithViewPager(viewPager)
    }
}