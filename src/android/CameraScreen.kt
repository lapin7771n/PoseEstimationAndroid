package android.cordovapluginskeleton

import android.Manifest.permission
import android.app.Fragment
import android.content.pm.PackageManager
import android.cordovapluginskeleton.utils.ImageBuffer
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.*
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import org.cdv.test.app.R

class CameraScreen : Fragment(), LifecycleOwner {

    private lateinit var viewFinder: TextureView
    private lateinit var lifecycleRegistry: LifecycleRegistry

    private var rotation = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.markState(Lifecycle.State.CREATED)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.camera_view, container, false)
        viewFinder = view.findViewById(R.id.viewFinder)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(activity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleRegistry.markState(Lifecycle.State.STARTED)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED)
    }

    private fun startCamera() {
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(viewFinder.width, viewFinder.height))
        }.build()

        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener {

            // To update the SurfaceTexture, we have to remove it and re-add it
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)

            viewFinder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }

        CameraX.bindToLifecycle(this, preview, buildImageAnalysisUseCase())
    }

    fun buildImageAnalysisUseCase(): ImageAnalysis {
        val analysisConfig = ImageAnalysisConfig.Builder()
                .setTargetResolution(Size(viewFinder.width, viewFinder.height))
                .build()

        val analysis = ImageAnalysis(analysisConfig)
        val imageBuffer = ImageBuffer.getInstance()

        analysis.setAnalyzer { image, rotationDegrees ->
            Thread {
                val bitmap = viewFinder.getBitmap(ImageBuffer.SIZE_OF_IMAGE, ImageBuffer.SIZE_OF_IMAGE)
                Log.d(TAG, "w: ${bitmap.width}, h: ${bitmap.height}")
                imageBuffer.addFrame(bitmap)
            }.start()
        }
        return analysis
    }


    override fun onResume() {
        super.onResume()
        lifecycleRegistry.markState(Lifecycle.State.RESUMED)
    }

    private fun updateTransform() {
        val matrix = Matrix()

        //Find the center
        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f

        //Get correct rotation
        rotation = when (viewFinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }

        matrix.postRotate(-rotation.toFloat(), centerX, centerY)

        viewFinder.setTransform(matrix)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(activity,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show()
                activity?.finish()
            }
        }
    }

    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                            activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        Log.i(TAG, "All permissions granted")
        return true
    }

    override fun getLifecycle(): Lifecycle {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        private const val TAG = "CameraScreen"
        private const val REQUEST_CODE_PERMISSIONS = 42
        private val REQUIRED_PERMISSIONS = arrayOf(permission.CAMERA)
    }
}
