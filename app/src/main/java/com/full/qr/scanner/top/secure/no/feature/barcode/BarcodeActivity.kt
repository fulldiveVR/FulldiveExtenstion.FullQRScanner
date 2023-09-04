package com.full.qr.scanner.top.secure.no.feature.barcode

import android.app.SearchManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.provider.Settings
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.print.PrintHelper
import com.full.qr.scanner.top.secure.no.R
import com.full.qr.scanner.top.secure.no.feature.BaseActivity
import com.full.qr.scanner.top.secure.no.feature.barcode.otp.OtpActivity
import com.full.qr.scanner.top.secure.no.feature.barcode.save.SaveBarcodeAsImageActivity
import com.full.qr.scanner.top.secure.no.feature.barcode.save.SaveBarcodeAsTextActivity
import com.full.qr.scanner.top.secure.no.feature.common.dialog.ChooseSearchEngineDialogFragment
import com.full.qr.scanner.top.secure.no.feature.common.dialog.DeleteConfirmationDialogFragment
import com.full.qr.scanner.top.secure.no.feature.common.dialog.EditBarcodeNameDialogFragment
import com.full.qr.scanner.top.secure.no.model.Barcode
import com.full.qr.scanner.top.secure.no.model.ParsedBarcode
import com.full.qr.scanner.top.secure.no.model.SearchEngine
import com.full.qr.scanner.top.secure.no.model.schema.BarcodeSchema
import com.full.qr.scanner.top.secure.no.model.schema.OtpAuth
import com.full.qr.scanner.top.secure.no.usecase.Logger
import com.full.qr.scanner.top.secure.no.usecase.save
import com.full.qr.scanner.top.secure.no.di.*
import com.full.qr.scanner.top.secure.no.extension.*
import com.full.qr.scanner.top.secure.no.feature.common.view.IconButton
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*


