package com.full.qr.scanner.pvt.no.extension

fun Long?.orZero(): Long {
    return this ?: 0L
}