package com.example.android.firefly.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize    //When navigating to details screen, we need to pass this object
data class UnsplashPhoto(
    val id: String,
    val description: String?,
    val blur_hash:String,
    val width:Int,
    val height:Int,
    val user: UnsplashUser,
    val urls: UnsplashUrls,
    val links:UnsplashLinks
) : Parcelable {

    @Parcelize
    data class UnsplashUrls(
        val raw: String,
        val full: String,
        val regular: String,
        val small: String,
        val thumb: String
    ) : Parcelable

    @Parcelize
    data class UnsplashUser(
        val name: String,
        val username: String
    ) : Parcelable {
        val attribution = "https://unsplash.com/$username?utm_source=Firefly&utm_medium=referral"
    }

    @Parcelize
    data class UnsplashLinks(
        val download:String
    ):Parcelable
}
