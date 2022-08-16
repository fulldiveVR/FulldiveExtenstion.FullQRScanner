package com.full.qr.scanner.top.secure.no.model

import androidx.room.TypeConverters
import com.full.qr.scanner.top.secure.no.usecase.BarcodeDatabaseTypeConverter
import com.google.zxing.BarcodeFormat

@TypeConverters(BarcodeDatabaseTypeConverter::class)
data class ExportBarcode(
    val date: Long,
    val format: BarcodeFormat,
    val text: String
)