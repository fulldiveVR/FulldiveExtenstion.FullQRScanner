package com.full.qr.scanner.top.secure.no.feature.tabs.create.qr

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.full.qr.scanner.top.secure.no.R
import com.full.qr.scanner.top.secure.no.feature.tabs.create.BaseCreateBarcodeFragment
import com.full.qr.scanner.top.secure.no.model.schema.App
import com.full.qr.scanner.top.secure.no.extension.showError
import com.full.qr.scanner.top.secure.no.extension.unsafeLazy
import com.full.qr.scanner.top.secure.no.model.schema.Schema
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class CreateQrCodeAppFragment : BaseCreateBarcodeFragment() {
    private val disposable = CompositeDisposable()
    private val appAdapter by unsafeLazy { AppAdapter(parentActivity) }
    private lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_qr_code_app, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentView = view
        initRecyclerView()
        loadApps()
    }

    override fun getBarcodeSchema(): Schema {
        return App.fromPackage("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.clear()
    }

    private fun initRecyclerView() {
        fragmentView.findViewById<RecyclerView>(R.id.recycler_view_apps)
            .apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = appAdapter
            }
    }

    private fun loadApps() {
        showLoading(true)

        Single.just(getApps())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { apps ->
                    showLoading(false)
                    showApps(apps)
                },
                { error ->
                    showLoading(false)
                    showError(error)
                }
            )
            .addTo(disposable)
    }

    private fun getApps(): List<ResolveInfo> {
        val mainIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        return requireContext().packageManager
            .queryIntentActivities(mainIntent, 0)
            .filter { it.activityInfo?.packageName != null }
    }

    private fun showLoading(isLoading: Boolean) {
        fragmentView.findViewById<ProgressBar>(R.id.progress_bar_loading).isVisible = isLoading
        fragmentView.findViewById<RecyclerView>(R.id.recycler_view_apps).isVisible = isLoading.not()
    }

    private fun showApps(apps: List<ResolveInfo>) {
        appAdapter.apps = apps
    }
}