package com.full.qr.scanner.top.secure.no.feature.tabs.create.barcode

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.full.qr.scanner.top.secure.no.R
import com.full.qr.scanner.top.secure.no.extension.applySystemWindowInsets
import com.full.qr.scanner.top.secure.no.feature.BaseActivity
import com.full.qr.scanner.top.secure.no.feature.common.view.IconButtonWithDelimiter
import com.full.qr.scanner.top.secure.no.feature.tabs.create.CreateBarcodeActivity
import com.google.zxing.BarcodeFormat

class CreateBarcodeAllActivity : BaseActivity() {

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, CreateBarcodeAllActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_barcode_all)
        supportEdgeToEdge()
        handleToolbarBackClicked()
        handleButtonsClicked()
    }

    private fun supportEdgeToEdge() {
        findViewById<CoordinatorLayout>(R.id.root_view).applySystemWindowInsets(
            applyTop = true,
            applyBottom = true
        )
    }

    private fun handleToolbarBackClicked() {
        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }
    }

    private fun handleButtonsClicked() {
        findViewById<IconButtonWithDelimiter>(R.id.button_data_matrix).setOnClickListener {
            CreateBarcodeActivity.start(
                this,
                BarcodeFormat.DATA_MATRIX
            )
        }
        findViewById<IconButtonWithDelimiter>(R.id.button_aztec).setOnClickListener { CreateBarcodeActivity.start(this, BarcodeFormat.AZTEC) }
        findViewById<IconButtonWithDelimiter>(R.id.button_pdf_417) .setOnClickListener {
            CreateBarcodeActivity.start(
                this,
                BarcodeFormat.PDF_417
            )
        }
        findViewById<IconButtonWithDelimiter>(R.id.button_codabar) .setOnClickListener {
            CreateBarcodeActivity.start(
                this,
                BarcodeFormat.CODABAR
            )
        }
        findViewById<IconButtonWithDelimiter>(R.id.button_code_39) .setOnClickListener {
            CreateBarcodeActivity.start(
                this,
                BarcodeFormat.CODE_39
            )
        }
        findViewById<IconButtonWithDelimiter>(R.id.button_code_93).setOnClickListener {
            CreateBarcodeActivity.start(
                this,
                BarcodeFormat.CODE_93
            )
        }
        findViewById<IconButtonWithDelimiter>(R.id.button_code_128).setOnClickListener {
            CreateBarcodeActivity.start(
                this,
                BarcodeFormat.CODE_128
            )
        }
        findViewById<IconButtonWithDelimiter>(R.id.button_ean_8).setOnClickListener { CreateBarcodeActivity.start(this, BarcodeFormat.EAN_8) }
        findViewById<IconButtonWithDelimiter>(R.id.button_ean_13).setOnClickListener { CreateBarcodeActivity.start(this, BarcodeFormat.EAN_13) }
        findViewById<IconButtonWithDelimiter>(R.id.button_itf_14).setOnClickListener { CreateBarcodeActivity.start(this, BarcodeFormat.ITF) }
        findViewById<IconButtonWithDelimiter>(R.id.button_upc_a).setOnClickListener { CreateBarcodeActivity.start(this, BarcodeFormat.UPC_A) }
        findViewById<IconButtonWithDelimiter>(R.id.button_upc_e).setOnClickListener { CreateBarcodeActivity.start(this, BarcodeFormat.UPC_E) }
    }
}