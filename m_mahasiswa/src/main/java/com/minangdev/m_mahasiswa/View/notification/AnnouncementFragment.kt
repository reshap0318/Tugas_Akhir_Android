package com.minangdev.m_mahasiswa.View.notification

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.minangdev.m_mahasiswa.Adapter.NotificationAdapter
import com.minangdev.m_mahasiswa.Helper.LoadingDialog
import com.minangdev.m_mahasiswa.Helper.SharePreferenceManager
import com.minangdev.m_mahasiswa.R
import com.minangdev.m_mahasiswa.ViewModel.AnnouncementViewHolder
import kotlinx.android.synthetic.main.fragment_announcement.view.*


class AnnouncementFragment : Fragment() {
    private lateinit var root : View
    private lateinit var sharePreference : SharePreferenceManager
    private lateinit var announcementViewHolder: AnnouncementViewHolder
    private lateinit var notificationAdapter: NotificationAdapter
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
        root = inflater.inflate(R.layout.fragment_announcement, container, false)
        sharePreference = SharePreferenceManager(activity!!)
        token = sharePreference.getToken()
        announcementViewHolder = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(AnnouncementViewHolder::class.java)

        notificationAdapter = NotificationAdapter{
            val intent = Intent(activity, NotificationDetailActivity::class.java)
            intent.putExtra(NotificationDetailActivity.EXTRA_ID,it.getString("id"))
            startActivity(intent)
        }

        val layoutManager = LinearLayoutManager(activity)
        root.rv_notification.adapter = notificationAdapter
        root.rv_notification.layoutManager = layoutManager

        loadingDialog = LoadingDialog(activity!!)
        loadingDialog.showLoading()

        announcementViewHolder.setListData(token)
        announcementViewHolder.getListData().observe(this, Observer { datas ->
            notificationAdapter.setData(datas)
            loadingDialog.hideLoading()
            root.refresh_notification.isRefreshing = false
        })

        root.refresh_notification.setOnRefreshListener {
            announcementViewHolder.setListData(token)
        }
        return root
    }
}