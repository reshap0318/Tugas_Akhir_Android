package com.minangdev.m_dosen.View.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.minangdev.m_dosen.API.ApiBuilder
import com.minangdev.m_dosen.API.ApiInterface
import com.minangdev.m_dosen.Helper.LoadingDialog
import com.minangdev.m_dosen.Helper.SharePreferenceManager
import com.minangdev.m_dosen.Helper.UploadImage
import com.minangdev.m_dosen.R
import com.minangdev.m_dosen.View.ChangePasswordActivity
import com.minangdev.m_dosen.ViewModel.ProfileViewModel
import kotlinx.android.synthetic.main.dialog_change_email.view.*
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
    private lateinit var sharePreference : SharePreferenceManager

    private lateinit var mCreate : AlertDialog
    lateinit var loadingDialog: LoadingDialog

    private lateinit var mCreateDialogView : View
    private lateinit var root : View

    lateinit var token: String

    var email: String? = ""
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
        initChangePassword()

        root.btn_ganti_password_profile.setOnClickListener { view ->
            val intent = Intent(activity, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        root.btn_ganti_avatar.setOnClickListener{
            openImage()
        }

        root.btn_logout_profile.setOnClickListener{
            MaterialAlertDialogBuilder(context!!)
            .setTitle("Are You Sure Logout??")
            .setNegativeButton("No") { dialog, which ->

            }
            .setPositiveButton("Yes") { dialog, which ->
                logout()
            }
            .show()
        }

        root.btn_ganti_email.setOnClickListener{
            mCreateDialogView.email_form_profile.error = ""
            mCreateDialogView.email_form_profile.editText?.setText(email)
            mCreate.show()
        }

        profileSetData(name="", nip="")

        token = sharePreference.getToken()

        profileViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(ProfileViewModel::class.java)

        loadingDialog = LoadingDialog(activity!!)
        loadData()
        return root
    }

    private fun loadData() {
        loadingDialog.showLoading()
        profileViewModel.setData(token)
        profileViewModel.getData().observe(this, Observer {data ->
            val name = data.getString("name")
            val nip = data.getString("username")
            val img = data.getString("avatar")
            email = if (data.getString("email").equals("null")){
                ""
            }else{
                data.getString("email")
            }
            profileSetData(name=name, nip=nip, img=img)
            loadingDialog.hideLoading()
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
        loadingDialog.showLoading()
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
                loadingDialog.hideLoading()
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
        loadingDialog.showLoading()
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
                loadingDialog.hideLoading()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_Logout", "onFailure: ERROR > " + t.toString());
            }

        });
    }

    fun initChangePassword(){
        mCreateDialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_change_email, null)
        val mCreateDialog = AlertDialog.Builder(activity)
                .setView(mCreateDialogView)
        mCreate = mCreateDialog.create()
        mCreateDialogView.btn_cancel_form_profile.setOnClickListener{
            mCreate.dismiss()
        }
        mCreateDialogView.btn_simpan_form_profile.setOnClickListener{
            val email = mCreateDialogView.email_form_profile.editText?.text.toString()
            updateEmail(email)
        }
    }

    fun updateEmail(email: String){
        loadingDialog.showLoading()
        val apiBuilder = ApiBuilder.buildService(ApiInterface::class.java)
        val topic = apiBuilder.changeProfile(token, email)
        topic.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    try {
                        Toast.makeText(activity, "Berhasil Merubah Email", Toast.LENGTH_SHORT).show()
                        loadData()
                        mCreate.dismiss()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (response.code() == 422) {
                    val dataError = JSONObject(response.errorBody()?.string())
                    if(dataError.getJSONObject("errors").has("email")){
                        val emailError = dataError.getJSONObject("errors").getJSONArray("email")
                        for (i in 0 until emailError.length()) {
                            mCreateDialogView.email_form_profile.error = emailError.getString(i).toString()
                        }
                    }
                } else {
                    Log.e("Resp_ProfileE", "Ada Error di server Code : " + response.code().toString())
                }
                loadingDialog.hideLoading()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Fail_ProfileE", "onFailure: ERROR > " + t.toString());
            }

        })
    }
}