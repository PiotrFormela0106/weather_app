package com.example.info_ui.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import coil.compose.rememberAsyncImagePainter
import com.example.info_ui.AdditionalInfoScreenFragmentArgs
import com.example.info_ui.AdditionalInfoScreenViewModel
import com.example.info_ui.R
import com.example.weather_domain.models.ForecastItem
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.math.RoundingMode

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ContentView(
    viewModel: AdditionalInfoScreenViewModel,
    days: Array<String>,
    activity: FragmentActivity?,
    args: AdditionalInfoScreenFragmentArgs
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    Column(modifier = Modifier.fillMaxSize()) {

        Button(
            onClick = {
                activity?.onBackPressed()
            },
            modifier = Modifier
                .wrapContentSize()
                .padding(top = 8.dp, start = 8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back_button),
                contentDescription = null,
            )
        }

        PagerContent(
            viewModel = viewModel,
            pagerState = pagerState,
            days = days,
            scope = coroutineScope,
            args = args
        )
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun PagerContent(
    pagerState: PagerState,
    days: Array<String>,
    scope: CoroutineScope,
    args: AdditionalInfoScreenFragmentArgs,
    viewModel: AdditionalInfoScreenViewModel
) {
    var isPageScrolled by remember { mutableStateOf(false) }
    val day = args.day
    val item = days.find { it.contains(day) }
    val index = days.indexOf(item)
    Column(modifier = Modifier.fillMaxSize()) {

        HorizontalPager(
            count = days.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> InfoScreen(viewModel, days[0])
                1 -> InfoScreen(viewModel, days[1])
                2 -> InfoScreen(viewModel, days[2])
                3 -> InfoScreen(viewModel, days[3])
                4 -> InfoScreen(viewModel, days[4])
                5 -> InfoScreen(viewModel, days[5])
            }
            scope.launch {
                if (!isPageScrolled) {
                    pagerState.scrollToPage(index)
                }
                isPageScrolled = true
            }
        }
    }
}

@Composable
fun InfoScreen(
    viewModel: AdditionalInfoScreenViewModel,
    day: String
) {
    val forecast by viewModel.forecast.observeAsState()
    val forecastList = forecast?.list?.filter { it -> it.date.contains(day) }.orEmpty()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = day, fontSize = 24.sp)
        ForecastDetailsList(forecastList)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleForecastItem(
    day: ForecastItem
) {
    val temperature =
        "${day.main.temp.toBigDecimal().setScale(0, RoundingMode.HALF_UP).toInt()}\u00B0"
    val icon = "https://openweathermap.org/img/wn/${day.weather[0].icon}@2x.png"
    val description = day.weather[0].description
    val wind = day.wind.speed
    val humidity = day.main.humidity
    val pressure = day.main.pressure
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val hour = day.date.removeRange(0, 11).removeRange(5, 8)
            Text(
                text = hour,
                fontSize = 24.sp,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp)
            )
            Row(
                modifier = Modifier
                    .wrapContentSize()
            ) {
                Text(text = temperature, fontSize = 24.sp, modifier = Modifier.padding(top = 16.dp))
                Image(
                    modifier = Modifier
                        .width(64.dp)
                        .height(64.dp),
                    painter = rememberAsyncImagePainter(model = icon),
                    contentDescription = null,
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = description, fontSize = 16.sp, modifier = Modifier.padding(start = 16.dp))
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ForecastDetailItem(imgId = R.drawable.ic_wind, text = wind.toString())
            }
        }
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ForecastDetailItem(imgId = R.drawable.ic_humidity, text = humidity.toString())
            }
            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ForecastDetailItem(imgId = R.drawable.ic_pressure, text = pressure.toString())
            }
        }
    }
}

@Composable
fun ForecastDetailItem(
    imgId: Int,
    text: String
) {
    Icon(
        painter = painterResource(id = imgId),
        contentDescription = null
    )
    Text(
        text = text,
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxHeight()
    )
}

@Composable
fun ForecastDetailsList(
    days: List<ForecastItem>
) {
    LazyColumn() {
        items(items = days) { day ->
            SingleForecastItem(day)
        }
    }
}
