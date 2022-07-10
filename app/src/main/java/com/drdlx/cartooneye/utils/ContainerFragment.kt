package com.drdlx.cartooneye.utils

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.drdlx.cartooneye.R
import com.google.ar.sceneform.Sceneform
import com.google.ar.sceneform.ux.ArFrontFacingFragment


class ContainerFragment : Fragment() {

    companion object {
        const val KEY = "FragmentKey"
        const val COLOR = "FragmentColor"
        fun newInstance(key: String, color: String): Fragment {
            val fragment = ContainerFragment()
            val argument = Bundle()
            argument.putString(KEY, key)
            argument.putString(COLOR, color)
            fragment.arguments = argument
            return fragment
        }
    }

    private var count = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (savedInstanceState != null) {
            count = childFragmentManager.backStackEntryCount
        }
        if (savedInstanceState == null) {
            if (Sceneform.isSupported(context)) {
                childFragmentManager.beginTransaction()
                    .replace(com.drdlx.cartooneye.R.id.arFragment, ArFrontFacingFragment::class.java, null)
                    .commit()
            }
        }
        return inflater.inflate(com.drdlx.cartooneye.R.layout.fragment_ar_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val key = it.getString(KEY)
            val color = it.getString(COLOR)
//            val fragment = childFragmentManager.findFragmentById(com.drdlx.cartooneye.R.id.arFragment)

//            view.findViewById<FrameLayout>(R.id.ar_fragment).setBackgroundColor(Color.parseColor(color))
        }

        childFragmentManager.addOnBackStackChangedListener { count = childFragmentManager.backStackEntryCount }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("Track", "ContainerFragment onSaveInstanceState")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Track", "ContainerFragment onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Track", "ContainerFragment onStop")
    }
}