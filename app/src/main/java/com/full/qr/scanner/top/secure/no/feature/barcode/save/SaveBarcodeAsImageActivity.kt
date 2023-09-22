package com.full.qr.scanner.top.secure.no.feature.barcode.save

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import com.full.qr.scanner.top.secure.no.R
import com.full.qr.scanner.top.secure.no.di.barcodeImageGenerator
import com.full.qr.scanner.top.secure.no.di.barcodeImageSaver
import com.full.qr.scanner.top.secure.no.di.permissionsHelper
import com.full.qr.scanner.top.secure.no.extension.applySystemWindowInsets
import com.full.qr.scanner.top.secure.no.extension.showError
import com.full.qr.scanner.top.secure.no.extension.unsafeLazy
import com.full.qr.scanner.top.secure.no.feature.BaseActivity
import com.full.qr.scanner.top.secure.no.model.Barcode
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class SaveBarcodeAsImageActivity : BaseActivity() {

    companion object {
        private const val REQUEST_PERMISSIONS_CODE = 101
        private val PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        private const val BARCODE_KEY = "BARCODE_KEY"

        fun start(context: Context, barcode: Barcode) {
            val intent = Intent(context, SaveBarcodeAsImageActivity::class.java).apply {
                putExtra(BARCODE_KEY, barcode)
            }
            context.startActivity(intent)
        }
    }

    private val barcode by unsafeLazy {
        intent?.getSerializableExtra(BARCODE_KEY) as? Barcode
            ?: throw IllegalArgumentException("No barcode passed")
    }

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_barcode_as_image)
        supportEdgeToEdge()
        initToolbar()
        initFormatSpinner()
        initSaveButton()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (permissionsHelper.areAllPermissionsGranted(grantResults)) {
            saveBarcode()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    private fun supportEdgeToEdge() {
        findViewById<CoordinatorLayout>(R.id.root_view).applySystemWindowInsets(
            applyTop = true,
            applyBottom = true
        )
    }

    private fun initToolbar() {
        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }
    }

    private fun initFormatSpinner() {
        findViewById<Spinner>(R.id.spinner_save_as).adapter = ArrayAdapter.createFromResource(
            this, R.array.activity_save_barcode_as_image_formats, R.layout.item_spinner
        ).apply {
            setDropDownViewResource(R.layout.item_spinner_dropdown)
        }
    }

    private fun initSaveButton() {
        findViewById<Button>(R.id.button_save).setOnClickListener {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        permissionsHelper.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSIONS_CODE)
    }

    private fun saveBarcode() {
        val saveFunc = when (findViewById<Spinner>(R.id.spinner_save_as).selectedItemPosition) {
            0 -> {
                barcodeImageGenerator
                    .generateBitmapAsync(barcode, 640, 640, 2)
                    .flatMapCompletable {
                        barcodeImageSaver.savePngImageToPublicDirectory(
                            this,
                            it,
                            barcode
                        )
                    }
            }

            1 -> {
                barcodeImageGenerator
                    .generateSvgAsync(barcode, 640, 640, 2)
                    .flatMapCompletable {
                        barcodeImageSaver.saveSvgImageToPublicDirectory(
                            this,
                            it,
                            barcode
                        )
                    }
            }

            else -> return
        }

        showLoading(true)

        saveFunc
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { showBarcodeSaved() },
                { error ->
                    showLoading(false)
                    showError(error)
                }
            )
            .addTo(disposable)
    }

    private fun showLoading(isLoading: Boolean) {
        findViewById<ProgressBar>(R.id.progress_bar_loading).isVisible = isLoading
        findViewById<NestedScrollView>(R.id.scroll_view).isVisible = isLoading.not()
    }

    private fun showBarcodeSaved() {
        Toast.makeText(
            this,
            R.string.activity_save_barcode_as_image_file_name_saved,
            Toast.LENGTH_LONG
        ).show()
        finish()
    }
}