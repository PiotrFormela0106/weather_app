package com.example.weatherapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.AppWidgetTarget
import com.example.base.Result
import com.example.main_ui.mappers.toStringFormat
import com.example.main_ui.mappers.toSymbol
import com.example.storage_data.repo.StorageRepositoryImpl
import com.example.weather_data.api.RetrofitClient
import com.example.weather_data.repo.WeatherRepositoryImpl
import com.example.weather_domain.models.CurrentWeather
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*


class WeatherWidget : AppWidgetProvider() {
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)

        val retrofitClient = RetrofitClient()
        lateinit var weather: CurrentWeather
        val storage = StorageRepositoryImpl(context = context)
        val weatherRepository = WeatherRepositoryImpl(retrofitClient, storage)

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val currentWeather = flowOf(weatherRepository.getCurrentWeather())
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(
                    ComponentName(
                        context,
                        WeatherWidget::class.java
                    )
                )
                withContext(Dispatchers.Main) {
                    currentWeather.collect { result ->
                        when (result) {
                            is Result.OnSuccess -> {

                                result.data.let { weather = it }
                                val sdf = SimpleDateFormat("EEEE, d MMMM HH:mm", Locale(storage.getLanguage().toStringFormat()))
                                val day = sdf.format(Date())
                                val temp = weather.main.temp.toBigDecimal().setScale(0, RoundingMode.HALF_UP).toInt()
                                    .toString() + storage.getUnits().toSymbol()
                                val city = weather.cityName
                                val description = weather.weather[0].description
                                val icon = weather.weather[0].icon
                                val url = "https://openweathermap.org/img/wn/${icon}@2x.png"
                                for (appWidgetId in appWidgetIds) {
                                    updateAppWidget(
                                        context = context,
                                        appWidgetManager = appWidgetManager,
                                        appWidgetId = appWidgetId,
                                        day = day,
                                        temperature = temp,
                                        city = city,
                                        description = description,
                                        icon = url
                                    )
                                }
                            }
                            is Result.OnError -> {
                                Log.i("download", "failure")
                            }
                        }
                    }
                }
            }
        }
    }
    override fun onDisabled(context: Context?) {
        super.onDisabled(context)

        job.cancel()
    }

}

@RequiresApi(Build.VERSION_CODES.M)
internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    day: String?,
    temperature: String?,
    city: String?,
    description: String?,
    icon: String?
) {
    val views = RemoteViews(context.packageName, R.layout.weather_widget)

    if (day != null) {
        views.setTextViewText(R.id.widget_day, day)
    }
    if (temperature != null) {
        views.setTextViewText(R.id.widget_temp, temperature)
    }
    if (city != null) {
        views.setTextViewText(R.id.widget_city, city)
    }
    if (description != null) {
        views.setTextViewText(R.id.widget_description, description)
    }
    if (icon != null) {
        val widgetIcon = AppWidgetTarget(context, R.id.widget_icon, views, appWidgetId)
        Glide.with(context.applicationContext)
            .asBitmap()
            .load(icon)
            .into(widgetIcon)
    }
    views.setOnClickPendingIntent(R.id.widget_layout, getPendingIntentActivity(context))
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

@RequiresApi(Build.VERSION_CODES.M)
private fun getPendingIntentActivity(context: Context): PendingIntent
{
    val intent = Intent(context, MainActivity::class.java)
    val pending = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarm.cancel(pending)
    val interval: Long = 1000*60
    alarm.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), interval, pending)
    return pending
}




