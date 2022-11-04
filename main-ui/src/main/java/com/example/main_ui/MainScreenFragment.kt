package com.example.main_ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.navigation.fragment.findNavController
import coil.compose.rememberAsyncImagePainter
import com.example.main_ui.theme.AppTheme
import com.example.weather_domain.models.ForecastItem
import com.example.weather_domain.models.ForecastWeather
import com.example.weather_domain.models.LocationMethod
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class MainScreenFragment : Fragment(), LifecycleObserver, DefaultLifecycleObserver {
    private val viewModel by viewModels<MainScreenViewModel>()
    lateinit var forecast: ForecastWeather
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lifecycle.addObserver(viewModel)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        if (viewModel.storageRepository.getLocationMethod() == LocationMethod.Location) {
            checkPermission()
        } else if (viewModel.storageRepository.getLocationMethod() == LocationMethod.City) {
            viewModel.photoVisibility.value = viewModel.storageRepository.getPhotoId().isNotEmpty()
        }

        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        MainScreen(viewModel)
                    }
                }
            }
        }
    }

    @Composable
    fun MainScreen(
        viewModel: MainScreenViewModel
    ) {
        val visibility by viewModel.status.observeAsState()
        val city by viewModel.cityName.observeAsState()
        val date by viewModel.date.observeAsState()
        val image = viewModel.storageRepository.getPhotoId()
        val forecast by viewModel.forecastData.observeAsState()
        val photoVisibility by viewModel.photoVisibility.observeAsState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { goToLocationScreen() }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_location),
                        contentDescription = null
                    )
                }
                if (visibility != MainScreenViewModel.Status.Loading) {
                    Text(
                        text = city.toString(),
                        fontSize = 34.sp,
                        textAlign = TextAlign.Center
                    )
                }
                IconButton(
                    onClick = { openSettingsSheet() }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_settings),
                        contentDescription = null
                    )
                }
            }
            Text(
                text = date.toString(),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            if (photoVisibility == true) {
                Card(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .width(250.dp)
                        .height(250.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = rememberAsyncImagePainter(model = image),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
            }
            if (visibility == MainScreenViewModel.Status.Loading) {
                CircularProgressIndicator()
            } else {
                CurrentWeather(viewModel = viewModel)
                forecast?.let { ForecastList(it.list) }
                DetailedWeather(viewModel.ViewState())
                AirPollution(viewModel.ViewState())
            }
        }
    }

    @Composable
    fun CurrentWeather(
        viewModel: MainScreenViewModel
    ) {
        val temperature by viewModel.roundedTemperature.observeAsState()
        val data by viewModel.weatherData.observeAsState()
        val icon by viewModel.weatherImageUrl.observeAsState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
                .height(128.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Card(
                modifier = Modifier
                    .width(240.dp)
                    .height(128.dp)
                    .padding(top = 8.dp, bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = temperature.toString(),
                    fontSize = 34.sp,
                    textAlign = TextAlign.Center
                )
                data?.weather?.get(0)?.let {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = it.description,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Image(
                modifier = Modifier
                    .width(128.dp)
                    .height(128.dp)
                    .padding(bottom = 8.dp, top = 8.dp),
                painter = rememberAsyncImagePainter(model = icon),
                contentDescription = null,
            )
        }
    }

    @Composable
    fun SingleForecastDay(
        day: ForecastItem,
        onClick: () -> Unit
    ) {
        val sdf = SimpleDateFormat("dd MMM HH:mm")
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        val dateAndTimeFormat2 = sdf.format(Date(day.dateLong * 1000))
        val temperature =
            "${day.main.temp.toBigDecimal().setScale(0, RoundingMode.HALF_UP).toInt()}\u00B0"

        Card(
            modifier = Modifier
                .width(100.dp)
                .height(150.dp)
                .padding(8.dp)
                .clickable {
                    onClick()
                },
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = dateAndTimeFormat2, fontSize = 20.sp, textAlign = TextAlign.Center)
                Text(text = temperature, fontSize = 20.sp, textAlign = TextAlign.Center)
                Image(
                    modifier = Modifier
                        .width(70.dp)
                        .height(70.dp),
                    painter = rememberAsyncImagePainter("https://openweathermap.org/img/wn/${day.weather[0].icon}@2x.png"),
                    contentDescription = null
                )
            }
        }
    }

    @Composable
    fun ForecastList(
        days: List<ForecastItem>
    ) {
        LazyRow() {
            items(items = days) { day ->
                SingleForecastDay(
                    day = day,
                    onClick = {
                        viewModel.forecastData.observe(viewLifecycleOwner) { data ->
                            val sdf = SimpleDateFormat("dd MMM HH:mm")
                            sdf.timeZone = TimeZone.getTimeZone("GMT")
                            val dateAndTimeFormat = sdf.format(Date(day.dateLong * 1000))
                            val forecastWithUniqueDays = data.list.distinctBy {
                                try {
                                    it.date.removeRange(10, 19).removeRange(0, 5)
                                } catch (e: IndexOutOfBoundsException) {
                                    Log.e("error", e.toString())
                                }
                            }
                            val listOfDays: MutableList<String> = mutableListOf()
                            for (day in forecastWithUniqueDays) {
                                try {
                                    listOfDays.add(day.date.removeRange(10, 19).removeRange(0, 5))
                                } catch (e: IndexOutOfBoundsException) {
                                    Log.e("error", e.toString())
                                }
                            }
                            val arrayList: Array<String> = listOfDays.toTypedArray()

                            findNavController().navigate(
                                MainScreenFragmentDirections.navigateToViewPager(
                                    dateAndTimeFormat,
                                    arrayList
                                )
                            )
                        }
                    }
                )
            }
        }
    }

    @Composable
    fun DetailedWeather(viewState: MainScreenViewModel.ViewState) {

        val sunrise by viewState.sunrise.observeAsState()
        val sunset by viewState.sunset.observeAsState()
        val wind by viewState.wind.observeAsState()
        val humidity by viewState.humidity.observeAsState()
        val pressure by viewState.pressure.observeAsState()

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
                    .wrapContentHeight()
                    .padding(bottom = 16.dp, top = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround

            ) {
                WeatherItem(image = R.drawable.ic_sunrise, text = sunrise.toString())
                WeatherItem(image = R.drawable.ic_sunset, text = sunset.toString())
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                WeatherItem(image = R.drawable.ic_wind, text = wind.toString())
                WeatherItem(image = R.drawable.ic_humidity, text = humidity.toString())
                WeatherItem(image = R.drawable.ic_pressure, text = pressure.toString())
            }
        }
    }

    @Composable
    fun WeatherItem(
        image: Int,
        text: String
    ) {
        Row() {
            Icon(painter = painterResource(id = image), contentDescription = null)
            Text(text = text, fontSize = 20.sp)
        }
    }

    @Composable
    fun AirPollution(
        viewState: MainScreenViewModel.ViewState
    ) {
        val aqi by viewState.aqi.observeAsState()
        val co by viewState.co.observeAsState()
        val no by viewState.no.observeAsState()
        val no2 by viewState.no2.observeAsState()
        val o3 by viewState.o3.observeAsState()

        val so2 by viewState.so2.observeAsState()
        val pm2_5 by viewState.pm2_5.observeAsState()
        val pm10 by viewState.pm10.observeAsState()
        val nh3 by viewState.nh3.observeAsState()

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = aqi.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(text = stringResource(id = R.string.air_quality_index), fontSize = 16.sp)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    AirPollutionItem(value = co.toString(), text = stringResource(id = R.string.co))
                    AirPollutionItem(value = no.toString(), text = stringResource(id = R.string.no))
                    AirPollutionItem(value = no2.toString(), text = stringResource(id = R.string.no2))
                    AirPollutionItem(value = o3.toString(), text = stringResource(id = R.string.o3))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    AirPollutionItem(
                        value = so2.toString(),
                        text = stringResource(id = R.string.so2)
                    )
                    AirPollutionItem(
                        value = pm2_5.toString(),
                        text = stringResource(id = R.string.pm2_5)
                    )
                    AirPollutionItem(
                        value = pm10.toString(),
                        text = stringResource(id = R.string.pm10)
                    )
                    AirPollutionItem(
                        value = nh3.toString(),
                        text = stringResource(id = R.string.nh3)
                    )
                }
            }
        }
    }

    @Composable
    fun AirPollutionItem(
        value: String,
        text: String
    ) {
        Column() {
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = text, textAlign = TextAlign.Center, fontSize = 12.sp)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            // Checking whether user granted the permission or not.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    checkGPS()
                }
            } else {
                alert(
                    "Allow permission for location to get weather in your region or select city",
                    "Location permission denied"
                )
            }
        }
    }

    private fun alert(message: String, title: String) {
        AlertDialog.Builder(requireContext()).apply {
            setMessage(message)
            setTitle(title)
            setPositiveButton(
                "ok"
            ) { _, _ ->
                findNavController().navigate(MainScreenFragmentDirections.navigateToCities())
            }
        }
            .create()
            .show()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            try {
                val location: Location = task.result
                val geocoder = Geocoder(requireActivity(), Locale.getDefault())
                val addresses: List<Address> = geocoder.getFromLocation(
                    location.latitude, location.longitude, 1
                )
                viewModel.storageRepository.saveCoordinates(
                    addresses[0].latitude,
                    addresses[0].longitude
                )
                viewModel.fetchData()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun checkGPS() {
        val manager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alert("You need to have Location Services enabled!", "Location GPS is disabled")
        } else {
            getLocation()
        }
    }

    @Suppress("DEPRECATION")
    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) { // When permission denied at start
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        } else {
            checkGPS()
        }
    }

    private fun handleEvent(event: MainScreenViewModel.Event) {
        when (event) {
            is MainScreenViewModel.Event.OnCitiesClick -> {
                goToLocationScreen()
            }
            is MainScreenViewModel.Event.OnSettingsClick -> {
                openSettingsSheet()
            }
        }
    }

    private fun openSettingsSheet() {
        if (findNavController().currentDestination?.id == com.example.base.R.id.main_screen_id)
            findNavController().navigate(MainScreenFragmentDirections.navigateToSettings())
    }

    private fun goToLocationScreen() {
        if (findNavController().currentDestination?.id == com.example.base.R.id.main_screen_id)
            findNavController().navigate(MainScreenFragmentDirections.navigateToCities())
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 44
    }
}
