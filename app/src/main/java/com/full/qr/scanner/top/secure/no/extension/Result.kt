package com.full.qr.scanner.top.secure.no.extension

import com.full.qr.scanner.top.secure.no.model.Barcode
import com.google.zxing.Result

fun Result.equalTo(barcode: Barcode?): Boolean {
    return barcodeFormat == barcode?.format && text == barcode?.text
}