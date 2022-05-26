package com.conamobile.lotin.bottomSheet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.conamobile.lotin.R
import com.conamobile.lotin.activity.CameraXActivity
import com.conamobile.lotin.activity.TextReaderActivity
import com.conamobile.lotin.textSaver.TextSaver
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottomsheet_design.*

class BottomSheetDialog: BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_design, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        defaultCameraBtn.setOnClickListener {
            dismiss()
            startActivity(Intent(context, TextReaderActivity::class.java))
        }
        cameraXBtn.setOnClickListener {
            dismiss()
            startActivity(Intent(context, CameraXActivity::class.java))
        }
    }
}