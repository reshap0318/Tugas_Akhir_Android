package com.minangdev.m_mahasiswa.View.bimbingan

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.minangdev.m_mahasiswa.API.ApiBuilder
import com.minangdev.m_mahasiswa.API.ApiInterface
import com.minangdev.m_mahasiswa.Adapter.BimbinganGroupChatAdapter
import com.minangdev.m_mahasiswa.Helper.SharePreferenceManager
import com.minangdev.m_mahasiswa.Helper.UploadImage
import com.minangdev.m_mahasiswa.R
import com.minangdev.m_mahasiswa.View.MainActivity
import com.minangdev.m_mahasiswa.ViewModel.BimbinganViewModel
import kotlinx.android.synthetic.main.activity_bimbingan_group.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import androidx.lifecycle.Observer
import java.util.*

class BimbinganGroupActivity : AppCompatActivity() {

    private lateinit var sharePreference : SharePreferenceManager
    private lateinit var bimbinganViewModel: BimbinganViewModel
    private lateinit var bimbinganGroupChatAdapter: BimbinganGroupChatAdapter

    private lateinit var token: String
    private lateinit var senderId: String
    private lateinit var receiverId: String
    private lateinit var topicPeriodId: String
    private lateinit var groupChanel: String

    companion object {
        const val REQUEST_CODE_IMAGE_PICKER = 101
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bimbingan_group)

        sharePreference = SharePreferenceManager(this)
        sharePreference.isLogin()

        setSupportActionBar(toolbar_detail_group_chat)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar_detail_group_chat.setNavigationOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(MainActivity.EXTRA_FRAGMENT, 2)
            startActivity(intent)
        }

        token = sharePreference.getToken()
        senderId = sharePreference.getUserId()

        val mData = JSONObject(Objects.requireNonNull(intent.getStringExtra("data")))
        receiverId = mData.getString("to")
        topicPeriodId = mData.getString("topicPeriodId")
        groupChanel = mData.getString("groupChanel")

        tv_nama_detail_group_chat.text = mData.getString("groupName")
        tv_topic_detail_group_chat.text = "Chanel "+groupChanel
        val avatar = intent.getStringExtra("groupAvatar")
        if (avatar != null){
            Glide.with(this)
                    .load(avatar)
                    .fitCenter()
                    .centerCrop()
                    .into(img_profile_detail_group_chat)
        }

        bimbinganViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(BimbinganViewModel::class.java)
        bimbinganGroupChatAdapter = BimbinganGroupChatAdapter(this, senderId)

        rv_chat_detail_group_chat.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        rv_chat_detail_group_chat.layoutManager = linearLayoutManager
        rv_chat_detail_group_chat.adapter = bimbinganGroupChatAdapter

        bimbinganViewModel.loadDetailChatGroup(groupChanel)
        bimbinganViewModel.detailChatGroup().observe(this, Observer {
            bimbinganGroupChatAdapter.setData(it)
        })

        chat_send_detail_group_chat_btn.setOnClickListener{
            sendingMessage()
        }

        img_send_detail_group_chat_btn.setOnClickListener{
            openImage()
        }
    }

    fun sendingMessage(){
        val mMessage = tv_chat_detail_group_chat.text.toString()
        if(mMessage==""){
            Toast.makeText(this, "Pesan Tidak Boleh Kosong", Toast.LENGTH_LONG).show()
            return
        }
        val reqboReceiverId = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),receiverId)
        val reqboMmessage = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),mMessage)
        val reqboTopicPeriod = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),topicPeriodId)
        val reqboGroupChanel = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),groupChanel)
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val respondBody = apiBuilder.bimbinganSendGroup(token, reqboReceiverId, reqboMmessage, reqboTopicPeriod,reqboGroupChanel)
        respondBody.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    Toast.makeText(this@BimbinganGroupActivity, "Berhasil Mengirim Pesan", Toast.LENGTH_SHORT).show()
                    tv_chat_detail_group_chat.setText("")
                } else if (response.code() == 422) {
                    Toast.makeText(this@BimbinganGroupActivity, "Pesan Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("sendingMessageGroup", "Error Code : " + response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("debug", "onFailure: ERROR > " + t.toString())
            }

        })
    }

    fun openImage(){
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, BimbinganGroupActivity.REQUEST_CODE_IMAGE_PICKER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                BimbinganGroupActivity.REQUEST_CODE_IMAGE_PICKER -> {
                    sendingImg(data?.data)
                }
            }
        }
    }

    fun sendingImg(data: Uri?){
        val loadingBar = ProgressDialog(this)
        loadingBar.setMessage("Please Wait, Image is Sending...")
        loadingBar.show()

        val parcelFileDescriptor = contentResolver.openFileDescriptor(data!!, "r", null) ?: null
        val inputStream = FileInputStream(parcelFileDescriptor!!.fileDescriptor)
        val file = File( cacheDir, getFileName(data!!))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        val sendImg = UploadImage(file)
        val formSendImg = MultipartBody.Part.createFormData("img", file.name, sendImg)

        val reqboReceiverId = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),receiverId)
        val reqboMmessage = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),"sent you an image")
        val reqboTopicPeriod = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),topicPeriodId)
        val reqboGroupChanel = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),groupChanel)
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val respondBody = apiBuilder.bimbinganSendGroup(token, reqboReceiverId, reqboMmessage, reqboTopicPeriod, reqboGroupChanel, formSendImg)
        respondBody.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    Toast.makeText(this@BimbinganGroupActivity, "Berhasil Mengirim Pesan", Toast.LENGTH_SHORT).show()
                    tv_chat_detail_group_chat.setText("")
                } else {
                    Log.e("submitLogin", "Error Code : " + response.code().toString())
                }
                loadingBar.dismiss()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("debug", "onFailure: ERROR > " + t.toString())
            }

        })
    }

    fun getFileName(fileUri: Uri): String{
        var name = ""
        val returnCursor = contentResolver?.query(fileUri, null, null, null, null)
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
        return name
    }

}