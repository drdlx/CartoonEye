package com.drdlx.cartooneye.tabScreens.cameraTabScreen.view

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drdlx.cartooneye.R
import com.drdlx.cartooneye.tabScreens.cameraTabScreen.view.components.CameraPreview
import com.drdlx.cartooneye.utils.Permission
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraTabScreen() {
    val context = LocalContext.current
    Permission(
        permission = Manifest.permission.CAMERA,
        rationale = stringResource(id = R.string.camera_permission_ask_message),
        permissionNotAvailableContent = {
            Column(Modifier) {
                Text(stringResource(id = R.string.no_camera_message))
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    })
                }) {
                    Text(stringResource(id = R.string.open_settings))
                }
            }
        }
    ) {
        CameraPreview()
    }
}