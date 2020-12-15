package com.minangdev.myta.View.topic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.minangdev.myta.R
import kotlinx.android.synthetic.main.fragment_base.view.*

class BaseFragment : Fragment() {

    private lateinit var root : View

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        root = inflater.inflate(R.layout.fragment_base, container, false)
        setupViewPager(root.topic_view_pager)
        return root
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = SectionsPagerAdapter(getChildFragmentManager())
        adapter.addFragment(TopicActiveFragment(), "Active")
        adapter.addFragment(TopicFragment(), "Topic")
        adapter.addFragment(PeriodFragment(), "Period")
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 3
        root.topic_tab.setupWithViewPager(viewPager)
    }
}