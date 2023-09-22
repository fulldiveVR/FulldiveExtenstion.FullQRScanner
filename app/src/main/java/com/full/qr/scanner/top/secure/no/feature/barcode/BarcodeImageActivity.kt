package com.full.qr.scanner.top.secure.no.feature.barcode

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import com.full.qr.scanner.top.secure.no.R
import com.full.qr.scanner.top.secure.no.di.barcodeImageGenerator
import com.full.qr.scanner.top.secure.no.di.settings
import com.full.qr.scanner.top.secure.no.extension.applySystemWindowInsets
import com.full.qr.scanner.top.secure.no.extension.toStringId
import com.full.qr.scanner.top.secure.no.extension.unsafeLazy
import com.full.qr.scanner.top.secure.no.feature.BaseActivity
import com.full.qr.scanner.top.secure.no.model.Barcode
import com.full.qr.scanner.top.secure.no.usecase.Logger
import java.text.SimpleDateFormat
import java.util.*

class BarcodeImageActivity : BaseActivity() {

    companion object {
        private const val BARCODE_KEY = "BARCODE_KEY"

        fun start(context: Context, barcode: Barcode) {
            val intent = Intent(context, BarcodeImageActivity::class.java)
            intent.putExtra(BARCODE_KEY, barcode)
            context.startActivity(intent)
        }
    }

    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)
    private val barcode by unsafeLazy {
        intent?.getSerializableExtra(BARCODE_KEY) as? Barcode
            ?: throw IllegalArgumentException("No barcode passed")
    }
    private var originalBrightness: Float = 0.5f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_image)
        supportEdgeToEdge()
        saveOriginalBrightness()
        handleToolbarBackPressed()
        handleToolbarMenuItemClicked()
        showMenu()
        showBarcode()
    }

    private fun supportEdgeToEdge() {
        findViewById<CoordinatorLayout>(R.id.root_view)
        .applySystemWindowInsets(applyTop = true, applyBottom = true)
    }

    private fun saveOriginalBrightness() {
        originalBrightness = window.attributes.screenBrightness
    }

    private fun handleToolbarBackPressed() {
        findViewById<Toolbar>(R.id.toolbar)?.let { toolbar ->
            toolbar.setNavigationOnClickListener {
                finish()
            }
        }
    }

    private fun handleToolbarMenuItemClicked() {
        findViewById<Toolbar>(R.id.toolbar)?.let { toolbar ->
            toolbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.item_increase_brightness -> {
                        increaseBrightnessToMax()
                        toolbar.menu.apply {
                            findItem(R.id.item_increase_brightness).isVisible = false
                            findItem(R.id.item_decrease_brightness).isVisible = true
                        }
                    }

                    R.id.item_decrease_brightness -> {
                        restoreOriginalBrightness()
                        toolbar.menu.apply {
                            findItem(R.id.item_decrease_brightness).isVisible = false
                            findItem(R.id.item_increase_brightness).isVisible = true
                        }
                    }
                }
                return@setOnMenuItemClickListener true
            }
        }
    }

    private fun showMenu() {
        findViewById<Toolbar>(R.id.toolbar)?.let { toolbar ->
            toolbar.inflateMenu(R.menu.menu_barcode_image)
        }
    }

    private fun showBarcode() {
        showBarcodeImage()
        showBarcodeDate()
        showBarcodeFormat()
        showBarcodeText()
    }

    private fun showBarcodeImage() {
        try {
            val bitmap = barcodeImageGenerator.generateBitmap(
                barcode,
                2000,
                2000,
                0,
                settings.barcodeContentColor,
                settings.barcodeBackgroundColor
            )
            findViewById<ImageView>(R.id.image_view_barcode)?.let {image_view_barcode->
                image_view_barcode.setImageBitmap(bitmap)
                image_view_barcode.setBackgroundColor(settings.barcodeBackgroundColor)
                findViewById<FrameLayout>(R.id.layout_barcode_image_background).setBackgroundColor(settings.barcodeBackgroundColor)

                if (settings.isDarkTheme.not() || settings.areBarcodeColorsInversed) {
                    findViewById<FrameLayout>(R.id.layout_barcode_image_background).setPadding(0, 0, 0, 0)
                }
            }
        } catch (ex: Exception) {
            Logger.log(ex)
            findViewById<ImageView>(R.id.image_view_barcode).isVisible = false
        }
    }

    private fun showBarcodeDate() {
        findViewById<TextView>(R.id.text_view_date)?.text = dateFormatter.format(barcode.date)
    }

    private fun showBarcodeFormat() {
        val format = barcode.format.toStringId()
        findViewById<Toolbar>(R.id.toolbar)?.let { toolbar ->
            toolbar.setTitle(format)
        }
    }

    private fun showBarcodeText() {
        findViewById<TextView>(R.id.text_view_barcode_text)?.text = barcode.text
    }

    private fun increaseBrightnessToMax() {
        setBrightness(1.0f)
    }

    private fun restoreOriginalBrightness() {
        setBrightness(originalBrightness)
    }

    private fun setBrightness(brightness: Float) {
        window.attributes = window.attributes.apply {
            screenBrightness = brightness
        }
    }
}