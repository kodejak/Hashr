package de.kodejak.hashr;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import de.kodejak.utils.hashgen;

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

public class fragmentHashFromText extends android.support.v4.app.Fragment {
    View rootView;
    Context mContext;

    //private static final String TAG = "Hashr";
    private hashgen hashGen = new hashgen();
    private EditText inputEdit = null;
    private EditText compareEdit = null;
    private TextView tvMatch = null;
    private int sdk;

    private String fragTitle;
    private String fragJobStr;
    private int fragJob;

    private boolean uppercaseHash = false;
    private boolean trimHash = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragmenthashfromtext_layout, container, false);
        this.mContext = container.getContext();

        this.fragJob = getArguments().getInt("job");

        switch (this.fragJob) {
            case 1:
                fragTitle = "MD5 from text";
                fragJobStr = "MD5";
                break;
            case 2:
                fragTitle = "SHA-1 from text";
                fragJobStr = "SHA-1";
                break;
            case 3:
                fragTitle = "SHA-256 from text";
                fragJobStr = "SHA-256";
                break;
            case 4:
                fragTitle = "CRC32 from text";
                fragJobStr = "CRC32";
                break;
        }

        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        uppercaseHash = mySharedPreferences.getBoolean("uppercase_hash", false);
        trimHash = mySharedPreferences.getBoolean("trim_hash", false);

        TextView tvHead = (TextView) rootView.findViewById(R.id.tvHeader);
        tvHead.setText(fragTitle);
        Button btnGen = (Button) rootView.findViewById(R.id.btnGenerate);
        btnGen.setText("Generate " + fragJobStr + " hash");
        Button btnComp = (Button) rootView.findViewById(R.id.btnCompare);
        btnComp.setText("Compare " + fragJobStr + " hashes");


        sdk = android.os.Build.VERSION.SDK_INT;

        tvMatch = (TextView) rootView.findViewById(R.id.tvCompare);
        inputEdit = (EditText)rootView.findViewById(R.id.edInput);
        inputEdit.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvMatch.setText("");
                tvMatch.setVisibility(View.GONE);
            }
        });
        compareEdit = (EditText)rootView.findViewById(R.id.edCompare);
        compareEdit.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvMatch.setText("");
                tvMatch.setVisibility(View.GONE);
            }
        });

        return rootView;
    }

    public void OnButtonGenerateClick() {
        EditText txtInput = (EditText) getView().findViewById(R.id.edInput);
        String strInput = txtInput.getText().toString();

        String hashed = hashGen.generateHashFromText(strInput, fragJobStr);

        if (uppercaseHash) {
            hashed = hashed.toUpperCase();
        }

        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtInput.getWindowToken(), 0);

        TextView txtOutput = (TextView) getView().findViewById(R.id.edOutput);
        txtOutput.setText(hashed);
    }

    public void OnButtonCompareClick() {
        TextView edOutput = (TextView) getView().findViewById(R.id.edOutput);
        String strOutput = edOutput.getText().toString();
        EditText edCompare = (EditText) getView().findViewById(R.id.edCompare);
        String strCompare = edCompare.getText().toString();
        TextView tvMatch = (TextView) getView().findViewById(R.id.tvCompare);

        if (strOutput.length() < 1 || strCompare.length() < 1) {
            return;
        }


        if (strOutput.equals(strCompare)) {
            tvMatch.setText("Match !");

            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                tvMatch.setBackgroundDrawable( getResources().getDrawable(R.drawable.match_ok_bg) );
            } else {
                tvMatch.setBackground( getResources().getDrawable(R.drawable.match_ok_bg));
            }

            tvMatch.setTextColor(Color.WHITE);
            tvMatch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
        } else {
            tvMatch.setText("No match !");

            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                tvMatch.setBackgroundDrawable( getResources().getDrawable(R.drawable.match_notok_bg) );
            } else {
                tvMatch.setBackground( getResources().getDrawable(R.drawable.match_notok_bg));
            }

            tvMatch.setTextColor(Color.WHITE);
            tvMatch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cancel, 0, 0, 0);
        }

        tvMatch.setVisibility(View.VISIBLE);
    }

    public void OnButtonToClipboardClick() {
        TextView txtOutput = (TextView) getView().findViewById(R.id.edOutput);
        String strOutput = txtOutput.getText().toString();

        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setText(strOutput);

        Toast.makeText(mContext, fragJobStr + " copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    public void OnButtonFromClipboardClick() {
        EditText txtOutput = (EditText) getView().findViewById(R.id.edCompare);
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        CharSequence hash = clipboard.getText();
        String out = (String) hash;

        if (trimHash)
            out = out.trim();

        if (uppercaseHash) {
            out = out.toUpperCase();
        }

        txtOutput.setText(out);
    }
}
