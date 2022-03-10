package com.appdev.eateryblueandroid.networking

import com.appdev.eateryblueandroid.util.Constants
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.Retrofit

object ApiService {
    var api: Api? = null
    fun getInstance() : Api {
        if (api == null) {
            val moshi = Moshi.Builder()
                .add(TimestampAdapter())
                .add(DateAdapter())
                .add(KotlinJsonAdapterFactory())
                .build()

            api = Retrofit.Builder()
                .baseUrl(Constants.BACKEND_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build().create(Api::class.java)
        }
        return api!!
    }
}
