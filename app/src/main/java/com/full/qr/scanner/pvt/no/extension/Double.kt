package com.full.qr.scanner.pvt.no.extension

fun Double?.orZero(): Double {
    return this ?: 0.0
}