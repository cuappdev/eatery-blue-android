package com.appdev.eateryblueandroid.networking.get

import com.appdev.eateryblueandroid.models.*
import retrofit2.http.Body
import retrofit2.http.POST

interface GetApi {
    @POST("user")
    suspend fun fetchUser(
        @Body body: GetApiRequestBody<GetApiUserParams>
    ): GetApiResponse<User>

    @POST("commerce")
    suspend fun fetchAccounts(
        @Body body: GetApiRequestBody<GetApiAccountsParams>
    ): GetApiResponse<AccountsResponse>

    @POST("commerce")
    suspend fun fetchTransactionHistory(
        @Body body: GetApiRequestBody<GetApiTransactionHistoryParams>
    ): GetApiResponse<TransactionsResponse>
}