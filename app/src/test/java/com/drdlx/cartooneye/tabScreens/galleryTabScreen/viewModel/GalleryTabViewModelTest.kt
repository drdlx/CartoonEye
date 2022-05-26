package com.drdlx.cartooneye.tabScreens.galleryTabScreen.viewModel

import android.content.Context
import android.net.Uri
import com.drdlx.cartooneye.di.AppModule
import com.drdlx.cartooneye.di.NavigationModule
import com.drdlx.cartooneye.services.files.FilesIOService
import com.drdlx.cartooneye.utils.EMPTY_IMAGE_URI
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class GalleryTabViewModelTest {

    private val filesIOService = mockk<FilesIOService>()
    private val galleryTabViewModel = GalleryTabViewModel(filesIOService)

    private val mContextMock = mockk<Context>(relaxed = true)

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun initialPictureIsEmptyTest() {
        val currentPictureState = galleryTabViewModel.uiState.currentPictureUri
        assertEquals(currentPictureState.value, EMPTY_IMAGE_URI)
    }

    @Test
    fun changeCurrentPictureTest() {
        val currentPictureState = galleryTabViewModel.uiState.currentPictureUri

        val testUri = Uri.parse("file://dev/mem")
        galleryTabViewModel.changeCurrentPicture(testUri)
        assertEquals(currentPictureState.value, testUri)
    }
}