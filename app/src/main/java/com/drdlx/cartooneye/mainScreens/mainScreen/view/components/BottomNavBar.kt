package com.drdlx.cartooneye.mainScreens.mainScreen.view.components

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.drdlx.cartooneye.mainScreens.mainScreen.model.StringCallback

@Composable
fun BottomNavBar(
    currentRoute: String,
    onItemClick: StringCallback? = null
) {
    val items = listOf(
        BottomBarItem.CameraTabItem,
        BottomBarItem.GalleryTabItem,
    )
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White
    ) {
        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.route)
                    /*when (currentRoute) {
                        item.route -> Icon(imageVector = item.icon, contentDescription = item.route)
                        else -> Icon(painterResource(id = item.disable_icon_id), contentDescription = item.route)
                    }*/
                },
                selected = currentRoute == item.route,
                alwaysShowLabel = true,
                /*selectedContentColor = Color.Unspecified,
                unselectedContentColor = Color.Unspecified,*/
                onClick = { onItemClick?.invoke(item.route) }
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun BottomNavBarPreview() {
    BottomNavBar(currentRoute = BottomBarItem.CameraTabItem.route)
}