package com.cornellappdev.android.eatery.util

import com.cornellappdev.android.eatery.data.models.Eatery
import kotlin.random.Random

object PreviewData {

    fun mockEatery(id:Int = Random.nextInt()) =
        Eatery(
            id = id,
            name = "Test Eatery",
            location = "Test Location",
        )

}
