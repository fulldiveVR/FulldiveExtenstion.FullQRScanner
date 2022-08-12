package com.full.qr.scanner.pvt.no.extension

fun Boolean?.orFalse(): Boolean {
    return this ?: false
}