package com.example.sportsbooking.PresentationLayer.Component

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi


@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageSlider(imageList:List<Uri>){
    val pagerState = rememberPagerState(pageCount = {
        imageList.size
    })

    Box(modifier = Modifier
        .padding(8.dp)
        .clip(RoundedCornerShape(10.dp))
        .fillMaxWidth()
        .height(250.dp)){
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            // Our page content


            Card( elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .height(350.dp)
                    .fillMaxWidth()
                    // .clip(RoundedCornerShape(10.dp))
                    .graphicsLayer {
                        val pageOffset = (
                                (pagerState.currentPage - page) +
                                        pagerState.currentPageOffsetFraction
                                )
                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
            ){
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageList[page])
                        .build(),
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterHorizontally)
                        .wrapContentHeight(),
                    contentDescription = ""
                )


            }
        }

    }
    Row(
        Modifier
            .wrapContentHeight()
            .fillMaxWidth()

            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pagerState.pageCount) { iteration ->
            val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(10.dp)
            )
        }
    }

}