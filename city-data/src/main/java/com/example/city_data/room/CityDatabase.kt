package com.example.city_data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [City::class], version = 2, exportSchema = false)
abstract class CityDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao

    companion object {
        private var instance: CityDatabase? = null

        fun getInstance(context: Context): CityDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context,
                    CityDatabase::class.java,
                    "city_table"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance!!
        }

        fun deleteInstanceOfDatabase() {
            instance = null
        }
    }
}
