package com.hasan.foraty.photogallery.api

import com.google.gson.annotations.SerializedName
import com.hasan.foraty.photogallery.GalleryItem

class PhotoResponse {
    @SerializedName("photo")
    lateinit var galleryItems:List<GalleryItem>
}