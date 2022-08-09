package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weatherapp.data.Human.DaggerHumanComponent
import com.example.weatherapp.data.Human.Human
import com.example.weatherapp.databinding.ActivityMainBinding
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //@Inject
    //lateinit var human: Human
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //val component = DaggerHumanComponent.create()
        //component.inject(this)
        //human.isAlive()
    }

}