package de.kodejak.hashr;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
//import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import de.kodejak.utils.fileWork;

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

public class MainActivity extends android.support.v7.app.ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static String TAG ="Hashr";

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private android.app.Fragment nativeFragment;

    private CharSequence mTitle;

    private String lastSumFile = null;
    private int forcedFragmentNum = -1;

    private static final int FRAG_TEXT_MD5 = 1;
    private static final int FRAG_TEXT_SHA1 = 2;
    private static final int FRAG_TEXT_SHA256 = 3;
    private static final int FRAG_TEXT_CRC32 = 4;
    private static final int FRAG_FILE_MD5 = 6;
    private static final int FRAG_FILE_SHA1 = 7;
    private static final int FRAG_FILE_SHA256 = 8;
    private static final int FRAG_FILE_CRC32 = 9;
    private static final int FRAG_PREF = 11;
    private static final int FRAG_ABOUT = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set a toolbar which will replace the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        Intent intent = getIntent();
        String action = intent.getAction();

        if (action.compareTo(Intent.ACTION_VIEW) == 0) {
            String scheme = intent.getScheme();
            ContentResolver resolver = getContentResolver();

            if (scheme.compareTo(ContentResolver.SCHEME_CONTENT) == 0) {
                Uri uri = intent.getData();
                String name = getContentName(resolver, uri);

                Log.v("tag" , "Content intent detected: " + action + " : " + intent.getDataString() + " : " + intent.getType() + " : " + name);
                //What TODO?
            }
            else if (scheme.compareTo(ContentResolver.SCHEME_FILE) == 0) {
                lastSumFile = intent.getData().getPath();
                forcedFragmentNum = prepareOpenSumFile(lastSumFile);

                if (forcedFragmentNum > -1) {
                    mNavigationDrawerFragment.selectItem(forcedFragmentNum);
                }
            }
        }
    }

    public static String getContentName(ContentResolver resolver, Uri uri){
        Cursor cursor = resolver.query(uri, null, null, null, null);
        cursor.moveToFirst();
        int nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
        if (nameIndex >= 0) {
            return cursor.getString(nameIndex);
        } else {
            return null;
        }
    }

    private Fragment createFromTextFragmentInstance(int job) {
        Fragment newFragment = new fragmentHashFromText();
        Bundle args = new Bundle();
        args.putInt("job", job);
        newFragment.setArguments(args);

        return newFragment;
    }
    private Fragment createFromFileFragmentInstance(int job, String sumFile) {
        Fragment newFragment = new fragmentHashFromFile();
        Bundle args = new Bundle();
        args.putInt("job", job);
        args.putString("sumfile", sumFile);
        newFragment.setArguments(args);

        return newFragment;
    }

    private int prepareOpenSumFile(String fileName) {
        int fragNum = -1;
        fileWork fw = new fileWork();

        String fileExt = fw.getFileExtension(fileName);

        if (fileExt.equalsIgnoreCase("md5")) {
            fragNum = FRAG_FILE_MD5;
        } else
            if (fileExt.equalsIgnoreCase("sha1")) {
                fragNum = FRAG_FILE_SHA1;
            } else
                if (fileExt.equalsIgnoreCase("sha256")) {
                    fragNum = FRAG_FILE_SHA256;
                } else
                if (fileExt.equalsIgnoreCase("crc32")) {
                    fragNum = FRAG_FILE_CRC32;
                }
        return fragNum;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment objFragment = null;

        String fragmentTag = "";

        String[] stringArray = getResources().getStringArray(R.array.sections);
        if (position >= 0) {
            fragmentTag = stringArray[position];
        }

        switch (position) {
            case 0:
            case FRAG_TEXT_MD5:
                objFragment = createFromTextFragmentInstance(1);
                fragmentTag = getString(R.string.title_section1);
                break;
            case FRAG_TEXT_SHA1:
                objFragment = createFromTextFragmentInstance(2);
                fragmentTag = getString(R.string.title_section1);
                break;
            case FRAG_TEXT_SHA256:
                objFragment = createFromTextFragmentInstance(3);
                fragmentTag = getString(R.string.title_section1);
                break;
            case FRAG_TEXT_CRC32:
                objFragment = createFromTextFragmentInstance(4);
                fragmentTag = getString(R.string.title_section1);
                break;
            case FRAG_FILE_MD5:
                objFragment = createFromFileFragmentInstance(1, lastSumFile);
                fragmentTag = getString(R.string.title_section2);
                break;
            case FRAG_FILE_SHA1:
                objFragment = createFromFileFragmentInstance(2, lastSumFile);
                fragmentTag = getString(R.string.title_section2);
                break;
            case FRAG_FILE_SHA256:
                objFragment = createFromFileFragmentInstance(3, lastSumFile);
                fragmentTag = getString(R.string.title_section2);
                break;
            case FRAG_FILE_CRC32:
                objFragment = createFromFileFragmentInstance(4, lastSumFile);
                fragmentTag = getString(R.string.title_section2);
                break;
            case FRAG_PREF:
                objFragment = new fragmentPreference();
                break;
            case FRAG_ABOUT:
                objFragment = new fragmentAbout();
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, objFragment, fragmentTag)
                .commit();

        // we have done this
        lastSumFile = null;
    }

    public void onSectionAttached(int number) {
        String[] stringArray = getResources().getStringArray(R.array.sections);
        if (number >= 1) {
            mTitle = stringArray[number - 1];
        }
    }

    /*
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }
    */


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    // TODO: avoid this stupid wrappers
    public void OnButtonGenerateClick(View v) {
        fragmentHashFromText fText;
        fText = (fragmentHashFromText) getSupportFragmentManager().findFragmentByTag(getString(R.string.title_section1));
        if (fText != null) {
            if (fText.isVisible()) {
                fText.OnButtonGenerateClick();
                return;
            }
        }
        fragmentHashFromFile fFile;
        fFile = (fragmentHashFromFile) getSupportFragmentManager().findFragmentByTag(getString(R.string.title_section2));
        if (fFile != null) {
            if (fFile.isVisible()) {
                fFile.OnButtonGenerateClick();
                return;
            }
        }
    }
    public void OnButtonCompareClick(View v) {
        fragmentHashFromText fText;
        fText = (fragmentHashFromText) getSupportFragmentManager().findFragmentByTag(getString(R.string.title_section1));
        if (fText != null) {
            if (fText.isVisible()) {
                fText.OnButtonCompareClick();
                return;
            }
        }
        fragmentHashFromFile fFile;
        fFile = (fragmentHashFromFile) getSupportFragmentManager().findFragmentByTag(getString(R.string.title_section2));
        if (fFile != null) {
            if (fFile.isVisible()) {
                fFile.OnButtonCompareClick();
                return;
            }
        }
    }
    public void OnButtonToClipboardClick(View v) {
        fragmentHashFromText fText;
        fText = (fragmentHashFromText) getSupportFragmentManager().findFragmentByTag(getString(R.string.title_section1));
        if (fText != null) {
            if (fText.isVisible()) {
                fText.OnButtonToClipboardClick();
                return;
            }
        }
        fragmentHashFromFile fFile;
        fFile = (fragmentHashFromFile) getSupportFragmentManager().findFragmentByTag(getString(R.string.title_section2));
        if (fFile != null) {
            if (fFile.isVisible()) {
                fFile.OnButtonToClipboardClick();
                return;
            }
        }
    }
    public void OnButtonFromClipboardClick(View v) {
        fragmentHashFromText fText;
        fText = (fragmentHashFromText) getSupportFragmentManager().findFragmentByTag(getString(R.string.title_section1));
        if (fText != null) {
            if (fText.isVisible()) {
                fText.OnButtonFromClipboardClick();
                return;
            }
        }
        fragmentHashFromFile fFile;
        fFile = (fragmentHashFromFile) getSupportFragmentManager().findFragmentByTag(getString(R.string.title_section2));
        if (fFile != null) {
            if (fFile.isVisible()) {
                fFile.OnButtonFromClipboardClick();
                return;
            }
        }
    }

    public void OnButtonChooseFileClick(View v) {
        fragmentHashFromFile fFile;
        fFile = (fragmentHashFromFile) getSupportFragmentManager().findFragmentByTag(getString(R.string.title_section2));
        if (fFile != null) {
            if (fFile.isVisible()) {
                fFile.OnButtonChooseFileClick();
                return;
            }
        }
    }

    public void OnButtonGetHashFromFileClick(View v) {
        fragmentHashFromFile fFile;
        fFile = (fragmentHashFromFile) getSupportFragmentManager().findFragmentByTag(getString(R.string.title_section2));
        if (fFile != null) {
            if (fFile.isVisible()) {
                fFile.OnButtonGetHashFromFileClick();
                return;
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            fragmentHashFromFile fFile;
            fFile = (fragmentHashFromFile) getSupportFragmentManager().findFragmentByTag(getString(R.string.title_section2));
            if (fFile != null) {
                if (fFile.isVisible()) {
                    fFile.onActivityResult(requestCode, resultCode, data);
                }
            }

            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
