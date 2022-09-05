package com.example.weatherapp.di

import dagger.Module

@Module(includes = [ViewModelModule::class, RepositoryModule::class])
class AppModule(){

}