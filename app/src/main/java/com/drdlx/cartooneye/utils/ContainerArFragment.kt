package com.drdlx.cartooneye.utils

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.ArFrontFacingFragment

class ContainerArFragment: ArFragment() {
    companion object {
        const val KEY = "FragmentKey"
        const val COLOR = "FragmentColor"
        fun newInstance(key: String, color: String): ArFragment {
            val fragment = ArFrontFacingFragment()
//            fragment.setOnViewCreatedListener(::onViewCreated)
            val argument = Bundle()
            argument.putString(KEY, key)
            argument.putString(COLOR, color)
            fragment.arguments = argument
            return fragment
        }
    }

}