package com.full.qr.scanner.top.secure.no.extension

import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import com.full.qr.scanner.top.secure.no.feature.common.dialog.ErrorDialogFragment

val Fragment.packageManager: PackageManager
    get() = requireContext().packageManager

fun Fragment.showError(error: Throwable?) {
    val errorDialog = ErrorDialogFragment.newInstance(requireContext(), error)
    errorDialog.show(childFragmentManager, "")
}
