package com.full.qr.scanner.top.secure.no.model.schema

class Other(val text: String): Schema {
    override val schema = BarcodeSchema.OTHER
    override fun toFormattedText(): String = text
    override fun toBarcodeText(): String = text
}