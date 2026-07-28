package com.example.copa26_album_digital.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Fábrica do cliente de rede. Centraliza a configuração de OkHttp + Retrofit +
 * Moshi e expõe uma instância pronta de [FootballApi].
 *
 * Em projetos com injeção de dependência (Hilt/Koin) esta lógica viraria um
 * módulo; aqui é uma fábrica simples para manter o exemplo autocontido.
 */
object NetworkModule {

    private const val BASE_URL = "https://api.football-data.org/"

    fun provideFootballApi(apiToken: String, enableLogging: Boolean = true): FootballApi {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val logging = HttpLoggingInterceptor().apply {
            level = if (enableLogging) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(apiToken))
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(FootballApi::class.java)
    }
}
