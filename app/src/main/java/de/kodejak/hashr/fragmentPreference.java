package de.kodejak.hashr;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

/**
 *   Hashr - generate and compare hashes like MD5 or SHA-1 on Android.
 *   Copyright (C) 2015  Christian Handorf - kodejak at gmail dot com
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see http://www.gnu.org/licenses
 */

public class fragmentPreference extends android.support.v4.app.Fragment {
    View rootView;
    Context mContext;
    CheckBox checkUpper = null;
    CheckBox checkTrim = null;

    //private static final String TAG = "Hashr";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragmentpref_layout, container, false);
        this.mContext = container.getContext();

        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean my_checkbox_upper = mySharedPreferences.getBoolean("uppercase_hash", false);
        boolean my_checkbox_trim = mySharedPreferences.getBoolean("trim_hash", true);

        checkUpper = (CheckBox) rootView.findViewById(R.id.checkPrefUppercase);
        checkUpper.setChecked(my_checkbox_upper);

        checkUpper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkUpper.setChecked(isChecked);
                SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                mySharedPreferences.edit().putBoolean("uppercase_hash", isChecked).commit();
            }
        });

        checkTrim = (CheckBox) rootView.findViewById(R.id.checkPrefTrim);
        checkTrim.setChecked(my_checkbox_trim);
        checkTrim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkTrim.setChecked(isChecked);
                SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                mySharedPreferences.edit().putBoolean("trim_hash", isChecked).commit();
            }
        });

        return rootView;
    }

}
