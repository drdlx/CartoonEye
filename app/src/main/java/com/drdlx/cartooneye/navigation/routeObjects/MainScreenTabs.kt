package com.drdlx.cartooneye.navigation.routeObjects

const val cameraTab = "cameraTab"
const val galleryTab = "galleryTab"

sealed class MainScreenTabRoute(val name: String) {
    object CameraTab: MainScreenTabRoute(cameraTab)
    object GalleryTab: MainScreenTabRoute(galleryTab)
}
