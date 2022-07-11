package com.drdlx.cartooneye.tabScreens.cameraTabScreen.view.components


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.drdlx.cartooneye.tabScreens.cameraTabScreen.model.CaptureButtonWorkMode
import com.drdlx.cartooneye.utils.ContainerArFragment
import com.drdlx.cartooneye.utils.ContainerFragment
import com.drdlx.cartooneye.utils.FragmentContainerCompose


@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    captureButtonWorkMode: CaptureButtonWorkMode,
    getCommitFunction: (
        fragment: Fragment,
        tag: String
    ) -> (FragmentTransaction.(containerId: Int) -> Unit)
) {

    //if (captureButtonWorkMode != CaptureButtonWorkMode.INITIAL) {

        FragmentContainerCompose(
            modifier = Modifier.fillMaxSize(),
            commit = getCommitFunction(
                ContainerFragment.newInstance("Home", "#FFFF00"),
                "container"
            )
        )
    //}

}