package com.example.cameraxfacemlkit.camerax

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.lifecycle.LifecycleOwner
import com.example.cameraxfacemlkit.face_detection.FaceContourDetectionProcessor
import com.example.cameraxfacemlkit.utils.Util
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraManager(
    private val activity: AppCompatActivity,
    private val finderView: PreviewView,
    private val lifecycleOwner: LifecycleOwner,
    private val graphicOverlay: GraphicOverlay
) {

    private var preview: Preview? = null

    private var camera: Camera? = null
    private lateinit var cameraExecutor: ExecutorService
    private var cameraSelectorOption = CameraSelector.LENS_FACING_FRONT
    private var cameraProvider: ProcessCameraProvider? = null

    private var imageAnalyzer: ImageAnalysis? = null

    private var imageCapture: ImageCapture? = null


    init {
        createNewExecutor()
    }

    private fun createNewExecutor() {
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)

        cameraProviderFuture.addListener(
            Runnable {

                cameraProvider = cameraProviderFuture.get()

                preview = Preview.Builder()
                    .build()

                /* imageCapture = ImageCapture.Builder()
                     .setTargetResolution(Size(720,1280))
                     .setFlashMode(FLASH_MODE_ON)
                     .build()*/


                imageCapture = ImageCapture.Builder()
                    .build()

                /*imageCapture = if (Util.resW > 0 && Util.resH> 0){
                    Log.d("myCameraRes","if -- ${Util.resW} -- ${Util.resH}")
                    ImageCapture.Builder()
                        .setTargetResolution(Util.getScreenSize(Util.resW))
                        .build()
                }    else{
                    Log.d("myCameraRes","else")
                    ImageCapture.Builder()
                        .setTargetResolution(Size(Util.defaultResW,Util.defaultResH))
                        .build()
                }*/

                imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, selectAnalyzer())
                    }

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(cameraSelectorOption)
                    .build()

                setCameraConfig(cameraProvider, cameraSelector)

            }, ContextCompat.getMainExecutor(activity)
        )
    }

    private fun selectAnalyzer(): ImageAnalysis.Analyzer {
        return FaceContourDetectionProcessor(graphicOverlay)
    }

    private fun setCameraConfig(
        cameraProvider: ProcessCameraProvider?,
        cameraSelector: CameraSelector
    ) {
        try {
            cameraProvider?.unbindAll()
            camera = cameraProvider?.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalyzer, imageCapture
            )

            preview?.setSurfaceProvider(
                finderView.surfaceProvider
            )

        } catch (e: Exception) {
            Log.e(TAG, "Use case binding failed", e)
        }
    }

    fun changeCameraSelector() {
        cameraProvider?.unbindAll()
        cameraSelectorOption =
            if (cameraSelectorOption == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT
            else CameraSelector.LENS_FACING_BACK
        graphicOverlay.toggleSelector()
        startCamera()
    }

    fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        val overlayBitmap = graphicOverlay.drawToBitmap(Bitmap.Config.ARGB_8888)

        // Create time-stamped output file to hold the image
        val photoFile = File(
            getOutputDirectory(),
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(activity),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {

                   // val savedUri = Uri.fromFile(photoFile)

                    output.savedUri?.let {

                        val savedUri:Uri = it

                        val msg = "Photo capture succeeded: $savedUri"

                        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, msg)

                        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            ImageDecoder.decodeBitmap(ImageDecoder.createSource(activity.contentResolver, it))
                        } else {
                            MediaStore.Images.Media.getBitmap(activity.contentResolver, it)
                        }

                        if (bitmap != null){

                            val finalBitmap = Util.mergeToPin(bitmap.copy(Bitmap.Config.ARGB_8888, true),overlayBitmap)

                            Log.d("myBitmapSize","${finalBitmap?.width}")
                        }

                    }

                }
            })
    }

    private fun getOutputDirectory(): File {
        val mediaDir = activity.externalMediaDirs.firstOrNull()?.let {
            File(it, "CameraxFaceMLKit").apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else activity.filesDir
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

}