package com.minangdev.m_dosen.API

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @FormUrlEncoded
    @POST("/dosen/login")
    fun login(
        @Field("login") login: String,
        @Field("password") password: String,
        @Field("device_id") device_id: String? = null
    ): Call<ResponseBody>

    @POST("/user/isLogin")
    fun isLogin(
            @Header("Authorization") token: String
    ): Call<ResponseBody>

    @GET("/user/profile")
    fun profile(
            @Header("Authorization") token: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @PATCH("/user/change-profile")
    fun changeProfile(
        @Header("Authorization") token: String,
        @Field("name") name: String,
        @Field("email") email: String
    ): Call<ResponseBody>

    @Multipart
    @POST("/user/change-avatar")
    fun changeAvatar(
        @Header("Authorization") token: String,
        @Part avatar: MultipartBody.Part,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @PATCH("/user/change-password")
    fun changePassword(
        @Header("Authorization") token: String,
        @Field("old_password") old_password: String,
        @Field("new_password") new_password: String,
        @Field("confirm_password") confirm_password: String
    ): Call<ResponseBody>

    @POST("/user/logout")
    fun logout(
        @Header("Authorization") token: String
    ): Call<ResponseBody>

    @GET("/user/semester-active")
    fun semesterActive(
            @Header("Authorization") token: String
    ) : Call<ResponseBody>

    @GET("/dosen/bimbingan")
    fun mahasiswaBimbingan(
            @Header("Authorization") token: String
    ) : Call<ResponseBody>

    @GET("/dosen/mahasiswa/{nim}")
    fun mahasiswaBimbinganDetail(
            @Header("Authorization") token: String,
            @Path("nim") nim: String
    ) : Call<ResponseBody>

    @GET("/dosen/mahasiswa/{nim}/krs")
    fun mahasiswaBimbinganKrs(
            @Header("Authorization") token: String,
            @Path("nim") nim: String
    ) : Call<ResponseBody>

    @GET("/dosen/mahasiswa/{nim}/krs/{semester}")
    fun mahasiswaBimbinganKrsSemester(
            @Header("Authorization") token: String,
            @Path("nim") nim: String,
            @Path("semester") semester: String
    ) : Call<ResponseBody>

    @GET("/dosen/mahasiswa/{nim}/transkrip")
    fun mahasiswaBimbinganTranskrip(
            @Header("Authorization") token: String,
            @Path("nim") nim: String
    ) : Call<ResponseBody>

    @GET("/dosen/mahasiswa/{nim}/transkrip/staticA")
    fun mahasiswaBimbinganStaticA(
            @Header("Authorization") token: String,
            @Path("nim") nim: String
    ) : Call<ResponseBody>

    @GET("/dosen/mahasiswa/{nim}/transkrip/staticB")
    fun mahasiswaBimbinganStaticB(
            @Header("Authorization") token: String,
            @Path("nim") nim: String
    ) : Call<ResponseBody>

    @GET("/dosen/mahasiswa/{nim}/sks/sum")
    fun mahasiswaBimbinganSksSum(
            @Header("Authorization") token: String,
            @Path("nim") nim: String
    ) : Call<ResponseBody>

}