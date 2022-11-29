package com.appdev.eateryblueandroid.ui.components.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.models.EaterySection
import com.appdev.eateryblueandroid.ui.components.EateryCard
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EaterySectionPreview(
    eateries: List<Eatery>,
    section: EaterySection,
    selectSection: (eaterySection: EaterySection) -> Unit,
    selectEatery: (eatery: Eatery) -> Unit = {}
) {
    val filteredEateries = eateries.filter { section.filter(it) }
    var height by remember { mutableStateOf(0) }
    val sectionItems: List<SectionPreviewItem> =
        listOf(
            filteredEateries.subList(0, Math.min(filteredEateries.size, 3)).map { SectionPreviewItem.EateryItem(it) },
            if (filteredEateries.size > 3) listOf(SectionPreviewItem.MoreEateriesBox) else listOf()
        ).flatten()
    LazyRow(contentPadding = PaddingValues(9.dp, 0.dp)) {
        items(sectionItems, key = { it.hashCode() }) { item ->
            when (item) {
                is SectionPreviewItem.EateryItem ->
                    Column(
                        modifier = Modifier
                            .fillParentMaxWidth(0.86f)
                            .padding(7.dp, 0.dp)
                            .onSizeChanged {
                                height = it.height
                            }
                            .animateItemPlacement()
                    ) {
                        EateryCard(eatery = item.eatery, selectEatery = selectEatery)
                    }
                is SectionPreviewItem.MoreEateriesBox ->
                    Surface(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .padding(7.dp, 0.dp)
                            .height(with(LocalDensity.current) { height.toDp() })
                            .aspectRatio(1f)
                            .clickable { selectSection(section) }
                            .animateItemPlacement()
                    ) {
                        Column(
                            modifier = Modifier.background(colorResource(id = R.color.white)),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_rightarrow_circ),
                                contentDescription = null,
                                tint = colorResource(id = R.color.eateryBlue)
                            )
                            Text(
                                text = "More eateries",
                                textStyle = TextStyle.HEADER_H4,
                                color = colorResource(R.color.eateryBlue),
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                    }
            }
        }
    }
}

sealed class SectionPreviewItem {
    object MoreEateriesBox : SectionPreviewItem()
    data class EateryItem(val eatery: Eatery) : SectionPreviewItem()
}