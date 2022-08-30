package com.example.weatherapp.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [City::class], version = 3, exportSchema = false)//version 1 until it's not released
abstract class CityDatabase: RoomDatabase() {
    abstract fun cityDao(): CityDao

    companion object{
        private var instance: CityDatabase? = null

        fun getInstance(context: Context): CityDatabase{
            if (instance==null)
            {
                instance = Room.databaseBuilder(
                    context,
                    CityDatabase::class.java,
                    "city_table")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance!!
        }

        fun deleteInstanceOfDatabase(){
            instance = null
        }
    }
}