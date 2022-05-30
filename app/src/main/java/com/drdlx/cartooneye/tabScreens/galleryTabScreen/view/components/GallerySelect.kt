package com.drdlx.cartooneye.tabScreens.galleryTabScreen.view.components

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drdlx.cartooneye.utils.EMPTY_IMAGE_URI
import com.drdlx.cartooneye.utils.Permission
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.drdlx.cartooneye.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GallerySelect(
    modifier: Modifier = Modifier,
    onImageUri: (Uri) -> Unit = { }
) {

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            onImageUri(uri ?: EMPTY_IMAGE_URI)
        }
    )

    @Composable
    fun LaunchGallery() {
        SideEffect {
            launcher.launch("image/*")
        }
    }

    //SideEffect {
    //    launcher.launch("image/*")
    //}

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        Permission(
            Manifest.permission.ACCESS_MEDIA_LOCATION,
            rationale = stringResource(id = R.string.default_permission_request_message),
            permissionsNotAvailableContent = {
                Column(modifier) {
                    Text(stringResource(id = R.string.no_gallery_message))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Button(
                            modifier = Modifier.padding(4.dp),
                            onClick = {
                                context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                })
                            }
                        ) {
                            Text(stringResource(R.string.open_settings))
                        }
                        Button(
                            modifier = Modifier.padding(4.dp),
                            onClick = {
                                onImageUri(EMPTY_IMAGE_URI)
                            }
                        ) {
                            Text(stringResource(id = R.string.use_camera))
                        }
                    }
                }
            },
        ) {
            LaunchGallery()
        }
    } else {
        LaunchGallery()
    }

}
