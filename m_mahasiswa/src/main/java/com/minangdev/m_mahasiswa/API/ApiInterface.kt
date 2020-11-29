package com.minangdev.m_mahasiswa.API

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @FormUrlEncoded
    @POST("/mahasiswa/login")
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

    @GET("/mahasiswa/sks/sum")
    fun sksSum(
        @Header("Authorization") token: String
    ) : Call<ResponseBody>

    @GET("/mahasiswa/list-semester")
    fun semester(
            @Header("Authorization") token: String
    ) : Call<ResponseBody>

    @GET("/mahasiswa/krs")
    fun krs(
            @Header("Authorization") token: String
    ) : Call<ResponseBody>

    @GET("/mahasiswa/krs/{semester}")
    fun krsSemester(
            @Header("Authorization") token: String,
            @Path("semester") semester: String
    ) : Call<ResponseBody>

    @GET("/mahasiswa/transkrip")
    fun transkrip(
            @Header("Authorization") token: String
    ) : Call<ResponseBody>

    @GET("/mahasiswa/transkrip/staticA")
    fun staticA(
            @Header("Authorization") token: String
    ) : Call<ResponseBody>

    @GET("/mahasiswa/transkrip/staticB")
    fun staticB(
            @Header("Authorization") token: String
    ) : Call<ResponseBody>

    @GET("/mahasiswa/kelas")
    fun kelas(
            @Header("Authorization") token: String
    ) : Call<ResponseBody>

    @GET("/mahasiswa/krs/{kelasId}")
    fun kelasDetail(
            @Header("Authorization") token: String,
            @Path("kelasId") kelasId: String
    ) : Call<ResponseBody>

}