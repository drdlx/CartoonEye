package com.drdlx.cartooneye.tabScreens.cameraTabScreen.viewModel

import android.net.Uri
import com.drdlx.cartooneye.services.files.FilesIOService
import com.drdlx.cartooneye.utils.EMPTY_IMAGE_URI
import io.mockk.mockk
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext.stopKoin
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class CameraTabViewModelTest {

    private val filesIOService = mockk<FilesIOService>()
    private val cameraTabViewModel = CameraTabViewModel(filesIOService)

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun initialPictureIsEmptyTest() {
        val currentPictureState = cameraTabViewModel.uiState.currentPictureUri
        assertEquals(currentPictureState.value, EMPTY_IMAGE_URI)
    }

    @Test
    fun changeCurrentPictureTest() {
        val currentPictureState = cameraTabViewModel.uiState.currentPictureUri

        val testUri = Uri.parse("file://dev/mem")
        cameraTabViewModel.changeCurrentPicture(testUri)
        assertEquals(currentPictureState.value, testUri)
    }

}