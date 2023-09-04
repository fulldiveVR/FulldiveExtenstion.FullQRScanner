package com.full.qr.scanner.top.secure.no.feature.tabs.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.full.qr.scanner.top.secure.no.R
import com.full.qr.scanner.top.secure.no.di.barcodeDatabase
import com.full.qr.scanner.top.secure.no.extension.applySystemWindowInsets
import com.full.qr.scanner.top.secure.no.extension.showError
import com.full.qr.scanner.top.secure.no.feature.common.dialog.DeleteConfirmationDialogFragment
import com.full.qr.scanner.top.secure.no.feature.tabs.history.export.ExportHistoryActivity
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers


class BarcodeHistoryFragment : Fragment(), DeleteConfirmationDialogFragment.Listener {
    private val disposable = CompositeDisposable()
    private lateinit var fragmentView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_barcode_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentView = view
        supportEdgeToEdge()
        initTabs()
        handleMenuClicked()
    }

    override fun onDeleteConfirmed() {
        clearHistory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.clear()
    }

    private fun supportEdgeToEdge() {
        fragmentView.findViewById<AppBarLayout>(R.id.app_bar_layout).applySystemWindowInsets(applyTop = true)
    }

    private fun initTabs() {
        fragmentView.findViewById<ViewPager>(R.id.view_pager).adapter = BarcodeHistoryViewPagerAdapter(requireContext(), childFragmentManager)
        fragmentView.findViewById<TabLayout>(R.id.tab_layout).setupWithViewPager(fragmentView.findViewById<ViewPager>(R.id.view_pager))
    }

    private fun handleMenuClicked() {
       fragmentView. findViewById<Toolbar>(R.id.toolbar).setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item_export_history -> navigateToExportHistoryScreen()
                R.id.item_clear_history -> showDeleteHistoryConfirmationDialog()
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun navigateToExportHistoryScreen() {
        ExportHistoryActivity.start(requireActivity())
    }

    private fun showDeleteHistoryConfirmationDialog() {
        val dialog = DeleteConfirmationDialogFragment.newInstance(R.string.dialog_delete_clear_history_message)
        dialog.show(childFragmentManager, "")
    }

    private fun clearHistory() {
        barcodeDatabase.deleteAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { },
                ::showError
            )
            .addTo(disposable)
    }
}