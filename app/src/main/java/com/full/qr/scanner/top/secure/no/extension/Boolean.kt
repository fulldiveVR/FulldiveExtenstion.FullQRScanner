package com.full.qr.scanner.top.secure.no.extension

fun Boolean?.orFalse(): Boolean {
    return this ?: false
}