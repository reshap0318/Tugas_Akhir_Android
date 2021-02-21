package com.minangdev.m_mahasiswa.API

import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    @GET("/user/news")
    fun announcements(
            @Header("Authorization") token: String
    ): Call<ResponseBody>

    @GET("/user/news/{id}")
    fun announcement(
            @Header("Authorization") token: String,
            @Path("id") id: String
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

    @GET("/mahasiswa/kelas/{kelasId}")
    fun kelasDetail(
            @Header("Authorization") token: String,
            @Path("kelasId") kelasId: String
    ) : Call<ResponseBody>

    @GET("/mahasiswa/krs/isCanEntry")
    fun isCanEntry(
            @Header("Authorization") token: String
    ) : Call<ResponseBody>

    @FormUrlEncoded
    @POST("/mahasiswa/krs/entry")
    fun entry(
            @Header("Authorization") token: String,
            @Field("klsId") klsId: String,
    ): Call<ResponseBody>

    @DELETE("/mahasiswa/krs/delete/{krsdtId}")
    fun krsDelete(
            @Header("Authorization") token: String,
            @Path("krsdtId") krsdtId: String
    ) : Call<ResponseBody>

    @GET("/mahasiswa/bimbingan")
    fun bimbinganList(
            @Header("Authorization") token: String
    ) : Call<ResponseBody>

    @Multipart
    @POST("/mahasiswa/bimbingan/send")
    fun bimbinganSend(
            @Header("Authorization") token: String,
            @Part("receiverId") receiverId: RequestBody,
            @Part("message") message: RequestBody,
            @Part("topicPeriodId") topicPeriodId: RequestBody,
            @Part img: MultipartBody.Part? = null,
    ) : Call<ResponseBody>

    @DELETE("/mahasiswa/bimbingan/{bimbinganId}/delete")
    fun bimbinganDelete(
            @Header("Authorization") token: String,
            @Path("bimbinganId") bimbinganId: String
    ) : Call<ResponseBody>

    @GET("/mahasiswa/bimbingan/group-chat")
    fun bimbinganListGroup(
        @Header("Authorization") token: String
    ) : Call<ResponseBody>

    @Multipart
    @POST("/mahasiswa/bimbingan/send-group-chat")
    fun bimbinganSendGroup(
            @Header("Authorization") token: String,
            @Part("receiverId") receiverId: RequestBody,
            @Part("message") message: RequestBody,
            @Part("topicPeriodId") topicPeriodId: RequestBody,
            @Part("groupchanel") groupchanel: RequestBody,
            @Part img: MultipartBody.Part? = null,
    ) : Call<ResponseBody>

    @FormUrlEncoded
    @POST("/mahasiswa/bimbingan/create")
    fun bimbinganCreate(
            @Header("Authorization") token: String,
            @Field("topicPeriodId") topicPeriodId: String,
    ): Call<ResponseBody>

    @GET("/user/topic/active")
    fun topicActive(
            @Header("Authorization") token: String
    ) : Call<ResponseBody>

}