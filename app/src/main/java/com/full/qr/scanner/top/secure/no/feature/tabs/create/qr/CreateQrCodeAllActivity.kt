package com.full.qr.scanner.top.secure.no.feature.tabs.create.qr

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.full.qr.scanner.top.secure.no.R
import com.full.qr.scanner.top.secure.no.extension.applySystemWindowInsets
import com.full.qr.scanner.top.secure.no.feature.BaseActivity
import com.full.qr.scanner.top.secure.no.feature.common.view.IconButtonWithDelimiter
import com.full.qr.scanner.top.secure.no.feature.tabs.create.CreateBarcodeActivity
import com.full.qr.scanner.top.secure.no.model.schema.BarcodeSchema
import com.google.zxing.BarcodeFormat

class CreateQrCodeAllActivity : BaseActivity() {

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, CreateQrCodeAllActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_qr_code_all)
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
        findViewById<IconButtonWithDelimiter>(R.id.button_text).setOnClickListener {
            CreateBarcodeActivity.start(
                this,
                BarcodeFormat.QR_CODE,
                BarcodeSchema.OTHER
            )
        }
        findViewById<IconButtonWithDelimiter>(R.id.button_url).setOnClickListener {
            CreateBarcodeActivity.start(
                this,
                BarcodeFormat.QR_CODE,
                BarcodeSchema.URL
            )
        }
        findViewById<IconButtonWithDelimiter>(R.id.button_wifi).setOnClickListener {
            CreateBarcodeActivity.start(
                this,
                BarcodeFormat.QR_CODE,
                BarcodeSchema.WIFI
            )
        }
        findViewById<IconButtonWithDelimiter>(R.id.button_location).setOnClickListener {
            CreateBarcodeActivity.start(
                this,
                BarcodeFormat.QR_CODE,
                BarcodeSchema.GEO
            )
        }
        findViewById<IconButtonWithDelimiter>(R.id.button_otp).setOnClickListener {
            CreateBarcodeActivity.start(
                this,
                BarcodeFormat.QR_CODE,
                BarcodeSchema.OTP_AUTH
            )
        }
        findViewById<IconButtonWithDelimiter>(R.id.button_contact_vcard).setOnClickListener {
            CreateBarcodeActivity.start(
                this,
                BarcodeFormat.QR_CODE,
                BarcodeSchema.VCARD
            )
        }
        findViewById<IconButtonWithDelimiter>(R.id.button_contact_mecard).setOnClickListener {
            CreateBarcodeActivity.start(
                this,
                BarcodeFormat.QR_CODE,
                BarcodeSchema.MECARD
            )
        }
        findViewById<IconButtonWithDelimiter>(R.id.button_event).setOnClickListener {
            CreateBarcodeActivity.start(
                this,
                BarcodeFormat.QR_CODE,
                BarcodeSchema.VEVENT
            )
        }
        findViewById<IconButtonWithDelimiter>(R.id.button_phone).setOnClickListener {
            CreateBarcodeActivity.start(
                this,
                BarcodeFormat.QR_CODE,
                BarcodeSchema.PHONE
            )
        }
        findViewById<IconButtonWithDelimiter>(R.id.button_email).setOnClickListener {
            CreateBarcodeActivity.start(
                this,
                BarcodeFormat.QR_CODE,
                BarcodeSchema.EMAIL
            )
        }
        findViewById<IconButtonWithDelimiter>(R.id.button_sms).setOnClickListener {
            CreateBarcodeActivity.start(
                this,
                BarcodeFormat.QR_CODE,
                BarcodeSchema.SMS
            )
        }
        findViewById<IconButtonWithDelimiter>(R.id.button_mms).setOnClickListener {
            CreateBarcodeActivity.start(
                this,
                BarcodeFormat.QR_CODE,
                BarcodeSchema.MMS
            )
        }
        findViewById<IconButtonWithDelimiter>(R.id.button_cryptocurrency).setOnClickListener {
            CreateBarcodeActivity.start(
                this,
                BarcodeFormat.QR_CODE,
                BarcodeSchema.CRYPTOCURRENCY
            )
        }
        findViewById<IconButtonWithDelimiter>(R.id.button_bookmark).setOnClickListener {
            CreateBarcodeActivity.start(
                this,
                BarcodeFormat.QR_CODE,
                BarcodeSchema.BOOKMARK
            )
        }
        findViewById<IconButtonWithDelimiter>(R.id.button_app).setOnClickListener {
            CreateBarcodeActivity.start(
                this,
                BarcodeFormat.QR_CODE,
                BarcodeSchema.APP
            )
        }
    }
}