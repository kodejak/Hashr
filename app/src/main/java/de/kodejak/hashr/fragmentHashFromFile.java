package de.kodejak.hashr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import de.kodejak.utils.fileWork;
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

public class fragmentHashFromFile extends android.support.v4.app.Fragment {
    View rootView;
    Context mContext;

    private static final String TAG = "Hashr";
    private hashgen hashGen = new hashgen();
    private EditText inputEdit = null;
    private EditText compareEdit = null;
    private TextView tvMatch = null;
    private int sdk;

    private String fragTitle;
    private String fragJobStr;
    private int fragJob;
    private String fragJobExt;

    private boolean uppercaseHash = false;
    private boolean trimHash = true;

    final int ACTIVITY_CHOOSE_FILE = 1;

    private String lastFile;
    private int fileJob;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragmenthashfromfile_layout, container, false);
        this.mContext = container.getContext();

        this.fragJob = getArguments().getInt("job");

        switch (this.fragJob) {
            case 1:
                fragTitle = "MD5 from file";
                fragJobStr = "MD5";
                fragJobExt = ".md5";
                break;
            case 2:
                fragTitle = "SHA-1 from file";
                fragJobStr = "SHA-1";
                fragJobExt = ".sha1";
                break;
            case 3:
                fragTitle = "SHA-256 from file";
                fragJobStr = "SHA-256";
                fragJobExt = ".sha256";
                break;
            case 4:
                fragTitle = "CRC32 from file";
                fragJobStr = "CRC32";
                fragJobExt = ".crc32";
                break;
        }

        lastFile = this.getArguments().getString("sumfile");

        Log.d(TAG, fragTitle + " / " + fragJobStr + " / " + lastFile);

        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        uppercaseHash = mySharedPreferences.getBoolean("uppercase_hash", false);
        trimHash = mySharedPreferences.getBoolean("trim_hash", false);

        TextView tvHead = (TextView) rootView.findViewById(R.id.tvHeader);
        tvHead.setText(fragTitle);
        Button btnGen = (Button) rootView.findViewById(R.id.btnGenerate);
        btnGen.setText("Generate " + fragJobStr + " hash");
        Button btnComp = (Button) rootView.findViewById(R.id.btnCompare);
        btnComp.setText("Compare " + fragJobStr + " hashes");
        CheckBox checkToFile = (CheckBox) rootView.findViewById(R.id.checkToFile);
        checkToFile.setText("Save " + fragJobStr + " to " + fragJobExt + " file");

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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (lastFile != null) {
            getHashFromFile(lastFile);
        }
    }

    public void OnButtonGenerateClick() {
        String hashed;
        EditText txtInput = (EditText) getView().findViewById(R.id.edInput);
        String strInput = txtInput.getText().toString();
        String hashTextToFile;
        File file;

        if (strInput.length() < 1) {
            return;
        }

        file = new File(strInput);
        hashed = hashGen.generateHashFromFile(file, fragJobStr);

        if (uppercaseHash) {
            hashed = hashed.toUpperCase();
        }

        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtInput.getWindowToken(), 0);

        TextView txtOutput = (TextView) getView().findViewById(R.id.edOutput);
        txtOutput.setText(hashed);

        CheckBox toFileCheck = (CheckBox) getView().findViewById(R.id.checkToFile);

        if (toFileCheck.isChecked()) {
            if (file == null) {
                return;
            }

            fileWork fw = new fileWork();
            boolean esAvail;
            boolean esReadOnly;
            esAvail = fw.isExtStorageAvailable();
            esReadOnly = !fw.isExtStorageReadOnly();

            if (esAvail == false) {
                Toast.makeText(mContext, "External Storage not available!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (esReadOnly == false) {
                Toast.makeText(mContext, "External Storage is readonly!", Toast.LENGTH_SHORT).show();
                return;
            }

            hashTextToFile = hashed + "  " + file.getName();
            String outputDir = fw.createExternalAppDir("Hashr");
            if (outputDir != "") {
                fw.writeTextToFile(outputDir, file.getName() + fragJobExt, hashTextToFile);
                Toast.makeText(mContext, fragJobExt + " file written to disk.", Toast.LENGTH_SHORT).show();
            }
        }
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

        if (uppercaseHash)
            out = out.toUpperCase();

        txtOutput.setText(out);
    }

    public void OnButtonChooseFileClick() {
        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("file/*");
        intent = Intent.createChooser(chooseFile, "Choose a file");
        fileJob = 0; // open to generate
        getActivity().startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
    }

    public void OnButtonGetHashFromFileClick() {
        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("file/*");
        intent = Intent.createChooser(chooseFile, "Choose a " + fragJobExt +" file");
        fileJob = 1; // open to generate
        getActivity().startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case ACTIVITY_CHOOSE_FILE: {
                if (resultCode == Activity.RESULT_OK){
                    Uri uri = data.getData();
                    fileWork fw= new fileWork();
                    String filePath = fw.getFileNameWithPathByUri(mContext, uri);
                    lastFile = filePath;

                    switch (fileJob) {
                        case 0:
                            EditText txtInput = (EditText) getView().findViewById(R.id.edInput);
                            txtInput.setText(filePath);
                            break;
                        case 1:
                            getHashFromFile(filePath);
                            break;
                    }
                }
            }
        }
    }

    private String getFirstWord(String text) {
        if (text.indexOf(' ') > -1) {
            return text.substring(0, text.indexOf(' '));
        } else {
            return text;
        }
    }

    private void getHashFromFile(String fileName) {
        String firstLine = null;
        String hash[] = null;
        fileWork fw = new fileWork();

        Log.d(TAG, "File to read: " + fileName);
        firstLine = fw.getFirstLineFromFile(fileName);

        if (firstLine == null) {
            Toast.makeText(mContext, "No hash from file found", Toast.LENGTH_SHORT).show();
            return;
        }

        TextView edComp = (TextView) getView().findViewById(R.id.edCompare);
        edComp.setText(getFirstWord(firstLine));
    }


}
