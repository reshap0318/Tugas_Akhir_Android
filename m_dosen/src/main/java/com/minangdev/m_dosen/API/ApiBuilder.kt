package com.minangdev.m_dosen.API

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiBuilder {
    // #kampus : http://192.168.0.181:8000 #home : http://192.168.100.81:8000 #heroku : https://akhir-tugas.herokuapp.com/
    private const val BASE_URL = "http://10.44.7.31:8000"

    private val okHttp = OkHttpClient.Builder().apply {
        readTimeout(60, TimeUnit.SECONDS)
        writeTimeout(60, TimeUnit.SECONDS)
        connectTimeout(60, TimeUnit.SECONDS)
    }

    private val builder = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttp.build())

    private val retrofit = builder.build()

    fun <T> buildService(serviceType: Class<T>): T{
        return retrofit.create(serviceType)
    }
}