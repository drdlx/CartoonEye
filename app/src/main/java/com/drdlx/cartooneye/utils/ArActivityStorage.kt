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

    private val _arRenderer = MutableLiveData<ARFacesRenderer>(null)
    val arRenderer: LiveData<ARFacesRenderer> = _arRenderer

    fun initRenderer(context: Context) {
        if(arActivity.value != null) {
            val renderer = ARFacesRenderer(context, arActivity.value!!)
            _arRenderer.postValue(renderer)
            Log.d(TAG, "Renderer created! current renderer: ${_arRenderer.value}")
            Log.d(TAG, "Renderer created! current renderer: ${renderer}")
        } else {
            Log.e(TAG, "Right now there's no activity presented!")
        }
    }

    fun setActivity(activity: Activity) {
        _arActivity.postValue(activity)
    }

}