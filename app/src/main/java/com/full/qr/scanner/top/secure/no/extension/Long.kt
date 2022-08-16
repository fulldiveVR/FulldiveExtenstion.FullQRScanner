package com.full.qr.scanner.top.secure.no.extension

fun Long?.orZero(): Long {
    return this ?: 0L
}