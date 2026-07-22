package com.mdi2.weatherapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class RetryInterceptor(
    private val maxRetries: Int = 3
) : Interceptor{
    override fun intercept( chain: Interceptor.Chain): Response {
        var lastException: IOException? = null
        repeat(maxRetries){ attempt ->
            try {
                return chain.proceed(chain.request())
            } catch (e: IOException){
                lastException = e
            }
        }
        throw lastException ?: IOException("Request failed after $maxRetries retries")
    }
}

object RetrofitClient {
    private lateinit var appContext: Context
    private lateinit var cache: Cache
    fun init(context: Context){
        appContext = context.applicationContext

    }

    fun clearCache(){
        if(::cache.isInitialized){
            cache.evictAll()
        }
    }


    val weatherApiService: WeatherApiService by lazy {
        val cacheDir = File(appContext.cacheDir, "http_cache")
//        val cache = Cache(cacheDir, 5L * 1024 * 1024) // previous # 2
        cache = Cache(cacheDir, 5L * 1024 * 1024)
        // 5MB max disk space for cached responses
        // L is for after 5 will be long type which is needed for maxSize is long, not Int
        val okHttpClient = OkHttpClient.Builder()
            .cache(cache)
            .addNetworkInterceptor { chain ->
                    chain.proceed(chain.request())
                         .newBuilder()
                         .header("Cache-Control", "public, max-age=300")
                         .build()
            }.addInterceptor(RetryInterceptor(maxRetries=3))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        Retrofit.Builder()
            .baseUrl(AppConstants.WEATHER_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
    val feedbackApiService: FeedbackApiService by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(RetryInterceptor(maxRetries=3))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        Retrofit.Builder()
            .baseUrl(AppConstants.FEEDBACK_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FeedbackApiService::class.java)
    }
}

//  PREVIOUS IMPLEMENTATION
//object RetrofitClient {
//    val weatherApiService: WeatherApiService by lazy{
//        Retrofit.Builder()
//            .baseUrl(AppConstants.WEATHER_BASE_URL) //prepends this to every @GET
//            .addConverterFactory(GsonConverterFactory.create()) //JSON-to-data-class conversion
//            .build() // API returns JSON, Gson maps it to weatherResponse fields
//            .create(WeatherApiService::class.java) // generates the HTTP implementation
//    }
//
//    val feedbackApiService: FeedbackApiService by lazy{
//        Retrofit.Builder()
//            .baseUrl(AppConstants.FEEDBACK_BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(FeedbackApiService::class.java)
//    }
//}