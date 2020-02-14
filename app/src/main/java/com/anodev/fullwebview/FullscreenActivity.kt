// Developed by Anodev (OPHoperHPO). All rights are reserved!
package com.anodev.fullwebview

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_fullscreen.*
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoRuntimeSettings
import org.mozilla.geckoview.GeckoSession
import kotlin.system.exitProcess


@TargetApi(Build.VERSION_CODES.KITKAT)
class FullscreenActivity : AppCompatActivity() {

    private val mHideHandler = Handler()
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        web.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
    }
    private var mVisible: Boolean = false
    private val mHideRunnable = Runnable { hide() }
    private var numButtonPressed = 5



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mVisible = true
        init() // Initialize Gecko Runtime, Session, Prepare Webview for full screen display
        main() // Load url from configuration and upload it to webview
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }
    override fun onDestroy() {
        super.onDestroy()
        exitProcess(0)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        hide()
        super.onWindowFocusChanged(hasFocus)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == 25){  // Open the settings if the volume down button was pressed 5 times
            numButtonPressed -= 1
            showToast(getString(R.string.open_settings_button_toast).replace("%N",
                numButtonPressed.toString()), 0)
            if (numButtonPressed == 0) {
                startActivity(Intent(this, SettingsActivity::class.java))
                numButtonPressed = 5
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun firstRun(){
        /*
        First stage of launch
         */
        debugToast("First run")
        showToast(getString(R.string.first_run_url_config_text), 1)
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    private fun config(): String {
        /*
        Loads variables from config
         */
        debugToast("Load config")
        // Initialize preferences
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)

        // URL config
        val savedUrl = sharedPref.getString(getString(R.string.url_settings_key), "0")

        if ("0" == savedUrl) { // First stage of launch
            firstRun()
            return "0"

        }
        return savedUrl.toString()
    }

    private fun init() {
        /*
          Initialize webview
         */
        debugToast("Init stage 1")

        if (runtime == null) {  //  Start GeckoRuntime
            val runtimeSettingsBuilder = GeckoRuntimeSettings.Builder()
            runtimeSettingsBuilder.configFilePath("") // Fix for bug with Gecko Runtime
                                                        // in Android KitKat 4.4
                                // https://bugzilla.mozilla.org/show_bug.cgi?id=1567115
            runtime = GeckoRuntime.create(this, runtimeSettingsBuilder.build())
        }
        if (session == null) {
            session = GeckoSession() // Start session
            session!!.open(runtime!!)
            web.setSession(session!!)
        }
        web.setOnSystemUiVisibilityChangeListener { hide() } // Preparing for full screen display
    }
    private fun main(){
        /*
        Loads url to webview
         */
        debugToast("Main stage")
        // Configure all components
        val url = config()
        // Start load url
        loadUrl(url)
    }
    private fun loadUrl(url:String){
        // Try to load
        debugToast("Load URL: $url")
        if (session?.isOpen!!){
            session?.loadUri(url)
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun debugToast(msg: String) {
        /*
         Shows debug Toast
         */
        if (Const.debug){
            Utils.showPopupMessage(this, msg, 1)
        }
    }
    private fun showToast(msg: String, long: Int) {
        /*
         Shows Toast
         */
            Utils.showPopupMessage(this, msg, long)

    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    @Suppress("SameParameterValue")
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true
        private var runtime: GeckoRuntime? = null
        private var session : GeckoSession? = null
        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
    }
}
