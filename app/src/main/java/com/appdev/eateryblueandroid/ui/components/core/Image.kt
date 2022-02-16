package com.appdev.eateryblueandroid.ui.components.core

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.appdev.eateryblueandroid.R
import com.skydoves.landscapist.ShimmerParams
import com.skydoves.landscapist.glide.GlideImage
@Composable
fun Image(url: String, modifier: Modifier = Modifier) {
    GlideImage(
        imageModel = url,
        contentScale = ContentScale.FillWidth,
        modifier = modifier,
        shimmerParams = ShimmerParams(
            baseColor = colorResource(id = R.color.white),
            highlightColor = colorResource(id = R.color.gray00),
            durationMillis = 350,
            dropOff = 0.65f,
            tilt = 20f
        ),
        failure = {
            androidx.compose.foundation.Image(
                painter = painterResource(R.drawable.blank_eatery),
                contentDescription = "Eatery Image",
                contentScale = ContentScale.FillWidth,
                modifier = modifier
            )
        }
    )
}

