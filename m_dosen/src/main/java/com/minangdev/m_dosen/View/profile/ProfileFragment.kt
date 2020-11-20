package com.minangdev.m_dosen.View.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.FileUtils
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.minangdev.m_dosen.API.ApiBuilder
import com.minangdev.m_dosen.API.ApiInterface
import com.minangdev.m_dosen.Helper.SharePreferenceManager
import com.minangdev.m_dosen.Helper.UploadImage
import com.minangdev.m_dosen.R
import com.minangdev.m_dosen.View.ChangePasswordActivity
import kotlinx.android.synthetic.main.fragment_profile.view.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception


class ProfileFragment : Fragment() {

    private lateinit var profileViewModel : ProfileViewModel
    private lateinit var root : View
    private lateinit var sharePreference : SharePreferenceManager
    lateinit var token: String
    companion object {
        const val REQUEST_CODE_IMAGE_PICKER = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_profile, container, false)
        sharePreference = SharePreferenceManager(root.context)
        sharePreference.isLogin()

        root.btn_ganti_password_profile.setOnClickListener { view ->
            val intent = Intent(activity, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        root.btn_ganti_avatar.setOnClickListener{
            openImage()
        }

        root.btn_logout_profile.setOnClickListener{
            logout()
        }

        profileSetData(name="", nip="")

        token = sharePreference.getToken()

        profileViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(ProfileViewModel::class.java)

        loadData()
        return root
    }

    private fun loadData() {
        profileViewModel.setData(token)
        profileViewModel.getData().observe(this, Observer {data ->
            val name = data.getString("name")
            val nip = data.getString("username")
            val img = data.getString("avatar")
            profileSetData(name=name, nip=nip, img=img)
        })
    }

    private fun profileSetData(name:String, nip:String, img:String? = null) {
        root.tv_name_profile.text = name
        root.tv_nip_profile.text = nip
        Glide.with(root)
            .load(img)
            .fitCenter()
            .centerCrop()
            .into(root.img_profile)
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
//                    root.img_profile.setImageURI(selectedImage)
                    changeProfile(data?.data)
                }
            }
        }
    }

    fun changeProfile(data: Uri?) {

        val parcelFileDescriptor = activity!!.contentResolver.openFileDescriptor(data!!, "r", null) ?: null

        val inputStream = FileInputStream(parcelFileDescriptor!!.fileDescriptor)
        val file = File( activity?.cacheDir, getFileName(data!!))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        val avatar = UploadImage(file)

        val formAvatar = MultipartBody.Part.createFormData("avatar", file.name, avatar)

        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val profile = apiBuilder.changeAvatar(token, formAvatar)
        profile.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    loadData()
                } else {
                    Log.e("Res_Change_Avatar", "Ada Error di server Code : " + response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_Change_Avatar", "onFailure: ERROR > " + t.toString());
            }
        })
    }

    fun getFileName(fileUri: Uri): String{
        var name = ""
        val returnCursor = activity?.contentResolver?.query(fileUri, null, null, null, null)
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
        return name
    }

    fun logout(){
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val profile = apiBuilder.logout(token)
        profile.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    Toast.makeText(activity, "Berhasil LogOut", Toast.LENGTH_SHORT).show()
                    sharePreference.isLogin()
                } else {
                    Log.e("Res_Logout", "Ada Error di server Code : " + response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_Logout", "onFailure: ERROR > " + t.toString());
            }

        });
    }
}