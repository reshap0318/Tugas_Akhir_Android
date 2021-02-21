package com.minangdev.m_mahasiswa.View.notification

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.minangdev.m_mahasiswa.Adapter.AnotherNotificationAdapter
import com.minangdev.m_mahasiswa.Helper.LoadingDialog
import com.minangdev.m_mahasiswa.Helper.SharePreferenceManager
import com.minangdev.m_mahasiswa.R
import com.minangdev.m_mahasiswa.ViewModel.AnnouncementViewHolder
import kotlinx.android.synthetic.main.fragment_another_notification.view.*

class AnotherNotificationFragment : Fragment() {

    private lateinit var root : View
    private lateinit var sharePreference : SharePreferenceManager
    private lateinit var announcementViewHolder: AnnouncementViewHolder
    private lateinit var notificationAdapter: AnotherNotificationAdapter
    lateinit var loadingDialog: LoadingDialog
    private lateinit var token : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_another_notification, container, false)

        sharePreference = SharePreferenceManager(activity!!)
        token = sharePreference.getFCMTOKEN()
        announcementViewHolder = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(AnnouncementViewHolder::class.java)

        notificationAdapter = AnotherNotificationAdapter()

        val layoutManager = LinearLayoutManager(activity)
        root.rv_another_notification.adapter = notificationAdapter
        root.rv_another_notification.layoutManager = layoutManager
        loadingDialog = LoadingDialog(activity!!)
        loadingDialog.showLoading()

        announcementViewHolder.setAnother(token)
        announcementViewHolder.getAnotherData().observe(this, Observer { datas ->
//            Log.e("aaaa", "onCreateView: "+datas.toString() )
            notificationAdapter.setData(datas)
            loadingDialog.hideLoading()
            root.refresh_another_notification.isRefreshing = false
        })

        root.refresh_another_notification.setOnRefreshListener {
            announcementViewHolder.setListData(token)
        }

        return root
    }
}