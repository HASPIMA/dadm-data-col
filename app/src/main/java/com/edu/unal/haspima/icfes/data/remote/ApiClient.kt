package com.edu.unal.haspima.icfes.data.remote

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Custom Moshi Adapter to flexibly parse strings, numbers, booleans, or nulls into [String].
 * This avoids JsonDataException when SODA API returns numeric fields as JSON numbers instead of strings.
 */
class FlexibleStringAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): String? {
        return when (reader.peek()) {
            JsonReader.Token.STRING -> reader.nextString()
            JsonReader.Token.NUMBER -> reader.nextString()
            JsonReader.Token.BOOLEAN -> reader.nextBoolean().toString()
            JsonReader.Token.NULL -> reader.nextNull()
            else -> {
                reader.skipValue()
                null
            }
        }
    }

    @ToJson
    fun toJson(value: String?): String? {
        return value
    }
}

/**
 * Singleton object for Retrofit & OkHttp client configuration pointing to https://www.datos.gov.co/
 */
object ApiClient {
    private const val BASE_URL = "https://www.datos.gov.co/"

    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(FlexibleStringAdapter())
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val icfesApiService: IcfesApiService by lazy {
        retrofit.create(IcfesApiService::class.java)
    }

    fun createService(baseUrl: String = BASE_URL, customOkHttpClient: OkHttpClient? = null): IcfesApiService {
        val client = customOkHttpClient ?: okHttpClient
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(IcfesApiService::class.java)
    }
}
