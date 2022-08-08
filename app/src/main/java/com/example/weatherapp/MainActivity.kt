package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import retrofit2.converter.gson.GsonConverterFactory
import com.example.weatherapp.data.api.WeatherApi
import com.example.weatherapp.data.models.WeatherClass
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tView: TextView = findViewById(R.id.text_view)
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        val weatherApi: WeatherApi = retrofit.create(WeatherApi::class.java)
        val call: Call<WeatherClass> =
            weatherApi.getData("Somonino", "7a6886b06890c79387cbdf1ebc857ef2")

        call.enqueue(object : Callback<WeatherClass?> {
            override fun onResponse(
                call: Call<WeatherClass?>,
                response: Response<WeatherClass?>
            ) {
                if (!response.isSuccessful) {
                    tView.text = response.code().toString()
                    return;
                }

                val weatherObj: WeatherClass? = response.body()
                tView.append("City name: ${weatherObj?.cityName}\n")
                val temp = weatherObj?.main?.temp?.minus(273.15)
                val rounded = String.format("%.2f", temp)
                tView.append("Temperature: $rounded C\n")
                tView.append("${weatherObj?.weather?.get(0)?.main}")
            }

            override fun onFailure(call: Call<WeatherClass?>, t: Throwable) {
                tView.text = t.message
            }

        })

    }

}