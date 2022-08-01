package com.conamobile.lotin.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import com.conamobile.lotin.R
import com.conamobile.lotin.textSaver.TextSaver
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
import kotlinx.android.synthetic.main.activity_text_reader.*


class TextReaderActivity : AppCompatActivity() {
    private var allPhotos = ArrayList<Uri>()
    private var pickedPhoto: Uri? = null
    private var permissionCheck = false
    private var permissionSplash = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.mainActivity2Theme)
        setContentView(R.layout.activity_text_reader)
        TextSaver.textSaver = ""
        intentSettingsButton()
    }

    override fun onStart() {
        super.onStart()
        if (!permissionCheck)
            checkPermission()
    }


    private fun pickFishBunPhoto() {
        permissionCheck = true
        FishBun.with(this)
            .setImageAdapter(GlideAdapter())
            .setMaxCount(1)
            .setMinCount(1)
            .setAllViewTitle(getString(R.string.all_image))
            .textOnNothingSelected(getString(R.string.select_any_image))
            .setPickerSpanCount(3)
            .setActionBarColor(
                ContextCompat.getColor(this, R.color.def2),
                ContextCompat.getColor(this, R.color.def2),
                false
            )
            .setActionBarTitleColor(ContextCompat.getColor(this, R.color.yellow_color))
            .hasCameraInPickerPage(true)
            .setButtonInAlbumActivity(false)
            .setSelectedImages(allPhotos)
            .startAlbumWithActivityResultCallback(photoLauncher)
    }

    private val photoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                Activity.RESULT_OK -> {
                    allPhotos =
                        it.data?.getParcelableArrayListExtra(FishBun.INTENT_PATH) ?: arrayListOf()
                    pickedPhoto = allPhotos[0]
                    imageBetaSuccessful.isVisible = true
                    animationView9.isVisible = true
                    permissionCheck = false
                    imageBetaSuccessful.setImageURI(pickedPhoto)
                    detectText(imageBetaSuccessful.drawable.toBitmap())
                }
                Activity.RESULT_CANCELED -> {
                    permissionCheck = true
                    finish()
                }
                else -> {
                    finish()
                }
            }
        }


    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            permissionSplash++
            toaster(getString(R.string.allow_storage))
            permission_decline_text.isVisible = true
            permission_decline_button.isVisible = true
            animationView9.isVisible = false
            imageBetaSuccessful.isVisible = false
            if (permissionSplash < 3)
                pickFishBunPhoto()
        } else {
            permission_decline_text.isVisible = false
            permission_decline_button.isVisible = false
            animationView9.isVisible = true
            imageBetaSuccessful.isVisible = true
            pickFishBunPhoto()
        }
    }

    private fun detectText(bitmap: Bitmap) {
        val image: InputImage = InputImage.fromBitmap(bitmap, 0)
        val recognizer: TextRecognizer =
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                TextSaver.textSaver = visionText.text
                TextSaver.textSaverBoolean = true
                Handler(Looper.getMainLooper()).postDelayed({
                    finish()
                }, 300)

            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun toaster(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun intentSettingsButton() {
        permission_decline_button.setOnClickListener {
            permissionCheck = false
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:" + this.packageName)
            startActivity(intent)
        }
    }
}