package com.drdlx.cartooneye.mainScreens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.drdlx.cartooneye.startScreen.Greeting
import com.drdlx.cartooneye.ui.theme.CartoonEyeTheme

@Composable
fun MainScreen() {
    CartoonEyeTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Greeting("Android")
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen()
}