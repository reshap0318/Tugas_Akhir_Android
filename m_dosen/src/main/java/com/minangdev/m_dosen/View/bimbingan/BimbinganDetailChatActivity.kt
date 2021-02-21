package com.minangdev.m_dosen.View.bimbingan

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import com.minangdev.m_dosen.API.ApiBuilder
import com.minangdev.m_dosen.API.ApiInterface
import com.minangdev.m_dosen.Adapter.BimbinganChatAdapter
import com.minangdev.m_dosen.Helper.SharePreferenceManager
import com.minangdev.m_dosen.Helper.UploadImage
import com.minangdev.m_dosen.R
import com.minangdev.m_dosen.View.profile.ProfileFragment.Companion.REQUEST_CODE_IMAGE_PICKER
import com.minangdev.m_dosen.ViewModel.BimbinganViewModel
import kotlinx.android.synthetic.main.activity_bimbingan_detail_chat.*
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
import java.lang.Exception

class BimbinganDetailChatActivity : AppCompatActivity() {

    private lateinit var sharePreference : SharePreferenceManager
    private lateinit var bimbinganViewModel: BimbinganViewModel
    private lateinit var bimbinganChatAdapter: BimbinganChatAdapter
    private lateinit var ref: DatabaseReference

    private lateinit var token: String
    private lateinit var senderId: String
    private lateinit var receiverId: String
    private lateinit var topicPeriodId: String
    private lateinit var topicPeriod : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bimbingan_detail_chat)

        sharePreference = SharePreferenceManager(this)
        sharePreference.isLogin()

        setSupportActionBar(toolbar_detail_chat_bimbingan)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar_detail_chat_bimbingan.setNavigationOnClickListener{
            onBackPressed()
        }
        //init
        token = sharePreference.getToken()
        senderId = sharePreference.getUserId()
        receiverId = intent.getStringExtra("receiverId").toString()
        topicPeriodId = intent.getStringExtra("topicPeriodId").toString()
        topicPeriod = intent.getStringExtra("topicPeriod").toString()

        val name = intent.getStringExtra("receiverNama").toString()
        val avatar = intent.getStringExtra("receiverAvatar")

        tv_nama_detail_chat_bimbingan.text = name
        if(avatar != null){
            Glide.with(this)
                .load(avatar)
                .fitCenter()
                .centerCrop()
                .into(img_profile_detail_chat_bimbingan)
        }

        bimbinganViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(BimbinganViewModel::class.java)
        bimbinganChatAdapter = BimbinganChatAdapter(this, senderId)

        bimbinganChatAdapter.setOnLongClick { jsonObject->
            MaterialAlertDialogBuilder(this)
            .setTitle("Delete This Chat??")
            .setNegativeButton("No") { dialog, which ->

            }
            .setPositiveButton("Yes") { dialog, which ->
                deleteRow(jsonObject.getString("id"))
            }
            .show()
        }

        rv_chat_detail_chat_bimbingan.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        rv_chat_detail_chat_bimbingan.layoutManager = linearLayoutManager
        rv_chat_detail_chat_bimbingan.adapter = bimbinganChatAdapter

        bimbinganViewModel.loadDetailChat(senderId, receiverId, topicPeriodId)
        bimbinganViewModel.detailChat().observe(this, Observer {
            bimbinganChatAdapter.setData(it)
        })

        tv_topic_detail_chat_bimbingan.text = topicPeriod

        chat_send_detail_chat_bimbingan_btn.setOnClickListener{
            sendingMessage()
        }

        img_send_detail_chat_bimbingan_btn.setOnClickListener{
            openImage()
        }
        readChat()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.download_chat_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.download_chat -> {
                downloadBimbingan()
                return true
            }
            else -> return true
        }
    }

    private fun deleteRow(id: String) {
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val respondBody = apiBuilder.bimbinganDelete(token, id)
        respondBody.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    Toast.makeText(this@BimbinganDetailChatActivity, "Berhasil Menghapus Data", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("deleteChat", "Error Code : " + response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("debug", "onFailure: ERROR > " + t.toString())
            }

        })
    }

    fun sendingMessage(){
        val mMessage = tv_chat_detail_chat_bimbingan.text.toString()
        if(mMessage==""){
            Toast.makeText(this, "Pesan Tidak Boleh Kosong", Toast.LENGTH_LONG).show()
            return
        }
        val reqboReceiverId = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),receiverId)
        val reqboMmessage = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),mMessage)
        val reqboTopicPeriod = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),topicPeriodId)
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val respondBody = apiBuilder.bimbinganSend(token, reqboReceiverId, reqboMmessage, reqboTopicPeriod)
        respondBody.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    Toast.makeText(this@BimbinganDetailChatActivity, "Berhasil Mengirim Pesan", Toast.LENGTH_SHORT).show()
                    tv_chat_detail_chat_bimbingan.setText("")
                } else if (response.code() == 422) {
                    Toast.makeText(this@BimbinganDetailChatActivity, "Pesan Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("submitLogin", "Error Code : " + response.code().toString())
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
            startActivityForResult(it, REQUEST_CODE_IMAGE_PICKER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                REQUEST_CODE_IMAGE_PICKER -> {
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
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val respondBody = apiBuilder.bimbinganSend(token, reqboReceiverId, reqboMmessage, reqboTopicPeriod, formSendImg)
        respondBody.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    Toast.makeText(this@BimbinganDetailChatActivity, "Berhasil Mengirim Pesan", Toast.LENGTH_SHORT).show()
                    tv_chat_detail_chat_bimbingan.setText("")
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

    fun downloadBimbingan(){
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val respondBody = apiBuilder.bimbinganCetak(token, receiverId, topicPeriodId)
        respondBody.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    Toast.makeText(this@BimbinganDetailChatActivity, "File Sent to Your Email", Toast.LENGTH_SHORT).show()
                    tv_chat_detail_chat_bimbingan.setText("")
                } else if (response.code() == 422) {
                    Toast.makeText(this@BimbinganDetailChatActivity, "Set Email Before Use This Fiture", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("donwloadChat", "Error Code : " + response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("debug", "onFailure: ERROR > " + t.toString())
            }

        })
    }
    var readListner: ValueEventListener? = null
    fun readChat(){
        ref = FirebaseDatabase.getInstance().getReference("Chat")
        readListner = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children){
                    val data: HashMap<String, String> = i.value as HashMap<String, String>
                    if(data.get("receiver").equals(senderId) && data.get("sender").equals(receiverId)){
                        val hashMap = HashMap<String, Any>()
                        hashMap["isRead"] = 1
                        i.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onPause() {
        super.onPause()
        ref.removeEventListener(readListner!!)
    }
}