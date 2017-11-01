package api

import api.Constants.DATE_FORMAT
import com.google.gson.GsonBuilder
import models.History
import models.Transaction
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface PrivateService {
    @FormUrlEncoded
    @POST("tradingApi.php")
    fun info(): Call<String>

    @FormUrlEncoded
    @POST("tradingApi.php")
    fun history(@FieldMap(encoded = true) fields: Map<String, String>): Call<List<History>>

    @FormUrlEncoded
    @POST("tradingApi.php")
    fun transactions(@FieldMap(encoded = true) fields: Map<String, String>): Call<List<Transaction>>
}

class PrivateApi private constructor(publicKey: String, privateKey: String){

    companion object : SingletonHolder<PrivateApi, String, String>(::PrivateApi)

    val service: Lazy<PrivateService> = lazy {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(HeaderInterceptor(publicKey,privateKey))
                .addNetworkInterceptor(logInterceptor)
                .build()

        val gson = GsonBuilder().setDateFormat(DATE_FORMAT).create()
        val retrofit = Retrofit.Builder()
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl("https://bitbay.net/API/Trading/")
                .build()
        retrofit.create(PrivateService::class.java)
    }



}