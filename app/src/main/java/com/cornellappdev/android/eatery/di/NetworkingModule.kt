package com.cornellappdev.android.eatery.di

import android.util.Log
import com.cornellappdev.android.eatery.BuildConfig
import com.cornellappdev.android.eatery.data.AccountTypeAdapter
import com.cornellappdev.android.eatery.data.AuthInterceptor
import com.cornellappdev.android.eatery.data.DateAdapter
import com.cornellappdev.android.eatery.data.DateTimeAdapter
import com.cornellappdev.android.eatery.data.NetworkApi
import com.cornellappdev.android.eatery.data.ReportAdapter
import com.cornellappdev.android.eatery.data.TimestampAdapter
import com.cornellappdev.android.eatery.data.models.PaymentMethod
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.EnumJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    /** Paths whose bodies hold payment credentials, so only the method, URL and status are logged. */
    private val CREDENTIAL_PATHS = setOf("/financials/barcode")

    @Singleton
    @Provides
    fun provideHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val logger = HttpLoggingInterceptor.Logger { message -> Log.d("NetworkRequest", message) }
        val bodyLogging = HttpLoggingInterceptor(logger).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val basicLogging = HttpLoggingInterceptor(logger).apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val logging = Interceptor { chain ->
            if (chain.request().url.encodedPath in CREDENTIAL_PATHS) {
                basicLogging.intercept(chain)
            } else {
                bodyLogging.intercept(chain)
            }
        }

        return OkHttpClient
            .Builder()
            .readTimeout(200, TimeUnit.SECONDS)
            .connectTimeout(200, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
    }

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(DateTimeAdapter())
        .add(TimestampAdapter())
        .add(DateAdapter())
        .add(AccountTypeAdapter())
        .add(KotlinJsonAdapterFactory())
        .add(ReportAdapter())
        .add(
            PaymentMethod::class.java, EnumJsonAdapter.create(PaymentMethod::class.java)
                .withUnknownFallback(PaymentMethod.UNKNOWN)
        )
        .build()

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BACKEND_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Singleton
    @Provides
    fun provideNetworkApi(retrofit: Retrofit): NetworkApi =
        retrofit.create(NetworkApi::class.java)
}
