package com.conamobile.lotin.activity

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Rational
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import com.conamobile.lotin.R
import com.conamobile.lotin.textSaver.TextSaver
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.android.synthetic.main.activity_camera_xactivity.*
import yanzhikai.textpath.PathAnimatorListener
import java.io.File

class CameraXActivity : AppCompatActivity() {
    private var flashManage = false
    private var imageCaptured = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.mainActivity2Theme)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(R.layout.activity_camera_xactivity)
        initViews()
    }

    private fun initViews() {
        brandNameManager()
        flashBtn.setOnClickListener { flashManager() }
        replaceBtn.setOnClickListener { onToggleCameraClick() }
        captureBtn.setOnClickListener { onCaptureImageClick() }
        galleryBtn.setOnClickListener { galleryManager() }
        galleryBtn.setOnLongClickListener { galleryTitleManager(); return@setOnLongClickListener false }
        bindCamera()
    }

    @SuppressLint("NewApi")
    private fun enterPipMode() {
        val aspectRatio = Rational(10, 18)
        val params = PictureInPictureParams
            .Builder()
            .setAspectRatio(aspectRatio)
            .build()
        enterPictureInPictureMode(params)
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        if (isInPictureInPictureMode) {
            flashBtn.isVisible = false
            captureBtn.isVisible = false
            replaceBtn.isVisible = false
            galleryBtn.isVisible = false
            company_name.isVisible = false
            company_username.isVisible = false
        } else {
            flashBtn.isVisible = true
            captureBtn.isVisible = true
            replaceBtn.isVisible = true
            galleryBtn.isVisible = true
            company_name.isVisible = true
            company_username.isVisible = true
        }
    }

    private fun galleryTitleManager() {
        galleryText.isVisible = true
        Handler().postDelayed({
            galleryText.isVisible = false
        }, 2000)
    }

    override fun onUserLeaveHint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enterPipMode()
        }
    }

    private fun galleryManager() {
        ImagePicker.with(this)
            .galleryOnly()
            .createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                val fileUri = data?.data!!
                imageView.setImageURI(fileUri)
                imageSelectedSuccess()
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
        }

    private fun brandNameManager() {
        company_name.startAnimation(0F, 1F)
        company_username.startAnimation(0F, 1F)

        company_username.setAnimatorListener(object : PathAnimatorListener() {
            override fun onAnimationStart(animation: Animator, isReverse: Boolean) {}

            override fun onAnimationEnd(animation: Animator, isReverse: Boolean) {
                super.onAnimationEnd(animation, isReverse)
                Handler().postDelayed({
                    company_name.startAnimation(0F, 1F)
                    company_username.startAnimation(0F, 1F)
                }, 5000)
            }
        })
    }

    private fun flashManager() {
        if (!imageCaptured) {
            if (!flashManage) {
                turnOnFlash()
            } else {
                turnOffFlash()
            }
        } else {
            cancelManager()
        }
    }

    private fun cancelManager() {
        imageCaptured = false
        imageView.visibility = View.GONE
        resultTvCard.visibility = View.GONE
        flashImage.setImageResource(R.drawable.flash_off)
        replaceImg.setImageResource(R.drawable.replace_png)
        captureBtn.visibility = View.VISIBLE
        imageView.setImageResource(0)
        resultText.text = ""
        galleryBtn.isVisible = true
    }

    private fun turnOnFlash() {
        flashImage.setImageResource(R.drawable.flash_on)
        cameraView.enableTorch(true)
        flashManage = true
    }

    private fun turnOffFlash() {
        flashImage.setImageResource(R.drawable.flash_off)
        cameraView.enableTorch(false)
        flashManage = false
    }

    private fun onToggleCameraClick() {
        if (!imageCaptured) {
            cameraView.toggleCamera()
            turnOffFlash()
        } else {
            submitManager()
        }
    }

    private fun submitManager() {
        TextSaver.textSaver = resultText.text.toString()
        TextSaver.textSaverBoolean = true
        finish()
    }

    @SuppressLint("MissingPermission")
    private fun bindCamera() {
        cameraView.isPinchToZoomEnabled = true
        try {
            cameraView.bindToLifecycle(this)
        } catch (e: java.lang.IllegalArgumentException) {
            Toast.makeText(this, "Don't support your phone", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onCaptureImageClick() {
        val file = File(filesDir.absoluteFile, "temp.jpg")
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        cameraView.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(this),
            imageSavedCallback
        )
    }

    private val imageSavedCallback = object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            imageView.setImageURI(outputFileResults.savedUri)
            imageSelectedSuccess()
        }

        override fun onError(exception: ImageCaptureException) {
            Toast.makeText(
                this@CameraXActivity,
                "Error: ${exception.message}${exception.imageCaptureError}",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    private fun imageSelectedSuccess() {
        imageCaptured = true
        cameraView.enableTorch(false)
        flashManage = false
        imageView.isVisible = true
        captureBtn.visibility = View.INVISIBLE
        flashImage.setImageResource(R.drawable.cancel_png)
        replaceImg.setImageResource(R.drawable.success_png)
        resultTvCard.visibility = View.VISIBLE
        galleryBtn.isVisible = false
        //textReader
        detectText(imageView.drawable.toBitmap())
    }

    //textReader
    @SuppressLint("UnsafeOptInUsageError")
    private fun detectText(imageBit: Bitmap) {
        lottieAnim.isVisible = true
        val imager: InputImage = InputImage.fromBitmap(imageBit, 0)
        val recognizer: TextRecognizer =
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val result = recognizer.process(imager)
            .addOnSuccessListener { visionText ->
                lottieAnim.isVisible = false
                if (visionText.text != "")
                    resultText.text = visionText.text
                else {
                    Toast.makeText(this, getString(R.string.text_not_found), Toast.LENGTH_SHORT)
                        .show()
                    cancelManager()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, getString(R.string.please_check_internet), Toast.LENGTH_LONG)
                    .show()
                lottieAnim.isVisible = false
                finish()
            }

    }
}