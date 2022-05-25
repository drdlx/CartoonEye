package com.drdlx.cartooneye.di

import com.drdlx.cartooneye.services.files.FilesIOService
import com.drdlx.cartooneye.services.files.FilesIoServiceImpl
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
class FilesModule {

    @Singleton
    fun provideFilesService(): FilesIOService = FilesIoServiceImpl()
}