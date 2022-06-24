package com.drdlx.cartooneye.utils

import android.app.Activity
import android.content.Context
import android.opengl.GLSurfaceView
import androidx.activity.ComponentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.util.Log

object ArActivityStorage {
    private const val TAG = "ArActivityStorage"
    private val _arActivity = MutableLiveData<Activity>(null)
    val arActivity: LiveData<Activity> = _arActivity

    private val _arWidth = MutableLiveData<Int>(0)
    private val _arHeight = MutableLiveData<Int>(0)
    val arWidth: LiveData<Int> = _arWidth
    val arHeight: LiveData<Int> = _arHeight

    fun setActivity(activity: Activity) {
        _arActivity.postValue(activity)
    }

    fun setDimensions(width: Int, height: Int) {
        _arWidth.postValue(width)
        _arHeight.postValue(height)
    }

}