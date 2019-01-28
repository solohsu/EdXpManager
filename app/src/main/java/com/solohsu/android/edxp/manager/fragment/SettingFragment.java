package com.solohsu.android.edxp.manager.fragment;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.solohsu.android.edxp.manager.R;

import static com.solohsu.android.edxp.manager.adapter.AppHelper.setForceGlobalMode;

public class SettingFragment extends PreferenceFragmentCompat {

    public SettingFragment() {

    }

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().setTitle(R.string.nav_title_settings);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_settings);
        findPreference("force_global_mode").setOnPreferenceChangeListener(
                (preference, newValue) -> setForceGlobalMode((Boolean) newValue));
    }

}
