package com.full.qr.scanner.top.secure.no.feature.tabs.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.full.qr.scanner.top.secure.no.R
import com.full.qr.scanner.top.secure.no.extension.applySystemWindowInsets
import com.full.qr.scanner.top.secure.no.extension.clipboardManager
import com.full.qr.scanner.top.secure.no.extension.orZero
import com.full.qr.scanner.top.secure.no.feature.common.view.IconButtonWithDelimiter
import com.full.qr.scanner.top.secure.no.feature.tabs.create.barcode.CreateBarcodeAllActivity
import com.full.qr.scanner.top.secure.no.feature.tabs.create.qr.CreateQrCodeAllActivity
import com.full.qr.scanner.top.secure.no.model.schema.BarcodeSchema
import com.google.android.material.appbar.AppBarLayout
import com.google.zxing.BarcodeFormat

class CreateBarcodeFragment : Fragment() {
    private lateinit var fragmentView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_barcode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentView = view
        supportEdgeToEdge()
        handleButtonsClicked()
    }

    private fun supportEdgeToEdge() {
        fragmentView.findViewById<AppBarLayout>(R.id.app_bar_layout).applySystemWindowInsets(applyTop = true)
    }

    private fun handleButtonsClicked() {
        // QR code
        fragmentView.findViewById<IconButtonWithDelimiter>(R.id.button_clipboard).setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.OTHER, getClipboardContent()) }
        fragmentView.findViewById<IconButtonWithDelimiter>(R.id.button_text).setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.OTHER) }
        fragmentView.findViewById<IconButtonWithDelimiter>(R.id.button_url).setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.URL) }
        fragmentView.findViewById<IconButtonWithDelimiter>(R.id.button_wifi).setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.WIFI) }
        fragmentView.findViewById<IconButtonWithDelimiter>(R.id.button_location).setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.GEO) }
        fragmentView.findViewById<IconButtonWithDelimiter>(R.id.button_contact_vcard).setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.VCARD) }
        fragmentView.findViewById<IconButtonWithDelimiter>(R.id.button_show_all_qr_code).setOnClickListener { CreateQrCodeAllActivity.start(requireActivity()) }

        // Barcode
        fragmentView.findViewById<IconButtonWithDelimiter>(R.id.button_create_barcode).setOnClickListener { CreateBarcodeAllActivity.start(requireActivity()) }
    }

    private fun getClipboardContent(): String {
        val clip = requireActivity().clipboardManager?.primaryClip ?: return ""
        return when (clip.itemCount.orZero()) {
            0 -> ""
            else -> clip.getItemAt(0).text.toString()
        }
    }
}