class BarcodeActivity : BaseActivity(), DeleteConfirmationDialogFragment.Listener,
    ChooseSearchEngineDialogFragment.Listener, EditBarcodeNameDialogFragment.Listener {

    companion object {
        private const val BARCODE_KEY = "BARCODE_KEY"
        private const val IS_CREATED = "IS_CREATED"

        fun start(context: Context, barcode: Barcode, isCreated: Boolean = false) {
            val intent = Intent(context, BarcodeActivity::class.java).apply {
                putExtra(BARCODE_KEY, barcode)
                putExtra(IS_CREATED, isCreated)
            }
            context.startActivity(intent)
        }
    }

    private val disposable = CompositeDisposable()
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)

    private val originalBarcode by unsafeLazy {
        intent?.getSerializableExtra(BARCODE_KEY) as? Barcode
            ?: throw IllegalArgumentException("No barcode passed")
    }

    private val isCreated by unsafeLazy {
        intent?.getBooleanExtra(IS_CREATED, false).orFalse()
    }

    private val barcode by unsafeLazy {
        ParsedBarcode(originalBarcode)
    }

    private val clipboardManager by unsafeLazy {
        getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    }

    private var originalBrightness: Float = 0.5f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_barcode)

        supportEdgeToEdge()
        saveOriginalBrightness()
        applySettings()

        handleToolbarBackPressed()
        handleToolbarMenuClicked()
        handleButtonsClicked()

        showBarcode()
        showOrHideButtons()
        showButtonText()
    }

    override fun onDeleteConfirmed() {
        deleteBarcode()
    }

    override fun onNameConfirmed(name: String) {
        updateBarcodeName(name)
    }

    override fun onSearchEngineSelected(searchEngine: SearchEngine) {
        performWebSearchUsingSearchEngine(searchEngine)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }


    private fun supportEdgeToEdge() {
        findViewById<CoordinatorLayout>(R.id.root_view)?.applySystemWindowInsets(
            applyTop = true,
            applyBottom = true
        )
    }

    private fun saveOriginalBrightness() {
        originalBrightness = window.attributes.screenBrightness
    }

    private fun applySettings() {
        if (settings.copyToClipboard) {
            copyToClipboard(barcode.text)
        }

        if (settings.openLinksAutomatically.not() || isCreated) {
            return
        }

        when (barcode.schema) {
            BarcodeSchema.APP -> openInAppMarket()
            BarcodeSchema.BOOKMARK -> saveBookmark()
            BarcodeSchema.CRYPTOCURRENCY -> openBitcoinUrl()
            BarcodeSchema.EMAIL -> sendEmail(barcode.email)
            BarcodeSchema.GEO -> showLocation()
            BarcodeSchema.GOOGLE_MAPS -> showLocation()
            BarcodeSchema.MMS -> sendSmsOrMms(barcode.phone)
            BarcodeSchema.MECARD -> addToContacts()
            BarcodeSchema.OTP_AUTH -> openOtpInOtherApp()
            BarcodeSchema.PHONE -> callPhone(barcode.phone)
            BarcodeSchema.SMS -> sendSmsOrMms(barcode.phone)
            BarcodeSchema.URL -> openLink()
            BarcodeSchema.VEVENT -> addToCalendar()
            BarcodeSchema.VCARD -> addToContacts()
            BarcodeSchema.WIFI -> connectToWifi()
            BarcodeSchema.YOUTUBE -> openInYoutube()
            BarcodeSchema.NZCOVIDTRACER -> openLink()
            BarcodeSchema.BOARDINGPASS -> return
            else -> return
        }
    }


    private fun handleToolbarBackPressed() {
        findViewById<Toolbar>(R.id.toolbar)?.let { toolbar ->
            toolbar.setNavigationOnClickListener {
                finish()
            }
        }
    }

    private fun handleToolbarMenuClicked() {
        findViewById<Toolbar>(R.id.toolbar)?.let { toolbar ->
            toolbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.item_increase_brightness -> {
                        increaseBrightnessToMax()
                        toolbar.menu.findItem(R.id.item_increase_brightness).isVisible = false
                        toolbar.menu.findItem(R.id.item_decrease_brightness).isVisible = true
                    }

                    R.id.item_decrease_brightness -> {
                        restoreOriginalBrightness()
                        toolbar.menu.findItem(R.id.item_increase_brightness).isVisible = true
                        toolbar.menu.findItem(R.id.item_decrease_brightness).isVisible = false
                    }

                    R.id.item_add_to_favorites -> toggleIsFavorite()
                    R.id.item_show_barcode_image -> navigateToBarcodeImageActivity()
                    R.id.item_save -> saveBarcode()
                    R.id.item_delete -> showDeleteBarcodeConfirmationDialog()
                }
                return@setOnMenuItemClickListener true
            }
        }
    }

    private fun handleButtonsClicked() {
        findViewById<ImageView>(R.id.button_edit_name)?.setOnClickListener { showEditBarcodeNameDialog() }
        findViewById<IconButton>(R.id.button_search_on_web)?.setOnClickListener { searchBarcodeTextOnInternet() }
        findViewById<IconButton>(R.id.button_add_to_calendar)?.setOnClickListener { addToCalendar() }
        findViewById<IconButton>(R.id.button_add_to_contacts)?.setOnClickListener { addToContacts() }
        findViewById<IconButton>(R.id.button_show_location)?.setOnClickListener { showLocation() }
        findViewById<IconButton>(R.id.button_connect_to_wifi)?.setOnClickListener { connectToWifi() }
        findViewById<IconButton>(R.id.button_open_wifi_settings)?.setOnClickListener { openWifiSettings() }
        findViewById<IconButton>(R.id.button_copy_network_name)?.setOnClickListener { copyNetworkNameToClipboard() }
        findViewById<IconButton>(R.id.button_copy_network_password)?.setOnClickListener { copyNetworkPasswordToClipboard() }
        findViewById<IconButton>(R.id.button_open_app)?.setOnClickListener { openApp() }
        findViewById<IconButton>(R.id.button_open_in_app_market)?.setOnClickListener { openInAppMarket() }
        findViewById<IconButton>(R.id.button_open_in_youtube)?.setOnClickListener { openInYoutube() }
        findViewById<IconButton>(R.id.button_show_otp)?.setOnClickListener { showOtp() }
        findViewById<IconButton>(R.id.button_open_otp)?.setOnClickListener { openOtpInOtherApp() }
        findViewById<IconButton>(R.id.button_open_bitcoin_uri)?.setOnClickListener { openBitcoinUrl() }
        findViewById<IconButton>(R.id.button_open_link)?.setOnClickListener { openLink() }
        findViewById<IconButton>(R.id.button_save_bookmark)?.setOnClickListener { saveBookmark() }

        findViewById<IconButton>(R.id.button_call_phone_1)?.setOnClickListener { callPhone(barcode.phone) }
        findViewById<IconButton>(R.id.button_call_phone_2)?.setOnClickListener { callPhone(barcode.secondaryPhone) }
        findViewById<IconButton>(R.id.button_call_phone_3)?.setOnClickListener { callPhone(barcode.tertiaryPhone) }

        findViewById<IconButton>(R.id.button_send_sms_or_mms_1)?.setOnClickListener {
            sendSmsOrMms(
                barcode.phone
            )
        }
        findViewById<IconButton>(R.id.button_send_sms_or_mms_2)?.setOnClickListener {
            sendSmsOrMms(
                barcode.secondaryPhone
            )
        }
        findViewById<IconButton>(R.id.button_send_sms_or_mms_3)?.setOnClickListener {
            sendSmsOrMms(
                barcode.tertiaryPhone
            )
        }

        findViewById<IconButton>(R.id.button_send_email_1)?.setOnClickListener { sendEmail(barcode.email) }
        findViewById<IconButton>(R.id.button_send_email_2)?.setOnClickListener { sendEmail(barcode.secondaryEmail) }
        findViewById<IconButton>(R.id.button_send_email_3)?.setOnClickListener { sendEmail(barcode.tertiaryEmail) }

        findViewById<IconButton>(R.id.button_share_as_text)?.setOnClickListener { shareBarcodeAsText() }
        findViewById<IconButton>(R.id.button_copy)?.setOnClickListener { copyBarcodeTextToClipboard() }
        findViewById<IconButton>(R.id.button_search)?.setOnClickListener { searchBarcodeTextOnInternet() }
        findViewById<IconButton>(R.id.button_save_as_text)?.setOnClickListener { navigateToSaveBarcodeAsTextActivity() }
        findViewById<IconButton>(R.id.button_share_as_image)?.setOnClickListener { shareBarcodeAsImage() }
        findViewById<IconButton>(R.id.button_save_as_image)?.setOnClickListener { navigateToSaveBarcodeAsImageActivity() }
        findViewById<IconButton>(R.id.button_print)?.setOnClickListener { printBarcode() }
    }


    private fun toggleIsFavorite() {
        val newBarcode = originalBarcode.copy(isFavorite = barcode.isFavorite.not())

        barcodeDatabase.save(newBarcode)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    barcode.isFavorite = newBarcode.isFavorite
                    showBarcodeIsFavorite(newBarcode.isFavorite)
                },
                {}
            )
            .addTo(disposable)
    }

    private fun updateBarcodeName(name: String) {
        if (name.isBlank()) {
            return
        }

        val newBarcode = originalBarcode.copy(
            id = barcode.id,
            name = name
        )

        barcodeDatabase.save(newBarcode)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    barcode.name = name
                    showBarcodeName(name)
                },
                ::showError
            )
            .addTo(disposable)
    }

    private fun saveBarcode() {
        findViewById<Toolbar>(R.id.toolbar)?.let { toolbar ->
            toolbar?.menu?.findItem(R.id.item_save)?.isVisible = false

            barcodeDatabase.save(originalBarcode, settings.doNotSaveDuplicates)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { id ->
                        barcode.id = id
                        findViewById<ImageView>(R.id.button_edit_name).isVisible = true
                        toolbar?.menu?.findItem(R.id.item_delete)?.isVisible = true
                    },
                    { error ->
                        toolbar?.menu?.findItem(R.id.item_save)?.isVisible = true
                        showError(error)
                    }
                )
                .addTo(disposable)
        }
    }

    private fun deleteBarcode() {
        showLoading(true)

        barcodeDatabase.delete(barcode.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { finish() },
                { error ->
                    showLoading(false)
                    showError(error)
                }
            )
            .addTo(disposable)
    }

    private fun addToCalendar() {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, barcode.eventSummary)
            putExtra(CalendarContract.Events.DESCRIPTION, barcode.eventDescription)
            putExtra(CalendarContract.Events.EVENT_LOCATION, barcode.eventLocation)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, barcode.eventStartDate)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, barcode.eventEndDate)
        }
        startActivityIfExists(intent)
    }

    private fun addToContacts() {
        val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
            type = ContactsContract.Contacts.CONTENT_TYPE

            val fullName = "${barcode.firstName.orEmpty()} ${barcode.lastName.orEmpty()}"
            putExtra(ContactsContract.Intents.Insert.NAME, fullName)
            putExtra(ContactsContract.Intents.Insert.COMPANY, barcode.organization.orEmpty())
            putExtra(ContactsContract.Intents.Insert.JOB_TITLE, barcode.jobTitle.orEmpty())

            putExtra(ContactsContract.Intents.Insert.PHONE, barcode.phone.orEmpty())
            putExtra(
                ContactsContract.Intents.Insert.PHONE_TYPE,
                barcode.phoneType.orEmpty().toPhoneType()
            )

            putExtra(
                ContactsContract.Intents.Insert.SECONDARY_PHONE,
                barcode.secondaryPhone.orEmpty()
            )
            putExtra(
                ContactsContract.Intents.Insert.SECONDARY_PHONE_TYPE,
                barcode.secondaryPhoneType.orEmpty().toPhoneType()
            )

            putExtra(
                ContactsContract.Intents.Insert.TERTIARY_PHONE,
                barcode.tertiaryPhone.orEmpty()
            )
            putExtra(
                ContactsContract.Intents.Insert.TERTIARY_PHONE_TYPE,
                barcode.tertiaryPhoneType.orEmpty().toPhoneType()
            )

            putExtra(ContactsContract.Intents.Insert.EMAIL, barcode.email.orEmpty())
            putExtra(
                ContactsContract.Intents.Insert.EMAIL_TYPE,
                barcode.emailType.orEmpty().toEmailType()
            )

            putExtra(
                ContactsContract.Intents.Insert.SECONDARY_EMAIL,
                barcode.secondaryEmail.orEmpty()
            )
            putExtra(
                ContactsContract.Intents.Insert.SECONDARY_EMAIL_TYPE,
                barcode.secondaryEmailType.orEmpty().toEmailType()
            )

            putExtra(
                ContactsContract.Intents.Insert.TERTIARY_EMAIL,
                barcode.tertiaryEmail.orEmpty()
            )
            putExtra(
                ContactsContract.Intents.Insert.TERTIARY_EMAIL_TYPE,
                barcode.tertiaryEmailType.orEmpty().toEmailType()
            )

            putExtra(ContactsContract.Intents.Insert.NOTES, barcode.note.orEmpty())
        }
        startActivityIfExists(intent)
    }

    private fun callPhone(phone: String?) {
        val phoneUri = "tel:${phone.orEmpty()}"
        startActivityIfExists(Intent.ACTION_DIAL, phoneUri)
    }

    private fun sendSmsOrMms(phone: String?) {
        val uri = Uri.parse("sms:${phone.orEmpty()}")
        val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra("sms_body", barcode.smsBody.orEmpty())
        }
        startActivityIfExists(intent)
    }

    private fun sendEmail(email: String?) {
        val uri = Uri.parse("mailto:${email.orEmpty()}")
        val intent = Intent(Intent.ACTION_SEND, uri).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email.orEmpty()))
            putExtra(Intent.EXTRA_SUBJECT, barcode.emailSubject.orEmpty())
            putExtra(Intent.EXTRA_TEXT, barcode.emailBody.orEmpty())
        }
        startActivityIfExists(intent)
    }

    private fun showLocation() {
        startActivityIfExists(Intent.ACTION_VIEW, barcode.geoUri.orEmpty())
    }

    private fun connectToWifi() {
        showConnectToWifiButtonEnabled(false)

        wifiConnector
            .connect(
                this,
                barcode.networkAuthType.orEmpty(),
                barcode.networkName.orEmpty(),
                barcode.networkPassword.orEmpty(),
                barcode.isHidden.orFalse(),
                barcode.anonymousIdentity.orEmpty(),
                barcode.identity.orEmpty(),
                barcode.eapMethod.orEmpty(),
                barcode.phase2Method.orEmpty()
            )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    showConnectToWifiButtonEnabled(true)
                    showToast(R.string.activity_barcode_connecting_to_wifi)
                },
                { error ->
                    showConnectToWifiButtonEnabled(true)
                    showError(error)
                }
            )
            .addTo(disposable)
    }

    private fun openWifiSettings() {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        startActivityIfExists(intent)
    }

    private fun copyNetworkNameToClipboard() {
        copyToClipboard(barcode.networkName.orEmpty())
        showToast(R.string.activity_barcode_copied)
    }

    private fun copyNetworkPasswordToClipboard() {
        copyToClipboard(barcode.networkPassword.orEmpty())
        showToast(R.string.activity_barcode_copied)
    }

    private fun openApp() {
        val intent = packageManager?.getLaunchIntentForPackage(barcode.appPackage.orEmpty())
        if (intent != null) {
            startActivityIfExists(intent)
        }
    }

    private fun openInAppMarket() {
        startActivityIfExists(Intent.ACTION_VIEW, barcode.appMarketUrl.orEmpty())
    }

    private fun openInYoutube() {
        startActivityIfExists(Intent.ACTION_VIEW, barcode.youtubeUrl.orEmpty())
    }

    private fun showOtp() {
        val otp = OtpAuth.parse(barcode.otpUrl.orEmpty()) ?: return
        OtpActivity.start(this, otp)
    }

    private fun openOtpInOtherApp() {
        startActivityIfExists(Intent.ACTION_VIEW, barcode.otpUrl.orEmpty())
    }

    private fun openBitcoinUrl() {
        startActivityIfExists(Intent.ACTION_VIEW, barcode.bitcoinUri.orEmpty())
    }

    private fun openLink() {
        startActivityIfExists(Intent.ACTION_VIEW, barcode.url.orEmpty())
    }

    private fun saveBookmark() {
        val intent = Intent(Intent.ACTION_INSERT, Uri.parse("content://browser/bookmarks")).apply {
            putExtra("title", barcode.bookmarkTitle.orEmpty())
            putExtra("url", barcode.url.orEmpty())
        }
        startActivityIfExists(intent)
    }

    private fun shareBarcodeAsText() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, barcode.text)
        }
        startActivityIfExists(intent)
    }

    private fun copyBarcodeTextToClipboard() {
        copyToClipboard(barcode.text)
        showToast(R.string.activity_barcode_copied)
    }

    private fun searchBarcodeTextOnInternet() {
        val searchEngine = settings.searchEngine
        when (searchEngine) {
            SearchEngine.NONE -> performWebSearch()
            SearchEngine.ASK_EVERY_TIME -> showSearchEnginesDialog()
            else -> performWebSearchUsingSearchEngine(searchEngine)
        }
    }

    private fun performWebSearch() {
        val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
            putExtra(SearchManager.QUERY, barcode.text)
        }
        startActivityIfExists(intent)
    }

    private fun performWebSearchUsingSearchEngine(searchEngine: SearchEngine) {
        val url = searchEngine.templateUrl + barcode.text
        startActivityIfExists(Intent.ACTION_VIEW, url)
    }

    private fun shareBarcodeAsImage() {
        val imageUri = try {
            val image = barcodeImageGenerator.generateBitmap(originalBarcode, 200, 200, 1)
            barcodeImageSaver.saveImageToCache(this, image, barcode)
        } catch (ex: Exception) {
            Logger.log(ex)
            showError(ex)
            return
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivityIfExists(intent)
    }

    private fun printBarcode() {
        val barcodeImage = try {
            barcodeImageGenerator.generateBitmap(originalBarcode, 1000, 1000, 3)
        } catch (ex: Exception) {
            Logger.log(ex)
            showError(ex)
            return
        }

        PrintHelper(this).apply {
            scaleMode = PrintHelper.SCALE_MODE_FIT
            printBitmap("${barcode.format}_${barcode.schema}_${barcode.date}", barcodeImage)
        }
    }

    private fun navigateToBarcodeImageActivity() {
        BarcodeImageActivity.start(this, originalBarcode)
    }

    private fun navigateToSaveBarcodeAsTextActivity() {
        SaveBarcodeAsTextActivity.start(this, originalBarcode)
    }

    private fun navigateToSaveBarcodeAsImageActivity() {
        SaveBarcodeAsImageActivity.start(this, originalBarcode)
    }


    private fun showBarcode() {
        showBarcodeMenuIfNeeded()
        showBarcodeIsFavorite()
        showBarcodeImageIfNeeded()
        showBarcodeDate()
        showBarcodeFormat()
        showBarcodeName()
        showBarcodeText()
        showBarcodeCountry()
    }

    private fun showBarcodeMenuIfNeeded() {
        findViewById<Toolbar>(R.id.toolbar)?.let { toolbar ->
            toolbar.inflateMenu(R.menu.menu_barcode)
            toolbar.menu.apply {
                findItem(R.id.item_increase_brightness).isVisible = isCreated
                findItem(R.id.item_add_to_favorites)?.isVisible = barcode.isInDb
                findItem(R.id.item_show_barcode_image)?.isVisible = isCreated.not()
                findItem(R.id.item_save)?.isVisible = barcode.isInDb.not()
                findItem(R.id.item_delete)?.isVisible = barcode.isInDb
            }
        }
    }

    private fun showBarcodeIsFavorite() {
        showBarcodeIsFavorite(barcode.isFavorite)
    }

    private fun showBarcodeIsFavorite(isFavorite: Boolean) {
        val iconId = if (isFavorite) {
            R.drawable.ic_favorite_checked
        } else {
            R.drawable.ic_favorite_unchecked
        }
        findViewById<Toolbar>(R.id.toolbar)?.let { toolbar ->
            toolbar.menu?.findItem(R.id.item_add_to_favorites)?.icon =
                ContextCompat.getDrawable(this, iconId)
        }
    }

    private fun showBarcodeImageIfNeeded() {
        if (isCreated) {
            showBarcodeImage()
        }
    }

    private fun showBarcodeImage() {
        try {
            val bitmap = barcodeImageGenerator.generateBitmap(
                originalBarcode,
                2000,
                2000,
                0,
                settings.barcodeContentColor,
                settings.barcodeBackgroundColor
            )

            findViewById<FrameLayout>(R.id.layout_barcode_image_background).isVisible = true
            findViewById<ImageView>(R.id.image_view_barcode)?.let { image_view_barcode ->
                image_view_barcode.isVisible = true
                image_view_barcode.setImageBitmap(bitmap)
                image_view_barcode.setBackgroundColor(settings.barcodeBackgroundColor)
            }

            findViewById<FrameLayout>(R.id.layout_barcode_image_background).setBackgroundColor(
                settings.barcodeBackgroundColor
            )

            if (settings.isDarkTheme.not() || settings.areBarcodeColorsInversed) {
                findViewById<FrameLayout>(R.id.layout_barcode_image_background).setPadding(
                    0,
                    0,
                    0,
                    0
                )
            }
        } catch (ex: Exception) {
            Logger.log(ex)
            findViewById<FrameLayout>(R.id.layout_barcode_image_background).isVisible = false
        }
    }

    private fun showBarcodeDate() {
        findViewById<TextView>(R.id.text_view_date).text = dateFormatter.format(barcode.date)
    }

    private fun showBarcodeFormat() {
        val format = barcode.format.toStringId()
        findViewById<Toolbar>(R.id.toolbar)?.let { toolbar ->
            toolbar.setTitle(format)
        }
    }

    private fun showBarcodeName() {
        showBarcodeName(barcode.name)
    }

    private fun showBarcodeName(name: String?) {
        findViewById<TextView>(R.id.text_view_barcode_name)?.let { text_view_barcode_name ->
            text_view_barcode_name.isVisible = name.isNullOrBlank().not()
            text_view_barcode_name.text = name.orEmpty()
        }
    }

    private fun showBarcodeText() {
        findViewById<TextView>(R.id.text_view_barcode_text)?.let { text_view_barcode_text ->
            text_view_barcode_text.text = if (isCreated) {
                barcode.text
            } else {
                barcode.formattedText
            }
        }
    }

    private fun showBarcodeCountry() {
        val country = barcode.country ?: return
        when (country.contains('/')) {
            false -> showOneBarcodeCountry(country)
            true -> showTwoBarcodeCountries(country.split('/'))
        }
    }

    private fun showOneBarcodeCountry(country: String) {
        val fullCountryName = buildFullCountryName(country)
        showFullCountryName(fullCountryName)
    }

    private fun showTwoBarcodeCountries(countries: List<String>) {
        val firstFullCountryName = buildFullCountryName(countries[0])
        val secondFullCountryName = buildFullCountryName(countries[1])
        val fullCountryName = "$firstFullCountryName / $secondFullCountryName"
        showFullCountryName(fullCountryName)
    }

    private fun buildFullCountryName(country: String): String {
        val currentLocale = currentLocale ?: return ""
        val countryName = Locale("", country).getDisplayName(currentLocale)
        val countryEmoji = country.toCountryEmoji()
        return "$countryEmoji $countryName"
    }

    private fun showFullCountryName(fullCountryName: String) {
        findViewById<TextView>(R.id.text_view_country)?.let { text_view_country ->
            text_view_country.apply {
                text = fullCountryName
                isVisible = fullCountryName.isBlank().not()
            }
        }
    }

    private fun showOrHideButtons() {
        findViewById<IconButton>(R.id.button_search)?.isVisible = isCreated.not()
        findViewById<ImageView>(R.id.button_edit_name)?.isVisible = barcode.isInDb

        if (isCreated) {
            return
        }

        findViewById<IconButton>(R.id.button_search_on_web)?.isVisible = barcode.isProductBarcode
        findViewById<IconButton>(R.id.button_search)?.isVisible = barcode.isProductBarcode.not()
        findViewById<IconButton>(R.id.button_add_to_calendar)?.isVisible =
            barcode.schema == BarcodeSchema.VEVENT
        findViewById<IconButton>(R.id.button_add_to_contacts)?.isVisible =
            barcode.schema == BarcodeSchema.VCARD || barcode.schema == BarcodeSchema.MECARD

        findViewById<IconButton>(R.id.button_call_phone_1)?.isVisible =
            barcode.phone.isNullOrEmpty().not()
        findViewById<IconButton>(R.id.button_call_phone_2)?.isVisible =
            barcode.secondaryPhone.isNullOrEmpty().not()
        findViewById<IconButton>(R.id.button_call_phone_3)?.isVisible =
            barcode.tertiaryPhone.isNullOrEmpty().not()

        findViewById<IconButton>(R.id.button_send_sms_or_mms_1)?.isVisible =
            barcode.phone.isNullOrEmpty().not() || barcode.smsBody.isNullOrEmpty().not()
        findViewById<IconButton>(R.id.button_send_sms_or_mms_2)?.isVisible =
            barcode.secondaryPhone.isNullOrEmpty().not()
        findViewById<IconButton>(R.id.button_send_sms_or_mms_3)?.isVisible =
            barcode.tertiaryPhone.isNullOrEmpty().not()

        findViewById<IconButton>(R.id.button_send_email_1)?.isVisible =
            barcode.email.isNullOrEmpty().not() || barcode.emailSubject.isNullOrEmpty()
                .not() || barcode.emailBody.isNullOrEmpty().not()
        findViewById<IconButton>(R.id.button_send_email_2)?.isVisible =
            barcode.secondaryEmail.isNullOrEmpty().not()
        findViewById<IconButton>(R.id.button_send_email_3)?.isVisible =
            barcode.tertiaryEmail.isNullOrEmpty().not()

        findViewById<IconButton>(R.id.button_show_location)?.isVisible =
            barcode.geoUri.isNullOrEmpty().not()
        findViewById<IconButton>(R.id.button_connect_to_wifi)?.isVisible =
            barcode.schema == BarcodeSchema.WIFI
        findViewById<IconButton>(R.id.button_open_wifi_settings)?.isVisible =
            barcode.schema == BarcodeSchema.WIFI
        findViewById<IconButton>(R.id.button_copy_network_name)?.isVisible =
            barcode.networkName.isNullOrEmpty().not()
        findViewById<IconButton>(R.id.button_copy_network_password)?.isVisible =
            barcode.networkPassword.isNullOrEmpty().not()
        findViewById<IconButton>(R.id.button_open_app)?.isVisible =
            barcode.appPackage.isNullOrEmpty().not() && isAppInstalled(barcode.appPackage)
        findViewById<IconButton>(R.id.button_open_in_app_market)?.isVisible =
            barcode.appMarketUrl.isNullOrEmpty().not()
        findViewById<IconButton>(R.id.button_open_in_youtube)?.isVisible =
            barcode.youtubeUrl.isNullOrEmpty().not()
        findViewById<IconButton>(R.id.button_show_otp)?.isVisible =
            barcode.otpUrl.isNullOrEmpty().not()
        findViewById<IconButton>(R.id.button_open_otp)?.isVisible =
            barcode.otpUrl.isNullOrEmpty().not()
        findViewById<IconButton>(R.id.button_open_bitcoin_uri)?.isVisible =
            barcode.bitcoinUri.isNullOrEmpty().not()
        findViewById<IconButton>(R.id.button_open_link)?.isVisible =
            barcode.url.isNullOrEmpty().not()
        findViewById<IconButton>(R.id.button_save_bookmark)?.isVisible =
            barcode.schema == BarcodeSchema.BOOKMARK
    }

    private fun showButtonText() {
        findViewById<IconButton>(R.id.button_call_phone_1)?.text =
            getString(R.string.activity_barcode_call_phone, barcode.phone)
        findViewById<IconButton>(R.id.button_call_phone_2)?.text =
            getString(R.string.activity_barcode_call_phone, barcode.secondaryPhone)
        findViewById<IconButton>(R.id.button_call_phone_3)?.text =
            getString(R.string.activity_barcode_call_phone, barcode.tertiaryPhone)

        findViewById<IconButton>(R.id.button_send_sms_or_mms_1)?.text =
            getString(R.string.activity_barcode_send_sms, barcode.phone)
        findViewById<IconButton>(R.id.button_send_sms_or_mms_2)?.text =
            getString(R.string.activity_barcode_send_sms, barcode.secondaryPhone)
        findViewById<IconButton>(R.id.button_send_sms_or_mms_3)?.text =
            getString(R.string.activity_barcode_send_sms, barcode.tertiaryPhone)

        findViewById<IconButton>(R.id.button_send_email_1)?.text =
            getString(R.string.activity_barcode_send_email, barcode.email)
        findViewById<IconButton>(R.id.button_send_email_2)?.text =
            getString(R.string.activity_barcode_send_email, barcode.secondaryEmail)
        findViewById<IconButton>(R.id.button_send_email_3)?.text =
            getString(R.string.activity_barcode_send_email, barcode.tertiaryEmail)
    }

    private fun showConnectToWifiButtonEnabled(isEnabled: Boolean) {
        findViewById<IconButton>(R.id.button_connect_to_wifi)?.isEnabled = isEnabled
    }

    private fun showDeleteBarcodeConfirmationDialog() {
        val dialog =
            DeleteConfirmationDialogFragment.newInstance(R.string.dialog_delete_barcode_message)
        dialog.show(supportFragmentManager, "")
    }

    private fun showEditBarcodeNameDialog() {
        val dialog = EditBarcodeNameDialogFragment.newInstance(barcode.name)
        dialog.show(supportFragmentManager, "")
    }

    private fun showSearchEnginesDialog() {
        val dialog = ChooseSearchEngineDialogFragment()
        dialog.show(supportFragmentManager, "")
    }

    private fun showLoading(isLoading: Boolean) {
        findViewById<ProgressBar>(R.id.progress_bar_loading)?.isVisible = isLoading
        findViewById<NestedScrollView>(R.id.scroll_view)?.isVisible = isLoading.not()
    }


    private fun startActivityIfExists(action: String, uri: String) {
        val intent = Intent(action, Uri.parse(uri))
        startActivityIfExists(intent)
    }

    private fun startActivityIfExists(intent: Intent) {
        intent.apply {
            flags = flags or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            showToast(R.string.activity_barcode_no_app)
        }
    }

    private fun isAppInstalled(appPackage: String?): Boolean {
        return packageManager?.getLaunchIntentForPackage(appPackage.orEmpty()) != null
    }

    private fun copyToClipboard(text: String) {
        val clipData = ClipData.newPlainText("", text)
        clipboardManager.setPrimaryClip(clipData)
    }

    private fun showToast(stringId: Int) {
        Toast.makeText(this, stringId, Toast.LENGTH_SHORT).show()
    }

    private fun increaseBrightnessToMax() {
        setBrightness(1.0f)
    }

    private fun restoreOriginalBrightness() {
        setBrightness(originalBrightness)
    }

    private fun setBrightness(brightness: Float) {
        window.attributes = window.attributes.apply {
            screenBrightness = brightness
        }
    }
}
