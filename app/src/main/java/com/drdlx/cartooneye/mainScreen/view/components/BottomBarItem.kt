package com.drdlx.cartooneye.mainScreen.view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.drdlx.cartooneye.navigation.routeObjects.cameraTab
import com.drdlx.cartooneye.navigation.routeObjects.galleryTab

sealed class BottomBarItem(val route: String, val icon: ImageVector) {
    object CameraTabItem : BottomBarItem(cameraTab, Icons.Default.Home)
    object GalleryTabItem : BottomBarItem(galleryTab, Icons.Default.List)
}
