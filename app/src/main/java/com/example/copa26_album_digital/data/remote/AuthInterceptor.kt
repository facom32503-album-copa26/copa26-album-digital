package com.example.copa26_album_digital.data.remote

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor OkHttp que adiciona o token de autenticação da football-data.org
 * em todas as requisições, via header `X-Auth-Token`.
 *
 * O token nunca é escrito no código-fonte: ele é lido de `BuildConfig` (injetado
 * a partir de `local.properties`, fora do controle de versão).
 */
class AuthInterceptor(private val apiToken: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader(HEADER_AUTH_TOKEN, apiToken)
            .build()
        return chain.proceed(request)
    }

    private companion object {
        const val HEADER_AUTH_TOKEN = "X-Auth-Token"
    }
}
