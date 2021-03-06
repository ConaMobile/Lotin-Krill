package com.conamobile.lotin.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.conamobile.lotin.R
import com.conamobile.lotin.bottomSheet.BottomSheetDialog
import com.conamobile.lotin.memory.MySharedPreferences
import com.conamobile.lotin.textSaver.TextSaver
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.customview_dialog.view.*
import kotlinx.android.synthetic.main.layout_parent.view.*
import kotlinx.android.synthetic.main.layout_second.view.*
import kotlinx.android.synthetic.main.player_sheet.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

class MainActivity : AppCompatActivity() {
    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var myClipboard: ClipboardManager? = null
    private lateinit var mAdView: AdView
    private lateinit var view: View
    private var collapseBool = false
    private val cameraPermissionCode = 200
    private var mInterstitialAd: InterstitialAd? = null
    private var fullTextCollapse = false
    private var copiedSaver = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.mainActivity2Theme)
        setContentView(R.layout.activity_main)
        initViews()
    }

    override fun onStart() {
        super.onStart()
        if (TextSaver.textSaverBoolean) {
            loadingText.isVisible = true
            text.isVisible = false
            Handler(Looper.getMainLooper()).postDelayed({
                TextSaver.textSaverBoolean = false
                if (TextSaver.textSaver != "") {
                    TextSaver.textSaver.forEach {
                        editText.append(it.toString())
                    }
                }
                loadingText.isVisible = false
                TextSaver.textSaver = ""
                Handler(Looper.getMainLooper()).postDelayed({
                    showInterstitialAd()
                }, 3000)
                loadingText.isVisible = false
                text.isVisible = true
            }, 1000)
        }
    }

    private fun initViews() {
        val playerSheet: ConstraintLayout = findViewById(R.id.player_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(playerSheet)
        installTextSize()
        bannerAds()
        toolsTouch()
        collapseManager()
        editTextManager()
        keyboardManager()
        sheetScreenResolution()
        expandedTouchManager()
        textSizeManager()
        loadInterstitialAd()
        themeManager()
        switcherManager()
        fullTextManager()
    }

    private fun fullTextManager() {
        fullTextImageBtn.setOnClickListener {
            if (text.text != "") {
                fullTextCard.isVisible = true
                fullEditText.setText(text.text)
                fullTextCollapse = true
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            } else toaster(getString(R.string.input_text))
        }

        cancelFullTextBtn.setOnClickListener {
            if (fullEditText.text.toString() == text.text) {
                copiedSaver = false
                fullTextWatcherBack()
            } else {
                if (editText.text.toString() != "" && !copiedSaver) showAlertDialog()
                else {
                    copiedSaver = false
                    fullTextWatcherBack()
                }
            }
        }

        copyFullTextBtn.setOnClickListener {
            clipboardCopier(fullEditText.text.toString())
            toaster(getString(R.string.copied))
            copiedSaver = true
        }

    }

    private fun toaster(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun themeManager() {
        if (!MySharedPreferences(this).getSavedSwitcher()) {
            turnOnLightMode()
            switcher_text.text = getString(R.string.turn_on_night_mode)
            switcher_compat.isChecked = false
        } else {
            turnOnNightMode()
            switcher_text.text = getString(R.string.turn_off_night_mode)
            switcher_compat.isChecked = true
        }
    }

    private fun turnOnLightMode() {
        linearLayout.background = ActivityCompat.getDrawable(this, R.drawable.media_header_bg_light)
        coordinatorLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white_text))
        resultsCard.background.setTint(ContextCompat.getColor(this, R.color.transparent_black))
        text.setTextColor(ContextCompat.getColor(this, R.color.def2))
        text.setHintTextColor(ContextCompat.getColor(this, R.color.transparent_dark))
        player_sheet.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.player_bg_light
            )
        )
        view_line.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow))
        pasteCard.background.setTint(ContextCompat.getColor(this, android.R.color.transparent))
        copyCard.background.setTint(ContextCompat.getColor(this, android.R.color.transparent))
        clearCard.background.setTint(ContextCompat.getColor(this, android.R.color.transparent))
        cameraCard.background.setTint(ContextCompat.getColor(this, android.R.color.transparent))
        scrollAllColor.setBackgroundColor(ContextCompat.getColor(this, R.color.white_text))
        editTextCard.background.setTint(ContextCompat.getColor(this, R.color.transparent_dark))
        editText.setTextColor(ContextCompat.getColor(this, R.color.def2))
        editText.setHintTextColor(ContextCompat.getColor(this, R.color.transparent_dark))
        textSizeCard.background.setTint(ContextCompat.getColor(this, R.color.transparent_dark))
        nightModeCard.background.setTint(ContextCompat.getColor(this, R.color.transparent_dark))
        programmerCard.background.setTint(ContextCompat.getColor(this, R.color.transparent_dark))
        switcher_text.setTextColor(ContextCompat.getColor(this, R.color.white))
        expandable.parentLayout.programmerInfoCard.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.transparent_dark
            )
        )
        loadingText.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
        loadingText.setTextColor(ContextCompat.getColor(this, R.color.transparent_dark))
        fullTextCard.background.setTint(ContextCompat.getColor(this, R.color.white_text))
        fullEditText.setTextColor(ContextCompat.getColor(this, R.color.black))
        cancelFullTextBtn.background.setTint(ContextCompat.getColor(this, R.color.white))
        copyFullTextBtn.background.setTint(ContextCompat.getColor(this, R.color.white))
    }

    private fun turnOnNightMode() {
        linearLayout.background = ActivityCompat.getDrawable(this, R.drawable.media_header_bg)
        coordinatorLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.def1))
        resultsCard.background.setTint(ContextCompat.getColor(this, R.color.def2))
        text.setTextColor(ContextCompat.getColor(this, R.color.white_text))
        text.setHintTextColor(ContextCompat.getColor(this, R.color.text_color))
        player_sheet.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.player_bg))
        view_line.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow_color))
        pasteCard.background.setTint(ContextCompat.getColor(this, R.color.def2))
        copyCard.background.setTint(ContextCompat.getColor(this, R.color.def2))
        clearCard.background.setTint(ContextCompat.getColor(this, R.color.def2))
        cameraCard.background.setTint(ContextCompat.getColor(this, R.color.def2))
        scrollAllColor.setBackgroundColor(ContextCompat.getColor(this, R.color.def1))
        editTextCard.background.setTint(ContextCompat.getColor(this, R.color.def2))
        editText.setTextColor(ContextCompat.getColor(this, R.color.white_text))
        editText.setHintTextColor(ContextCompat.getColor(this, R.color.text_color))
        textSizeCard.background.setTint(ContextCompat.getColor(this, R.color.def2))
        nightModeCard.background.setTint(ContextCompat.getColor(this, R.color.def2))
        programmerCard.background.setTint(ContextCompat.getColor(this, R.color.def2))
        switcher_text.setTextColor(ContextCompat.getColor(this, R.color.yellow))
        expandable.parentLayout.programmerInfoCard.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.info_color
            )
        )
        loadingText.setBackgroundColor(ContextCompat.getColor(this, R.color.def2))
        loadingText.setTextColor(ContextCompat.getColor(this, R.color.white_text))
        fullTextCard.background.setTint(ContextCompat.getColor(this, R.color.def2))
        fullEditText.setTextColor(ContextCompat.getColor(this, R.color.white_text))
        cancelFullTextBtn.background.setTint(
            ContextCompat.getColor(
                this,
                R.color.transparent_black
            )
        )
        copyFullTextBtn.background.setTint(ContextCompat.getColor(this, R.color.transparent_black))
    }

    private fun switcherManager() {
        switcher_card.setOnClickListener {
            if (!MySharedPreferences(this).getSavedSwitcher()) {
                turnOnNightMode()
                switcher_text.text = getString(R.string.turn_off_night_mode)
                switcher_compat.isChecked = true
                MySharedPreferences(this).isSavedSwitcher(true)
            } else {
                turnOnLightMode()
                switcher_text.text = getString(R.string.turn_on_night_mode)
                switcher_compat.isChecked = false
                MySharedPreferences(this).isSavedSwitcher(false)
            }
        }
        switcher_compat.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                turnOnNightMode()
                switcher_text.text = getString(R.string.turn_off_night_mode)
                MySharedPreferences(this).isSavedSwitcher(true)
            } else if (!isChecked) {
                turnOnLightMode()
                switcher_text.text = getString(R.string.turn_on_night_mode)
                MySharedPreferences(this).isSavedSwitcher(false)
            }
        }
    }

    private fun keyboardManager() {
        KeyboardVisibilityEvent.setEventListener(
            this
        ) {
            if (it) {
                if (!fullTextCollapse) {
                    bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
                    expandable.collapse()
                    collapseBool = false
                }
            } else {
                if (!fullTextCollapse) bottomSheetBehavior?.state =
                    BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)

        val view = LayoutInflater.from(this)
            .inflate(R.layout.customview_dialog, findViewById(R.id.layoutDialog))
        builder.setView(view)
        view.textTitle.text = getString(R.string.attention)
        view.textMessage.text = getString(R.string.message)
        view.buttonCancelAction.text = getString(R.string.cancel)
        view.buttonExitAction.text = getString(R.string.yes)

        val alertDialog: AlertDialog = builder.create()

        view.buttonCancelAction.setOnClickListener {
            fullTextWatcherBack()
            alertDialog.dismiss()
            copiedSaver = false
        }

        view.buttonExitAction.setOnClickListener {
            clipboardCopier(fullEditText.text.toString())
            toaster(getString(R.string.copied))
            fullTextWatcherBack()
            alertDialog.dismiss()
            copiedSaver = false
        }

        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }

        alertDialog.setCancelable(true)
        alertDialog.show()

    }

    private fun fullTextWatcherBack() {
        fullTextCard.isVisible = false
        fullTextCollapse = false
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun installTextSize() {
        when (MySharedPreferences(this).getSavedTextSize()) {
            1 -> {
                allTextSizerCardOff()
                soo_little_card.background.setTint(
                    ContextCompat.getColor(
                        this,
                        R.color.yellow
                    )
                )
                editText.textSize = 10f
                text.textSize = 10f
            }
            2 -> {
                allTextSizerCardOff()
                little_card.background.setTint(ContextCompat.getColor(this, R.color.yellow))
                editText.textSize = 15f
                text.textSize = 15f
            }
            3 -> {
                allTextSizerCardOff()
                normal_card.background.setTint(ContextCompat.getColor(this, R.color.yellow))
                editText.textSize = 20f
                text.textSize = 20f
            }
            4 -> {
                allTextSizerCardOff()
                big_card.background.setTint(ContextCompat.getColor(this, R.color.yellow))
                editText.textSize = 25f
                text.textSize = 25f
            }
            5 -> {
                allTextSizerCardOff()
                soo_big_card.background.setTint(ContextCompat.getColor(this, R.color.yellow))
                editText.textSize = 30f
                text.textSize = 30f
            }
            else -> {
                allTextSizerCardOff()
                normal_card.background.setTint(ContextCompat.getColor(this, R.color.yellow))
                editText.textSize = 20f
                text.textSize = 20f
            }
        }
    }

    private fun textSizeManager() {
        soo_little_card.setOnClickListener {
            allTextSizerCardOff()
            soo_little_card.background.setTint(ContextCompat.getColor(this, R.color.yellow_color))
            editText.textSize = 10f
            text.textSize = 10f
            MySharedPreferences(this).isSavedTextSize(1)
        }
        little_card.setOnClickListener {
            allTextSizerCardOff()
            little_card.background.setTint(ContextCompat.getColor(this, R.color.yellow_color))
            editText.textSize = 15f
            text.textSize = 15f
            MySharedPreferences(this).isSavedTextSize(2)
        }
        normal_card.setOnClickListener {
            allTextSizerCardOff()
            normal_card.background.setTint(ContextCompat.getColor(this, R.color.yellow_color))
            editText.textSize = 20f
            text.textSize = 20f
            MySharedPreferences(this).isSavedTextSize(3)
        }
        big_card.setOnClickListener {
            allTextSizerCardOff()
            big_card.background.setTint(ContextCompat.getColor(this, R.color.yellow_color))
            editText.textSize = 25f
            text.textSize = 25f
            MySharedPreferences(this).isSavedTextSize(4)
        }
        soo_big_card.setOnClickListener {
            allTextSizerCardOff()
            soo_big_card.background.setTint(ContextCompat.getColor(this, R.color.yellow_color))
            editText.textSize = 30f
            text.textSize = 30f
            MySharedPreferences(this).isSavedTextSize(5)
        }
    }

    private fun allTextSizerCardOff() {
        soo_little_card.background.setTint(ContextCompat.getColor(this, R.color.def1))
        little_card.background.setTint(ContextCompat.getColor(this, R.color.def1))
        normal_card.background.setTint(ContextCompat.getColor(this, R.color.def1))
        big_card.background.setTint(ContextCompat.getColor(this, R.color.def1))
        soo_big_card.background.setTint(ContextCompat.getColor(this, R.color.def1))
    }

    private fun expandedTouchManager() {
        expandable.setOnClickListener {
            if (collapseBool) {
                expandable.collapse()
                collapseBool = false
            } else {
                expandable.expand()
                collapseBool = true
            }
        }
        expandable.secondLayout.card2.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "conamobiledev@gmail.com", null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Latin Kirill app")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "")
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }
        expandable.secondLayout.card3.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Cona_Mobile"))
            startActivity(browserIntent)
        }
        expandable.secondLayout.card4.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.linkedin.com/in/nurmuhammad-obidjonov-420835230/")
            )
            startActivity(browserIntent)
        }
        expandable.secondLayout.card5.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ConaMobile"))
            startActivity(browserIntent)
        }
    }

    private fun sheetScreenResolution() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height: Int = displayMetrics.heightPixels - 400

        val params: ViewGroup.LayoutParams = player_sheet.layoutParams
        params.height = height
        player_sheet.layoutParams = params
    }

    private fun editTextManager() {
        view = editText

        myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString() == "") {
                    text.text = ""
                } else {
                    initAll(p0.toString())
                }
            }
        })

        registerForContextMenu(editText)
    }

    private fun collapseManager() {
        bottomSheetBehavior?.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when {
                    newState == BottomSheetBehavior.STATE_HIDDEN -> {
                        if (!fullTextCollapse) {
                            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                            hideKeyboard(this@MainActivity, view)
                        }

                    }
                    bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED -> {
                        view_line.isVisible = true
                    }
                    bottomSheetBehavior?.state == BottomSheetBehavior.STATE_COLLAPSED -> {
                        view_line.isVisible = false
                        expandable.collapse()
                        collapseBool = false
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun hideKeyboard(activity: Activity, viewToHide: View) {
        val actManage = activity
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        actManage.hideSoftInputFromWindow(viewToHide.windowToken, 0)
    }

    private fun clipboardCopier(clipboardText: String) {
        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(android.R.attr.label.toString(), clipboardText)
        clipboard.setPrimaryClip(clip)
    }

    private fun toolsTouch() {
        pasteCard.setOnClickListener { it ->
            val abc = myClipboard?.primaryClip
            if (abc != null) {
                val item = abc.getItemAt(0)
                when {
                    item?.text != "" && item.text != "null" -> {
                        item.text.forEach {
                            editText.append(it.toString())
                        }
                        //cursor position end
                        editText.setSelection(editText.length())
                        Snackbar.make(it, getString(R.string.pasted), 650).show()
                    }
                    else -> {
                        Snackbar.make(it, getString(R.string.empty), 650).show()
                    }
                }
            } else {
                Snackbar.make(it, getString(R.string.empty), 650).show()
            }
        }

        copyCard.setOnClickListener {
            if (text.text?.length!! >= 1) {
                clipboardCopier(text.text.toString())
                Snackbar.make(it, getString(R.string.copied), 650).show()
            } else {
                Snackbar.make(it, getString(R.string.empty), 650).show()
            }
        }

        clearCard.setOnClickListener {
            Snackbar.make(it, getString(R.string.cleared), 650).show()
            if (editText.text?.length!! >= 1) {
                editText.text?.clear()
            } else {
                Snackbar.make(it, getString(R.string.empty), 650).show()
            }
        }

        cameraCard.setOnClickListener {
            requestPermission()
        }

        text.setOnLongClickListener {
            if (text.text.isNotEmpty()) {
                val clipboard: ClipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(android.R.attr.label.toString(), text.text)
                clipboard.setPrimaryClip(clip)
                Snackbar.make(it, "Copied", 650).show()
            }
            return@setOnLongClickListener true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            cameraPermissionCode -> {
                try {
                    if ((grantResults.isNotEmpty() &&
                                grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val bottomSheetDialog = BottomSheetDialog()
                            bottomSheetDialog.showNow(supportFragmentManager, bottomSheetDialog.tag)
                        } else startActivity(Intent(this, TextReaderActivity::class.java))
                    } else {
                        toaster(getString(R.string.allow_camera_permission))
                    }
                    return
                } catch (e: Exception) {
                }
            }
        }
    }


    private fun bannerAds() {
        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

    }

    override fun onCreateContextMenu(
        menu: ContextMenu, v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.paste -> {
                textInReaderClipboard()
            }
            R.id.copy -> {
                if (text.text?.length!! >= 1) {
                    val clipboard: ClipboardManager =
                        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText(android.R.attr.label.toString(), text.text)
                    clipboard.setPrimaryClip(clip)
                    Snackbar.make(view, getString(R.string.copied), 650).show()
                } else {
                    Snackbar.make(view, getString(R.string.empty), 650).show()
                }
            }
        }
        return true
    }

    private fun textInReaderClipboard() {
        val abc = myClipboard?.primaryClip
        if (abc != null) {
            val item = abc.getItemAt(0)
            when {
                item?.text != "" && item.text != "null" -> {
                    item.text.forEach {
                        editText.append(it.toString())
                    }
                    //cursor position end
                    editText.setSelection(editText.length())
                    Snackbar.make(view, getString(R.string.pasted), 650).show()
                }
                else -> {
                    Snackbar.make(view, getString(R.string.empty), 650).show()
                }
            }
        } else {
            Snackbar.make(view, getString(R.string.empty), 650).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initAll(value: String) {

        textCompiler(value)

        editText.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (text.text.isNotEmpty()) {
                    //cursor position end
                    editText.setSelection(editText.length())
                    try {
                        if (editText.text!!.length > 1) {

                            //english
                            //sh -> ??
                            if (editText.text!![editText.text!!.length - 2].toString() == "s" &&
                                editText.text!![editText.text!!.length - 1].toString() == "h"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 1)
                                text.text = "${text.text}??"
                            }
                            //ya -> ??
                            else if (editText.text!![editText.text!!.length - 2].toString() == "y" &&
                                editText.text!![editText.text!!.length - 1].toString() == "a"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 1)
                                text.text = "${text.text}??"
                            }
                            //ye -> ??
                            else if (editText.text!![editText.text!!.length - 2].toString() == "y" &&
                                editText.text!![editText.text!!.length - 1].toString() == "e"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 1)
                                text.text = "${text.text}??"
                            }
                            //Ya -> ??
                            else if (editText.text!![editText.text!!.length - 2].toString() == "Y" &&
                                editText.text!![editText.text!!.length - 1].toString() == "a"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 1)
                                text.text = "${text.text}??"
                            }
                            //YA -> ??
                            else if (editText.text!![editText.text!!.length - 2].toString() == "Y" &&
                                editText.text!![editText.text!!.length - 1].toString() == "A"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 1)
                                text.text = "${text.text}??"
                            }
                            //Ye -> ??
                            else if (editText.text!![editText.text!!.length - 2].toString() == "Y" &&
                                editText.text!![editText.text!!.length - 1].toString() == "e"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 1)
                                text.text = "${text.text}??"
                            }
                            //YE -> ??
                            else if (editText.text!![editText.text!!.length - 2].toString() == "Y" &&
                                editText.text!![editText.text!!.length - 1].toString() == "E"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 1)
                                text.text = "${text.text}??"
                            }
                            //SH -> ??
                            else if (editText.text!![editText.text!!.length - 2].toString() == "S" &&
                                editText.text!![editText.text!!.length - 1].toString() == "H"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 1)
                                text.text = "${text.text}??"
                            }
                            //sh -> ??
                            else if (editText.text!![editText.text!!.length - 2].toString() == "S" &&
                                editText.text!![editText.text!!.length - 1].toString() == "h"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 1)
                                text.text = "${text.text}??"
                            }
                            //o' -> ??
                            else if (editText.text!![editText.text!!.length - 2].toString() == "o" &&
                                editText.text!![editText.text!!.length - 1].toString() == "'"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 1)
                                text.text = "${text.text}o"
                            }
                            //O' -> ??
                            else if (editText.text!![editText.text!!.length - 2].toString() == "O" &&
                                editText.text!![editText.text!!.length - 1].toString() == "'"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 1)
                                text.text = "${text.text}O"
                            }
                            //g' -> ??
                            else if (editText.text!![editText.text!!.length - 2].toString() == "g" &&
                                editText.text!![editText.text!!.length - 1].toString() == "'"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 1)
                                text.text = "${text.text}g"
                            }
                            //G' -> ??
                            else if (editText.text!![editText.text!!.length - 2].toString() == "G" &&
                                editText.text!![editText.text!!.length - 1].toString() == "'"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 1)
                                text.text = "${text.text}G"
                            }
                            //yo -> ??
                            else if (editText.text!![editText.text!!.length - 2].toString() == "y" &&
                                editText.text!![editText.text!!.length - 1].toString() == "o"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 1)
                                text.text = "${text.text}??"
                            }
                            //ch -> ??
                            else if (editText.text!![editText.text!!.length - 2].toString() == "c" &&
                                editText.text!![editText.text!!.length - 1].toString() == "h"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 1)
                                text.text = "${text.text}??"
                            }
                            //Yo -> ??
                            else if (editText.text!![editText.text!!.length - 2].toString() == "Y" &&
                                editText.text!![editText.text!!.length - 1].toString() == "o"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 1)
                                text.text = "${text.text}??"
                            }
                            //YO -> ??
                            else if (editText.text!![editText.text!!.length - 2].toString() == "Y" &&
                                editText.text!![editText.text!!.length - 1].toString() == "O"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 1)
                                text.text = "${text.text}??"
                            }

                            //yu -> ??
                            else if (editText.text!![editText.text!!.length - 2].toString() == "y" &&
                                editText.text!![editText.text!!.length - 1].toString() == "u"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 1)
                                text.text = "${text.text}??"
                            }
                            //Yu -> ??
                            else if (editText.text!![editText.text!!.length - 2].toString() == "Y" &&
                                editText.text!![editText.text!!.length - 1].toString() == "u"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 1)
                                text.text = "${text.text}??"
                            }
                            //YU -> ??
                            else if (editText.text!![editText.text!!.length - 2].toString() == "Y" &&
                                editText.text!![editText.text!!.length - 1].toString() == "U"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 1)
                                text.text = "${text.text}??"
                            }
                            //Ch -> ??
                            else if (editText.text!![editText.text!!.length - 2].toString() == "C" &&
                                editText.text!![editText.text!!.length - 1].toString() == "h"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 1)
                                text.text = "${text.text}??"
                            }
                            //CH -> ??
                            else if (editText.text!![editText.text!!.length - 2].toString() == "C" &&
                                editText.text!![editText.text!!.length - 1].toString() == "H"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 1)
                                text.text = "${text.text}??"
                            }

                            //russian
                            //?? -> yo
                            else if (text.text[text.text.length - 2].toString() == "y" &&
                                text.text[text.text.length - 1].toString() == "o"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 2)
                                text.text = "${text.text}"
                            }
                            //?? -> ch
                            else if (text.text[text.text.length - 2].toString() == "c" &&
                                text.text[text.text.length - 1].toString() == "h"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 2)
                                text.text = "${text.text}"
                            }
                            //?? -> sh
                            else if (text.text[text.text.length - 2].toString() == "s" &&
                                text.text[text.text.length - 1].toString() == "h"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 2)
                                text.text = "${text.text}"
                            }
                            //?? -> yu
                            else if (text.text[text.text.length - 2].toString() == "y" &&
                                text.text[text.text.length - 1].toString() == "u"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 2)
                                text.text = "${text.text}"
                            }
                            //?? -> ya
                            else if (text.text[text.text.length - 2].toString() == "y" &&
                                text.text[text.text.length - 1].toString() == "a"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 2)
                                text.text = "${text.text}"
                            }
                            //?? -> o??
                            else if (text.text[text.text.length - 2].toString() == "o" &&
                                text.text[text.text.length - 1].toString() == "??"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 2)
                                text.text = "${text.text}"
                            }
                            //?? -> g??
                            else if (text.text[text.text.length - 2].toString() == "g" &&
                                text.text[text.text.length - 1].toString() == "??"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 2)
                                text.text = "${text.text}"
                            }
                            //?? -> Yo
                            else if (text.text[text.text.length - 2].toString() == "Y" &&
                                text.text[text.text.length - 1].toString() == "o"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 2)
                                text.text = "${text.text}"
                            }
                            //?? -> Ch
                            else if (text.text[text.text.length - 2].toString() == "C" &&
                                text.text[text.text.length - 1].toString() == "h"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 2)
                                text.text = "${text.text}"
                            }
                            //?? -> G??
                            else if (text.text[text.text.length - 2].toString() == "G" &&
                                text.text[text.text.length - 1].toString() == "??"
                            ) {
                                text.text = text.text.substring(0, text.text.length - 2)
                                text.text = "${text.text}"
                            }
                            //other
                            else text.text = text.text.substring(0, text.text.length - 1)

                        }
                        //?? -> ch manage
                        else if (editText.text!!.toString() == "??") {
                            text.text = "ch"
                        }
                        //?? -> yo manage
                        else if (editText.text!!.toString() == "??") {
                            text.text = "yo"
                        }
                        //?? -> sh manage
                        else if (editText.text!!.toString() == "??") {
                            text.text = "sh"
                        }
                        //?? -> yu manage
                        else if (editText.text!!.toString() == "??") {
                            text.text = "yu"
                        }
                        //?? -> ya manage
                        else if (editText.text!!.toString() == "??") {
                            text.text = "ya"
                        }
                        //?? -> o?? manage
                        else if (editText.text!!.toString() == "??") {
                            text.text = "o??"
                        }
                        //?? -> g?? manage
                        else if (editText.text!!.toString() == "??") {
                            text.text = "g??"
                        }
                        //?? -> Yo manage
                        else if (editText.text!!.toString() == "??") {
                            text.text = "Yo"
                        }
                        //?? -> Ch manage
                        else if (editText.text!!.toString() == "??") {
                            text.text = "Ch"
                        }
                        //?? -> G?? manage
                        else if (editText.text!!.toString() == "??") {
                            text.text = "G??"
                        }
                        //clear
                        else if (editText.text!!.isNotEmpty()) {
                            text.text = text.text.substring(0, text.text.length - 1)
                        }
                    } catch (e: Exception) {
                        text.text = ""
                        val textSave = editText.text!!.toString()
                        editText.setText("")
                        textSave.forEach {
                            editText.append(it.toString())
                        }
                        //cursor position end
                        editText.setSelection(editText.length())
                    }
                } else
                    text.text = ""
            }
            false
        }

    }

    private fun textCompiler(str: String) {
        for (i in str.last().toString()) {
            latinToKrillAndKrillToLatin(i.toString())
        }
    }

    @SuppressLint("SetTextI18n")
    private fun latinToKrillAndKrillToLatin(i: String) {
        when (i) {
            //english little alphabet
            "a" -> {
                if (editText.text!!.length > 1) {
                    //check ya -> y
                    if (editText.text!![editText.text!!.length - 2].toString() == "y") {
                        //remove last
                        text.text = text.text.substring(0, text.text.length - 1)
                        text.text = "${text.text}??"
                    }
                    //check Sh -> S
                    else if (editText.text!![editText.text!!.length - 2].toString() == "Y") {
                        //remove last
                        text.text = text.text.substring(0, text.text.length - 1)
                        text.text = "${text.text}??"
                    } else text.text = "${text.text}??"
                } else text.text = "${text.text}??"
            }
            "b" -> text.text = "${text.text}??"
            "c" -> text.text = "${text.text}c"
            "d" -> text.text = "${text.text}??"
            "e" -> {
                //check last-1 check
                if (editText.text!!.length > 1) {
                    //check ye -> ??
                    if (editText.text!![editText.text!!.length - 2].toString() == "y") {
                        //remove last
                        text.text = text.text.substring(0, text.text.length - 1)
                        text.text = "${text.text}??"
                    }
                    //check Ye -> ??
                    else if (editText.text!![editText.text!!.length - 2].toString() == "Y") {
                        //remove last
                        text.text = text.text.substring(0, text.text.length - 1)
                        text.text = "${text.text}??"
                    }
                    //check first e or ??
                    else {
                        if (text.text[text.text.length - 1].toString() == "??")
                            text.text = "${text.text}??"
                        else if (text.text[text.text.length - 1].toString() == "??")
                            text.text = "${text.text}??"
                        else text.text = "${text.text}??"
                    }
                } else text.text = "${text.text}??"
            }
            "f" -> text.text = "${text.text}??"
            "g" -> text.text = "${text.text}??"
            "h" -> {
//                    check last-1 check
                if (editText.text!!.length > 1) {
                    //check sh -> s
                    if (editText.text!![editText.text!!.length - 2].toString() == "s") {
                        //remove last
                        text.text = text.text.substring(0, text.text.length - 1)
                        text.text = "${text.text}??"
                    }
                    //check Sh -> S
                    else if (editText.text!![editText.text!!.length - 2].toString() == "S") {
                        //remove last
                        text.text = text.text.substring(0, text.text.length - 1)
                        text.text = "${text.text}??"
                    } else if (editText.text!![editText.text!!.length - 2].toString() == "c") {
                        //remove last
                        text.text = text.text.substring(0, text.text.length - 1)
                        text.text = "${text.text}??"
                    } else if (editText.text!![editText.text!!.length - 2].toString() == "C") {
                        //remove last
                        text.text = text.text.substring(0, text.text.length - 1)
                        text.text = "${text.text}??"
                    }
                    //other
                    else text.text = "${text.text}??"

                } else text.text = "${text.text}??"

            }
            "i" -> text.text = "${text.text}??"
            "j" -> text.text = "${text.text}??"
            "k" -> text.text = "${text.text}??"
            "l" -> text.text = "${text.text}??"
            "m" -> text.text = "${text.text}??"
            "n" -> text.text = "${text.text}??"
            "o" -> {
                //check last-1 check
                if (editText.text!!.length > 1) {
                    //check yo -> y
                    if (editText.text!![editText.text!!.length - 2].toString() == "y") {
                        //remove last
                        text.text = text.text.substring(0, text.text.length - 1)
                        text.text = "${text.text}??"
                    }
                    //check Yo -> Y
                    else if (editText.text!![editText.text!!.length - 2].toString() == "Y") {
                        //remove last
                        text.text = text.text.substring(0, text.text.length - 1)
                        text.text = "${text.text}??"
                    }
                    //other
                    else text.text = "${text.text}??"

                } else {
                    text.text = "${text.text}??"
                }
            }
            "p" -> text.text = "${text.text}??"
            "q" -> text.text = "${text.text}??"
            "r" -> text.text = "${text.text}??"
            "s" -> text.text = "${text.text}??"
            "t" -> text.text = "${text.text}??"
            "u" -> {
                //check last-1 check
                if (editText.text!!.length > 1) {
                    //check yu -> y
                    if (editText.text!![editText.text!!.length - 2].toString() == "y") {
                        //remove last
                        text.text = text.text.substring(0, text.text.length - 1)
                        text.text = "${text.text}??"
                    }
                    //check Yu -> Y
                    else if (editText.text!![editText.text!!.length - 2].toString() == "Y") {
                        //remove last
                        text.text = text.text.substring(0, text.text.length - 1)
                        text.text = "${text.text}??"
                    }
                    //other
                    else text.text = "${text.text}??"

                } else {
                    text.text = "${text.text}??"
                }
            }
            "v" -> text.text = "${text.text}??"
            "x" -> text.text = "${text.text}??"
            "y" -> text.text = "${text.text}??"
            "z" -> text.text = "${text.text}??"

            //english upper alphabet
            "A" -> {
                if (editText.text!!.length > 1) {
                    //check YA -> Y
                    if (editText.text!![editText.text!!.length - 2].toString() == "Y") {
                        //remove last
                        text.text = text.text.substring(0, text.text.length - 1)
                        text.text = "${text.text}??"
                    } else text.text = "${text.text}??"
                } else text.text = "${text.text}??"
            }
            "B" -> text.text = "${text.text}??"
            "C" -> text.text = "${text.text}C"
            "D" -> text.text = "${text.text}??"
            "E" -> {
                //check last-1 check
                if (editText.text!!.length > 1) {
                    //check YE -> ??
                    if (editText.text!![editText.text!!.length - 2].toString() == "Y") {
                        //remove last
                        text.text = text.text.substring(0, text.text.length - 1)
                        text.text = "${text.text}??"
                    }
                    //check first e or ??
                    else {
                        if (text.text[text.text.length - 1].toString() == "??")
                            text.text = "${text.text}E"
                        else if (text.text[text.text.length - 1].toString() == "E")
                            text.text = "${text.text}??"
                        else text.text = "${text.text}E"
                    }
                } else text.text = "${text.text}??"
            }
            "F" -> text.text = "${text.text}??"
            "G" -> text.text = "${text.text}??"
            "H" -> {
//                    check last-1 check
                if (editText.text!!.length > 1) {
                    if (editText.text!![editText.text!!.length - 2].toString() == "S") {
                        //remove last
                        text.text = text.text.substring(0, text.text.length - 1)
                        text.text = "${text.text}??"
                    } else if (editText.text!![editText.text!!.length - 2].toString() == "C") {
                        //remove last
                        text.text = text.text.substring(0, text.text.length - 1)
                        text.text = "${text.text}??"
                    } else text.text = "${text.text}??"

                } else text.text = "${text.text}??"

            }
            "I" -> text.text = "${text.text}??"
            "J" -> text.text = "${text.text}??"
            "K" -> text.text = "${text.text}??"
            "L" -> text.text = "${text.text}??"
            "M" -> text.text = "${text.text}??"
            "N" -> text.text = "${text.text}??"
            "O" -> {
                //check last-1 check
                if (editText.text!!.length > 1) {
                    //check YO -> Y
                    if (editText.text!![editText.text!!.length - 2].toString() == "Y") {
                        //remove last
                        text.text = text.text.substring(0, text.text.length - 1)
                        text.text = "${text.text}??"
                    }
                    //other
                    else text.text = "${text.text}??"

                } else text.text = "${text.text}??"

            }
            "P" -> text.text = "${text.text}??"
            "Q" -> text.text = "${text.text}??"
            "R" -> text.text = "${text.text}??"
            "S" -> text.text = "${text.text}??"
            "T" -> text.text = "${text.text}??"
            "U" -> {
                //check last-1 check
                if (editText.text!!.length > 1) {
                    //check YU -> Y
                    if (editText.text!![editText.text!!.length - 2].toString() == "Y") {
                        //remove last
                        text.text = text.text.substring(0, text.text.length - 1)
                        text.text = "${text.text}??"
                    }
                    //other
                    else text.text = "${text.text}??"

                } else {
                    text.text = "${text.text}??"
                }
            }
            "V" -> text.text = "${text.text}??"
            "X" -> text.text = "${text.text}??"
            "Y" -> text.text = "${text.text}??"
            "Z" -> text.text = "${text.text}??"

            //tools
            "'" -> {
                if (editText.text!!.length > 1) {
                    //check o' -> o
                    if (editText.text!![editText.text!!.length - 2].toString() == "o") {
                        //remove last
                        text.text = text.text.substring(0, text.text.length - 1)
                        text.text = "${text.text}??"
                    }
                    //check O' -> O
                    else if (editText.text!![editText.text!!.length - 2].toString() == "O") {
                        //remove last
                        text.text = text.text.substring(0, text.text.length - 1)
                        text.text = "${text.text}??"
                    }
                    //check g' -> ??
                    else if (editText.text!![editText.text!!.length - 2].toString() == "g") {
                        //remove last
                        text.text = text.text.substring(0, text.text.length - 1)
                        text.text = "${text.text}??"
                    }
                    //check G' -> ??
                    else if (editText.text!![editText.text!!.length - 2].toString() == "G") {
                        //remove last
                        text.text = text.text.substring(0, text.text.length - 1)
                        text.text = "${text.text}??"
                    }
                    //check a' -> ??
                    else if (editText.text!![editText.text!!.length - 2].toString() == "G") {
                        //remove last
                        text.text = text.text.substring(0, text.text.length - 1)
                        text.text = "${text.text}??"
                    }
//                    check ' -> ??
                    else if (editText.text!![editText.text!!.length - 2].toString() == "'") {
                        text.text = "${text.text}??"
                    }

                    //other
                    else text.text = "${text.text}??"
                } else text.text = "${text.text}??"
            }

            //russian little alphabet
            "??" -> text.text = "${text.text}a"
            "??" -> text.text = "${text.text}b"
            "??" -> text.text = "${text.text}v"
            "??" -> text.text = "${text.text}g"
            "??" -> text.text = "${text.text}d"
            "??" -> {
                if (editText.text!!.length > 1)
                    text.text = "${text.text}??"
                else text.text = "${text.text}ye"
            }
            "??" -> text.text = "${text.text}yo"
            "??" -> text.text = "${text.text}j"
            "??" -> text.text = "${text.text}z"
            "??" -> text.text = "${text.text}i"
            "??" -> text.text = "${text.text}y"
            "??" -> text.text = "${text.text}k"
            "??" -> text.text = "${text.text}l"
            "??" -> text.text = "${text.text}m"
            "??" -> text.text = "${text.text}n"
            "??" -> text.text = "${text.text}o"
            "??" -> text.text = "${text.text}p"
            "??" -> text.text = "${text.text}r"
            "??" -> text.text = "${text.text}s"
            "??" -> text.text = "${text.text}t"
            "??" -> text.text = "${text.text}u"
            "??" -> text.text = "${text.text}f"
            "??" -> text.text = "${text.text}x"
            "??" -> text.text = "${text.text}s"
            "??" -> text.text = "${text.text}ch"
            "??" -> text.text = "${text.text}sh"
            "??" -> text.text = "${text.text}??"
            "??" -> text.text = "${text.text}e"
            "??" -> text.text = "${text.text}yu"
            "??" -> text.text = "${text.text}ya"
            "??" -> text.text = "${text.text}o??"
            "??" -> text.text = "${text.text}q"
            "??" -> text.text = "${text.text}g??"

            //russian little alphabet
            "??" -> text.text = "${text.text}A"
            "??" -> text.text = "${text.text}B"
            "??" -> text.text = "${text.text}V"
            "??" -> text.text = "${text.text}G"
            "??" -> text.text = "${text.text}D"
            "??" -> {
                if (editText.text!!.length > 1)
                    text.text = "${text.text}E"
                else text.text = "${text.text}Ye"
            }
            "??" -> text.text = "${text.text}Yo"
            "??" -> text.text = "${text.text}J"
            "??" -> text.text = "${text.text}Z"
            "??" -> text.text = "${text.text}I"
            "??" -> text.text = "${text.text}Y"
            "??" -> text.text = "${text.text}K"
            "??" -> text.text = "${text.text}L"
            "??" -> text.text = "${text.text}M"
            "??" -> text.text = "${text.text}N"
            "??" -> text.text = "${text.text}O"
            "??" -> text.text = "${text.text}P"
            "??" -> text.text = "${text.text}R"
            "??" -> text.text = "${text.text}S"
            "??" -> text.text = "${text.text}T"
            "??" -> text.text = "${text.text}U"
            "??" -> text.text = "${text.text}F"
            "??" -> text.text = "${text.text}X"
            "??" -> text.text = "${text.text}S"
            "??" -> text.text = "${text.text}Ch"
            "??" -> text.text = "${text.text}Sh"
            "??" -> text.text = "${text.text}??"
            "??" -> text.text = "${text.text}??"
            "??" -> text.text = "${text.text}??"
            "??" -> text.text = "${text.text}E"
            "??" -> text.text = "${text.text}Yu"
            "??" -> text.text = "${text.text}Ya"
            "??" -> text.text = "${text.text}O??"
            "??" -> text.text = "${text.text}Q"
            "??" -> text.text = "${text.text}G??"

            else -> text.text = "${text.text}$i"

        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            cameraPermissionCode
        )
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, resources.getString(R.string.interstitial_ads), adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(p0: InterstitialAd) {
                    super.onAdLoaded(p0)
                    mInterstitialAd = p0
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    mInterstitialAd = null
                }
            })
    }

    private fun showInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    mInterstitialAd = null
                }

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    mInterstitialAd = null
                    loadInterstitialAd()
                }

            }
            mInterstitialAd!!.show(this)
        }
    }
}