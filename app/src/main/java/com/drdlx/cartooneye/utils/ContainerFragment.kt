package com.drdlx.cartooneye.utils

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.drdlx.cartooneye.R
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.Sceneform
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.Texture
import com.google.ar.sceneform.ux.ArFrontFacingFragment
import com.google.ar.sceneform.ux.AugmentedFaceNode
import java.util.concurrent.CompletableFuture


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

    private val loaders: MutableSet<CompletableFuture<*>> = HashSet()

    private var arFragment: ArFrontFacingFragment? = null
    private var arSceneView: ArSceneView? = null

    private var faceTexture: Texture? = null
    private var faceModel: ModelRenderable? = null

    private var facesNodes: HashMap<AugmentedFace, AugmentedFaceNode> = HashMap()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (savedInstanceState != null) {
            count = childFragmentManager.backStackEntryCount
        }
        loadModels()
        loadTextures()
        return inflater.inflate(R.layout.fragment_ar_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            /*val key = it.getString(KEY)
            view.findViewById<TextView>(R.id.text_title).text = key
            view.findViewById<View>(R.id.container).setBackgroundColor(Color.parseColor(it.getString(
                COLOR
            )))*/

        }

        childFragmentManager.addFragmentOnAttachListener(this::onAttachFragment)

        if (savedInstanceState == null) {
            if (Sceneform.isSupported(context)) {
                childFragmentManager.beginTransaction()
                    .add(R.id.arFragment, ArFrontFacingFragment::class.java, null)
                    .commit()
            }
        }

        //childFragmentManager.addOnBackStackChangedListener { count = childFragmentManager.backStackEntryCount }
        arSceneView?.setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST)

        // Check for face detections
        arFragment?.setOnAugmentedFaceUpdateListener(::onAugmentedFaceTrackingUpdate)
    }

    private fun onAttachFragment(fragmentManager: FragmentManager, fragment: Fragment) {
        if (fragment.id == R.id.arFragment) {
            arFragment = fragment as ArFrontFacingFragment
            arFragment!!.setOnViewCreatedListener(::onArViewCreated)
        }
    }

    private fun onArViewCreated(arSceneView: ArSceneView) {
        this.arSceneView = arSceneView

        // This is important to make sure that the camera stream renders first so that
        // the face mesh occlusion works correctly.
        arSceneView.setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST)

        // Check for face detections
        arFragment!!.setOnAugmentedFaceUpdateListener(::onAugmentedFaceTrackingUpdate)
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

        for (loader in loaders) {
            if (!loader.isDone) {
                loader.cancel(true)
            }
        }

    }

    private fun loadModels() {
        loaders.add(ModelRenderable.builder()
            .setSource(context, Uri.parse("models/fox.glb"))
            .setIsFilamentGltf(true)
            .build()
            .thenAccept { model: ModelRenderable ->
                faceModel = model
            }
            .exceptionally {
                Toast.makeText(context, "Unable to load renderable", Toast.LENGTH_LONG).show()
                null
            }
        )
    }

    private fun loadTextures() {
        loaders.add(
            Texture.builder()
                .setSource(context, Uri.parse("textures/freckles.png"))
                .setUsage(Texture.Usage.COLOR_MAP)
                .build()
                .thenAccept { texture: Texture ->
                    faceTexture = texture
                }
                .exceptionally {
                    Toast.makeText(context, "Unable to load texture", Toast.LENGTH_LONG).show()
                    null
                }
        )
    }

    private fun onAugmentedFaceTrackingUpdate(augmentedFace: AugmentedFace) {
        if (faceModel == null || faceTexture == null) {
            return
        }
        val existingFaceNode = facesNodes[augmentedFace]
        when (augmentedFace.trackingState) {
            TrackingState.TRACKING -> if (existingFaceNode == null) {
                val faceNode = AugmentedFaceNode(augmentedFace)
                val modelInstance = faceNode.setFaceRegionsRenderable(faceModel)
                modelInstance.isShadowCaster = false
                modelInstance.isShadowReceiver = true
                faceNode.faceMeshTexture = faceTexture
                arSceneView!!.scene.addChild(faceNode)
                facesNodes[augmentedFace] = faceNode
            }
            TrackingState.STOPPED -> {
                if (existingFaceNode != null) {
                    arSceneView!!.scene.removeChild(existingFaceNode)
                }
                facesNodes.remove(augmentedFace)
            }
        }
    }

}