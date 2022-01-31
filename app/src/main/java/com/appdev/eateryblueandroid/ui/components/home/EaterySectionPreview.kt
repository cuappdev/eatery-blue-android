package com.appdev.eateryblueandroid.ui.components.home

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.models.EaterySection
import com.appdev.eateryblueandroid.ui.components.EateryCard
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle

@Composable
fun EaterySectionPreview(eateries: List<Eatery>, section: EaterySection) {
    val filteredEateries = eateries.filter {section.filter(it)}
    val sectionItems: List<SectionPreviewItem> =
        listOf(
            filteredEateries.subList(0, 3).map { SectionPreviewItem.EateryItem(it) },
            if (filteredEateries.size > 3) listOf(SectionPreviewItem.MoreEateriesBox) else listOf()
        ).flatten()
    Column {
        Row(
            modifier = Modifier.padding(start = 13.dp, top = 5.dp)
        ) {
            Text(
                text = section.name,
                textStyle = TextStyle.HEADER_H3
            )
        }
        LazyRow(contentPadding = PaddingValues(6.dp, 15.dp)) {
            items(sectionItems) { item ->
                when(item) {
                    is SectionPreviewItem.EateryItem ->
                        Column(modifier = Modifier.fillParentMaxWidth(0.85f).padding(7.dp, 0.dp)) {
                            EateryCard(eatery = item.eatery, selectEatery = {})
                        }
                    is SectionPreviewItem.MoreEateriesBox ->
                        Text(text = "hello")
                }
            }
        }
    }
}

sealed class SectionPreviewItem {
    object MoreEateriesBox: SectionPreviewItem()
    data class EateryItem(val eatery: Eatery) : SectionPreviewItem()
}