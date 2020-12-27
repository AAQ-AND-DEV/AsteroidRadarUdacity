package com.udacity.asteroidradar.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.udacity.asteroidradar.PictureOfDay

@JsonClass(generateAdapter = true)
data class NetworkPod(
    @Json(name="media_type") val mediaType: String,
    val title: String,
    val url: String)

fun NetworkPod.asDomainModel(): PictureOfDay{
    return PictureOfDay(
        mediaType = this.mediaType,
        title = this.title,
        url = this.url
    )
}