package com.hasan.foraty.photogallery.networking

import com.google.gson.annotations.SerializedName
import com.hasan.foraty.photogallery.data.GalleryItem

class PhotoResponse {
    @SerializedName("photo")
    lateinit var galleryItems:List<GalleryItem>
}