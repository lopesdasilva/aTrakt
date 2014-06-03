package com.lopesdasilva.trakt.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.lopesdasilva.trakt.R;

/**
 * Created by Tiago on 03-06-2014.
 */
public class SettingsFragment extends PreferenceFragment {

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }

}
