package com.cornellappdev.android.eateryblue.data.repositories

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import com.cornellappdev.android.eateryblue.data.models.ApiResponse
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryApiResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Handles background image URL loading.
 */
object CoilRepository {
    private val urlMap: MutableMap<String, MutableState<EateryApiResponse<ImageBitmap>>> =
        mutableMapOf()

    /**
     * Returns a [MutableState] containing an [ApiResponse] corresponding to a loading or loaded
     * image bitmap for loading the input [imageUrl]. If the image previously resulted in an error,
     * calling this function will attempt to re-load.
     *
     * Loads images with Coil.
     */
    fun getUrlState(
        imageUrl: String,
        context: Context
    ): MutableState<EateryApiResponse<ImageBitmap>> {
        val imageLoader = context.imageLoader

        // Make new request.
        if (!urlMap.containsKey(imageUrl) || urlMap[imageUrl]!!.value is EateryApiResponse.Error) {
            val imageRequest = ImageRequest.Builder(context)
                .data(imageUrl)
                .build()

            urlMap[imageUrl] = mutableStateOf(EateryApiResponse.Pending)

            val disposable = imageLoader.enqueue(imageRequest)

            CoroutineScope(Dispatchers.IO).launch {
                val result = disposable.job.await()
                if (result.drawable == null) {
                    urlMap[imageUrl]!!.value = EateryApiResponse.Error
                } else {
                    urlMap[imageUrl]!!.value =
                        EateryApiResponse.Success(result.drawable!!.toBitmap().asImageBitmap())
                }
            }
        }

        return urlMap[imageUrl]!!
    }
}
