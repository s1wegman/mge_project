package ch.ost.rj.mge.timer.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import ch.ost.rj.mge.timer.R

class PreferenceFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}