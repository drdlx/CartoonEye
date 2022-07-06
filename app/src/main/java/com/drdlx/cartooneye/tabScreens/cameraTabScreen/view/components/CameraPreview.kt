package com.drdlx.cartooneye.tabScreens.cameraTabScreen.view.components

import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.opengl.GLSurfaceView
import android.view.View
import android.widget.FrameLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.doOnAttach
import androidx.fragment.app.*
import androidx.fragment.app.commit
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.SceneView
import com.google.ar.sceneform.Sceneform
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.ArFrontFacingFragment


@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    supportFragmentManager: FragmentManager,
    arFragment: ArFrontFacingFragment,
) {

    FragmentContainer(
        modifier = Modifier.fillMaxSize(),
        fragmentManager = supportFragmentManager,
        commit = { add(it, arFragment) }
    )

}

/** Access to package-private method in FragmentManager through reflection */
private fun FragmentManager.onContainerAvailable(view: FragmentContainerView) {
    val method = FragmentManager::class.java.getDeclaredMethod(
        "onContainerAvailable",
        FragmentContainerView::class.java
    )
    method.isAccessible = true
    method.invoke(this, view)
}

@Composable
fun FragmentContainer(
    modifier: Modifier = Modifier,
    fragmentManager: FragmentManager,
    commit: FragmentTransaction.(containerId: Int) -> Unit
) {
    val localView = LocalView.current
    val containerId = rememberSaveable {
        mutableStateOf(View.generateViewId()) }
    val container = remember {
        mutableStateOf<FragmentContainerView?>(null) }
    val viewBlock: (Context) -> View = remember(localView) {
        { context ->
            FragmentContainerView(context)
                .apply { id = containerId.value }
                .also {
                    fragmentManager.commit { commit(it.id) }
                    container.value = it
                }
        }
    }
    AndroidView(
        modifier = modifier,
        factory = viewBlock,
        update = {},
    )

    val localContext = LocalContext.current
    DisposableEffect(localView, localContext, container) {
        onDispose {
            val existingFragment = fragmentManager.findFragmentById(container.value?.id ?: 0)
            if (existingFragment != null &&
                !fragmentManager.isStateSaved) {
                fragmentManager.commit {
                    detach(existingFragment)
                    remove(existingFragment)
                }
            }
        }
    }
}