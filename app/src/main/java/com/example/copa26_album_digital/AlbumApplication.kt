package com.example.copa26_album_digital

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient

/**
 * Application do app. Configura o [ImageLoader] global do Coil com:
 *  - suporte a SVG (os escudos da football-data.org vêm em .svg);
 *  - um interceptor OkHttp que injeta a chave privada `x-api-key` APENAS nas
 *    requisições ao nosso host de fotos ([BuildConfig.PHOTO_API_BASE_URL]),
 *    garantindo que só o app autorizado consuma a API privada de imagens.
 */
class AlbumApplication : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .components { add(SvgDecoder.Factory()) }
            .okHttpClient { buildPhotoApiClient() }
            .crossfade(true)
            .build()

    private fun buildPhotoApiClient(): OkHttpClient {
        val photoApiHost = BuildConfig.PHOTO_API_BASE_URL.toHttpUrlOrNull()?.host
        val apiKeyInterceptor = Interceptor { chain ->
            val request = chain.request()
            val requestForOurApi = photoApiHost != null && request.url.host == photoApiHost
            val finalRequest = if (requestForOurApi) {
                request.newBuilder()
                    .header("x-api-key", BuildConfig.PHOTO_API_KEY)
                    .build()
            } else {
                request
            }
            chain.proceed(finalRequest)
        }
        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .build()
    }
}
