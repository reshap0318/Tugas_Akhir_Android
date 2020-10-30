package com.minangdev.myta.API

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @FormUrlEncoded
    @POST("/sia/login")
    fun login(
        @Field("login") login: String,
        @Field("password") password: String,
        @Field("device_id") device_id: String = ""
    ): Call<ResponseBody>

    @POST("/user/isLogin")
    fun isLogin(
            @Header("Authorization") token: String
    ): Call<ResponseBody>

    @GET("/user/profile")
    fun profile(
            @Header("Authorization") token: String
    ): Call<ResponseBody>

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
    @POST("/sia/news/{id}/update")
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

}