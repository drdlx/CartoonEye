package com.drdlx.cartooneye.utils

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.drdlx.cartooneye.R
import com.google.accompanist.permissions.*

@ExperimentalPermissionsApi
@Composable
fun Permission(
    vararg permissions: String,
    rationale: String = stringResource(id = R.string.default_permission_request_message),
    permissionsNotAvailableContent: @Composable () -> Unit = { },
    content: @Composable () -> Unit = { }
) {
    val multiplePermissionsState = rememberMultiplePermissionsState(permissions = permissions.asList())
    PermissionsRequired(
        multiplePermissionsState = multiplePermissionsState,
        permissionsNotGrantedContent = { Rationale(
            text = rationale,
            onRequestPermission = {
                multiplePermissionsState.launchMultiplePermissionRequest()
            }
        )},
        permissionsNotAvailableContent = permissionsNotAvailableContent,
        content = content,
    )

}

@Composable
private fun Rationale(
    text: String,
    onRequestPermission: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* Don't */ },
        title = {
            Text(text = stringResource(id = R.string.default_permission_request_header))
        },
        text = {
            Text(text)
        },
        confirmButton = {
            Button(onClick = onRequestPermission) {
                Text(stringResource(id = R.string.ok))
            }
        }
    )
}