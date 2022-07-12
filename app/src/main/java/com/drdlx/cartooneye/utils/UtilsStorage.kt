package com.drdlx.cartooneye.utils

import android.app.Activity
import android.content.Context
import android.opengl.GLSurfaceView
import androidx.activity.ComponentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.util.Log
import com.drdlx.cartooneye.startScreen.model.AppRecordingState

object UtilsStorage {
    private const val TAG = "ArActivityStorage"

    private val _arWidth = MutableLiveData<Int>(0)
    private val _arHeight = MutableLiveData<Int>(0)
    val arWidth: LiveData<Int> = _arWidth
    val arHeight: LiveData<Int> = _arHeight

    private val _appRecordingState = MutableLiveData<AppRecordingState>(AppRecordingState.IDLE)
    val appRecordingState: LiveData<AppRecordingState> = _appRecordingState

    fun setDimensions(width: Int, height: Int) {
        _arWidth.postValue(width)
        _arHeight.postValue(height)
    }

    fun changeAppRecordingState(state: AppRecordingState) {
        _appRecordingState.postValue(state)
    }

}