package com.full.qr.scanner.pvt.no.feature.tabs.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.full.qr.scanner.pvt.no.BuildConfig
import com.full.qr.scanner.pvt.no.R
import com.full.qr.scanner.pvt.no.di.barcodeDatabase
import com.full.qr.scanner.pvt.no.di.settings
import com.full.qr.scanner.pvt.no.extension.applySystemWindowInsets
import com.full.qr.scanner.pvt.no.extension.packageManager
import com.full.qr.scanner.pvt.no.extension.showError
import com.full.qr.scanner.pvt.no.feature.common.dialog.DeleteConfirmationDialogFragment
import com.full.qr.scanner.pvt.no.feature.tabs.settings.camera.ChooseCameraActivity
import com.full.qr.scanner.pvt.no.feature.tabs.settings.formats.SupportedFormatsActivity
import com.full.qr.scanner.pvt.no.feature.tabs.settings.permissions.AllPermissionsActivity
import com.full.qr.scanner.pvt.no.feature.tabs.settings.search.ChooseSearchEngineActivity
import com.full.qr.scanner.pvt.no.feature.tabs.settings.theme.ChooseThemeActivity
import com.fulldive.startapppopups.PopupManager
import com.fulldive.startapppopups.donation.DonationManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : Fragment(), DeleteConfirmationDialogFragment.Listener {
    private val disposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        supportEdgeToEdge()
    }

    override fun onResume() {
        super.onResume()
        handleButtonCheckedChanged()
        handleButtonClicks()
        showSettings()
        showAppVersion()
    }

    override fun onDeleteConfirmed() {
        clearHistory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.clear()
    }

    fun supportEdgeToEdge() {
        app_bar_layout.applySystemWindowInsets(applyTop = true)
    }

    private fun handleButtonCheckedChanged() {
        button_inverse_barcode_colors_in_dark_theme.setCheckedChangedListener { settings.areBarcodeColorsInversed = it }
        button_open_links_automatically.setCheckedChangedListener { settings.openLinksAutomatically = it }
        button_copy_to_clipboard.setCheckedChangedListener { settings.copyToClipboard = it }
        button_simple_auto_focus.setCheckedChangedListener { settings.simpleAutoFocus = it }
        button_flashlight.setCheckedChangedListener { settings.flash = it }
        button_vibrate.setCheckedChangedListener { settings.vibrate = it }
        button_continuous_scanning.setCheckedChangedListener { settings.continuousScanning = it }
        button_confirm_scans_manually.setCheckedChangedListener { settings.confirmScansManually = it }
        button_save_scanned_barcodes.setCheckedChangedListener { settings.saveScannedBarcodesToHistory = it }
        button_save_created_barcodes.setCheckedChangedListener { settings.saveCreatedBarcodesToHistory = it }
        button_do_not_save_duplicates.setCheckedChangedListener { settings.doNotSaveDuplicates = it }
        button_enable_error_reports.setCheckedChangedListener { settings.areErrorReportsEnabled = it }
    }

    private fun handleButtonClicks() {
        button_choose_theme.setOnClickListener { ChooseThemeActivity.start(requireActivity()) }
        button_choose_camera.setOnClickListener { ChooseCameraActivity.start(requireActivity()) }
        button_select_supported_formats.setOnClickListener { SupportedFormatsActivity.start(requireActivity()) }
        button_clear_history.setOnClickListener { showDeleteHistoryConfirmationDialog() }
        button_choose_search_engine.setOnClickListener { ChooseSearchEngineActivity.start(requireContext()) }
        button_donate.setOnClickListener {

//            DonationManager.purchaseFromSettings(
//                    Activity,
//                    onPurchased = {
//                        PopupManager().showDonationSuccess(context)
//                    }
//            )
//            activity?.let { it1 -> PopupManager().onAppStarted(it1, BuildConfig.APPLICATION_ID) }

            DonationManager.purchaseFromSettings(
                    requireActivity(),
                    onPurchased = {
                        PopupManager().showDonationSuccess(requireContext())
                    }
            )


//            Toast.makeText(context, "Click", Toast.LENGTH_LONG).show()

        }
        button_permissions.setOnClickListener { AllPermissionsActivity.start(requireActivity()) }
        button_check_updates.setOnClickListener { showAppInMarket() }
        button_source_code.setOnClickListener { showSourceCode() }
    }

    private fun clearHistory() {
        button_clear_history.isEnabled = false

        barcodeDatabase.deleteAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    button_clear_history.isEnabled = true
                },
                { error ->
                    button_clear_history.isEnabled = true
                    showError(error)
                }
            )
            .addTo(disposable)
    }

    private fun showSettings() {
        settings.apply {
            button_inverse_barcode_colors_in_dark_theme.isChecked = areBarcodeColorsInversed
            button_open_links_automatically.isChecked = openLinksAutomatically
            button_copy_to_clipboard.isChecked = copyToClipboard
            button_simple_auto_focus.isChecked = simpleAutoFocus
            button_flashlight.isChecked = flash
            button_vibrate.isChecked = vibrate
            button_continuous_scanning.isChecked = continuousScanning
            button_confirm_scans_manually.isChecked = confirmScansManually
            button_save_scanned_barcodes.isChecked = saveScannedBarcodesToHistory
            button_save_created_barcodes.isChecked = saveCreatedBarcodesToHistory
            button_do_not_save_duplicates.isChecked = doNotSaveDuplicates
            button_enable_error_reports.isChecked = areErrorReportsEnabled
        }
    }

    private fun showDeleteHistoryConfirmationDialog() {
        val dialog = DeleteConfirmationDialogFragment.newInstance(R.string.dialog_delete_clear_history_message)
        dialog.show(childFragmentManager, "")
    }

    private fun showAppInMarket() {
        val uri = Uri.parse("market://details?id=" + requireContext().packageName)
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        }
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun showSourceCode() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/wewewe718/QrAndBarcodeScanner"))
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun showAppVersion() {
        button_app_version.hint = BuildConfig.VERSION_NAME
    }
}