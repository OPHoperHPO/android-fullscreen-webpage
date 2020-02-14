// Developed by Anodev (OPHoperHPO). All rights are reserved!
package com.anodev.fullwebview

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import kotlinx.android.synthetic.main.settings_activity.*


@Suppress("DEPRECATION")
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        AuthorTextViewBottom.setOnClickListener {
            // Open author link
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.author_url)))
            startActivity(browserIntent)
        }

    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val otherCategory = PreferenceCategory(context)
            otherCategory.title = getString(R.string.other_settings_title)
            preferenceScreen.addPreference(otherCategory)

            val appOnGithub = CheckBoxPreference(context)
            val authorOnGithub = CheckBoxPreference(context)

            appOnGithub.title = getString(R.string.app_github_settings_text)
            authorOnGithub.title = getString(R.string.author)
            appOnGithub.setDefaultValue(true)
            authorOnGithub.setDefaultValue(true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                appOnGithub.icon = resources.getDrawable(R.drawable.github_logo, null)
                authorOnGithub.icon = resources.getDrawable(R.drawable.github_logo, null)
            } else {
                try {
                    appOnGithub.icon = resources.getDrawable(R.drawable.github_logo)
                    authorOnGithub.icon = resources.getDrawable(R.drawable.github_logo)
                } catch (e: Throwable){}
            }

            appOnGithub.setOnPreferenceClickListener {
                // Open app link
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_url)))
                startActivity(browserIntent)
                false }

            authorOnGithub.setOnPreferenceClickListener {
                // Open author link
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.author_url)))
                startActivity(browserIntent)
                false }

            otherCategory.addPreference(appOnGithub)
            otherCategory.addPreference(authorOnGithub)
         }
    }

}