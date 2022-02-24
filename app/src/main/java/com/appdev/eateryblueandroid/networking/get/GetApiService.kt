package com.appdev.eateryblueandroid.networking.get

import com.appdev.eateryblueandroid.models.*
import com.appdev.eateryblueandroid.util.Constants
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*

object GetApiService {
    var api: GetApi? = null
    fun getInstance(): GetApi {
        if (api == null) {
            val moshi = Moshi.Builder()
                .add(DateTimeAdapter())
                .add(TransactionTypeAdapter())
                .add(AccountTypeAdapter())
                .add(KotlinJsonAdapterFactory())
                .build()

            api = Retrofit.Builder()
                .baseUrl(Constants.GET_BACKEND_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build().create(GetApi::class.java)
        }
        return api!!
    }

    fun generateUserBody(sessionId: String): GetApiRequestBody<GetApiUserParams> {
        return GetApiRequestBody(
            version = "1",
            method = "retrieve",
            params = GetApiUserParams(
                sessionId = sessionId
            )
        )
    }

    fun generateAccountsBody(sessionId: String, userId: String): GetApiRequestBody<GetApiAccountsParams> {
        return GetApiRequestBody(
            version = "1",
            method = "retrieveAccountsByUser",
            params = GetApiAccountsParams(
                sessionId = sessionId,
                userId = userId
            )
        )
    }

    fun generateTransactionHistoryBody(
        sessionId: String, userId: String, endDate: Date
    ): GetApiRequestBody<GetApiTransactionHistoryParams> {
        val startDate = Date.from(endDate.toInstant().minus(Duration.ofDays(5000)))
        return GetApiRequestBody(
            version = "1",
            method="retrieveTransactionHistory",
            params = GetApiTransactionHistoryParams(
                paymentSystemType = 0,
                sessionId = sessionId,
                queryCriteria = GetApiTransactionHistoryQueryCriteria(
                    endDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(endDate),
                    startDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(startDate),
                    maxReturn = 10000,
                    institutionId = Constants.CORNELL_INSTITUTION_ID,
                    userId = userId
                )
            )
        )
    }
}