package com.full.qr.scanner.top.secure.no.feature.tabs.settings.camera

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.full.qr.scanner.top.secure.no.R
import com.full.qr.scanner.top.secure.no.di.settings
import com.full.qr.scanner.top.secure.no.extension.applySystemWindowInsets
import com.full.qr.scanner.top.secure.no.feature.BaseActivity
import com.full.qr.scanner.top.secure.no.feature.common.view.SettingsRadioButton

class ChooseCameraActivity : BaseActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ChooseCameraActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_camera)
        supportEdgeToEdge()
        handleToolbarBackClicked()
    }

    override fun onResume() {
        super.onResume()
        showSelectedCamera()
        handleBackCameraButtonChecked()
        handleFrontCameraButtonChecked()
    }

    private fun showSelectedCamera() {
        val isBackCamera = settings.isBackCamera
        findViewById<SettingsRadioButton>(R.id.button_back_camera).isChecked = isBackCamera
        findViewById<SettingsRadioButton>(R.id.button_front_camera).isChecked = isBackCamera.not()
    }

    private fun supportEdgeToEdge() {
        findViewById<CoordinatorLayout>(R.id.root_view)
            .applySystemWindowInsets(applyTop = true, applyBottom = true)
    }

    private fun handleToolbarBackClicked() {
        findViewById<Toolbar>(R.id.toolbar)?.let { toolbar ->
            toolbar.setNavigationOnClickListener {
                finish()
            }
        }
    }

    private fun handleBackCameraButtonChecked() {
        findViewById<SettingsRadioButton>(R.id.button_back_camera)?.let { button_back_camera ->
            button_back_camera.setCheckedChangedListener { isChecked ->
                if (isChecked) {
                    findViewById<SettingsRadioButton>(R.id.button_front_camera).isChecked = false
                }
                settings.isBackCamera = isChecked
            }
        }
    }

    private fun handleFrontCameraButtonChecked() {
        findViewById<SettingsRadioButton>(R.id.button_front_camera).setCheckedChangedListener { isChecked ->
            if (isChecked) {
                findViewById<SettingsRadioButton>(R.id.button_back_camera).isChecked = false
            }
            settings.isBackCamera = isChecked.not()
        }
    }
}