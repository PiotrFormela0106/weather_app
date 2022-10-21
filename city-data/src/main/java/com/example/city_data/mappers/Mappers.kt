package com.example.city_data.mappers

typealias CityRoomDomain = com.example.city_domain.models.City
typealias CityRoomData = com.example.city_data.room.City

fun CityRoomDomain.toData(): CityRoomData = CityRoomData(
    cityId = cityId,
    city = city,
    photoId = photoId
)
fun CityRoomData.toDomain(): CityRoomDomain = CityRoomDomain(
    cityId = cityId,
    city = city,
    photoId = photoId
)
