package com.minangdev.myta.API

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @FormUrlEncoded
    @POST("/sia/login")
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

    @GET("/sia/news")
    fun announcements(
            @Header("Authorization") token: String
    ): Call<ResponseBody>

    @GET("/sia/news/{id}")
    fun announcement(
            @Header("Authorization") token: String,
            @Path("id") id: String
    ): Call<ResponseBody>

    @GET("/sia/news/{id}/edit")
    fun announcementEdit(
            @Header("Authorization") token: String,
            @Path("id") id: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/sia/news/save")
    fun announcementSave(
            @Header("Authorization") token: String,
            @Field("title") title: String,
            @Field("description") description: String,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @PATCH("/sia/news/{id}/update")
    fun announcementUpdate(
            @Header("Authorization") token: String,
            @Path("id") id: String,
            @Field("title") title: String,
            @Field("description") description: String,
    ): Call<ResponseBody>

    @DELETE("/sia/news/{id}/delete")
    fun announcementDelete(
            @Header("Authorization") token: String,
            @Path("id") id: String,
    ): Call<ResponseBody>

    //topic
    @GET("/sia/topic")
    fun topics(
            @Header("Authorization") token: String
    ): Call<ResponseBody>

    @GET("/sia/topic/active")
    fun topicActiv(
            @Header("Authorization") token: String
    ): Call<ResponseBody>

    @GET("/sia/topic/deactive")
    fun topicDeactiv(
            @Header("Authorization") token: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/sia/topic/save")
    fun topicSave(
            @Header("Authorization") token: String,
            @Field("name") name: String,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @PATCH("/sia/topic/{id}/update")
    fun topicUpdate(
            @Header("Authorization") token: String,
            @Path("id") id: String,
            @Field("name") name: String,
    ): Call<ResponseBody>

    @DELETE("/sia/topic/{id}/delete")
    fun topicDelete(
            @Header("Authorization") token: String,
            @Path("id") id: String,
    ): Call<ResponseBody>

    @GET("/sia/period")
    fun periods(
            @Header("Authorization") token: String
    ): Call<ResponseBody>

    @POST("/sia/period/syn")
    fun periodSyn(
            @Header("Authorization") token: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/sia/period/{periodId}/add-topic")
    fun periodAddTopic(
            @Header("Authorization") token: String,
            @Path("periodId") periodId: String,
            @Field("topics[]") topics: ArrayList<String>
    ): Call<ResponseBody>

    @DELETE("/sia/period/{periodId}/delete-topic/{topicId}")
    fun periodDeleteTopic(
            @Header("Authorization") token: String,
            @Path("periodId") periodId: String,
            @Path("topicId") topic: String,
    ): Call<ResponseBody>

}