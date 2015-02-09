package de.kodejak.hashr;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

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

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static String TAG ="Hashr";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private android.app.Fragment nativeFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    private Fragment createFromTextFragmentInstance(int job) {
        Fragment newFragment = new fragmentHashFromText();
        Bundle args = new Bundle();
        args.putInt("job", job);
        newFragment.setArguments(args);

        return newFragment;
    }
    private Fragment createFromFileFragmentInstance(int job) {
        Fragment newFragment = new fragmentHashFromFile();
        Bundle args = new Bundle();
        args.putInt("job", job);
        newFragment.setArguments(args);

        return newFragment;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        Log.d(TAG, "" + position);
        Fragment objFragment = null;
        boolean useNativeFragment = false;

        String fragmentTag = "";

        final String[] sections = getResources().getStringArray(R.array.sections);
        if (sections[position].charAt(0) == '#') {
            return;
        }

        String[] stringArray = getResources().getStringArray(R.array.sections);
        if (position >= 0) {
            fragmentTag = stringArray[position];
        }



        switch (position) {
            case 0:
            case 1:
                objFragment = createFromTextFragmentInstance(1); // MD5
                fragmentTag = getString(R.string.title_section1);
                break;
            case 2:
                objFragment = createFromTextFragmentInstance(2); // SHA-1
                fragmentTag = getString(R.string.title_section1);
                break;
            case 3:
                objFragment = createFromTextFragmentInstance(3); // SHA-256
                fragmentTag = getString(R.string.title_section1);
                break;
            case 5:
                objFragment = createFromFileFragmentInstance(1); // MD5
                fragmentTag = getString(R.string.title_section2);
                break;
            case 6:
                objFragment = createFromFileFragmentInstance(2); // SHA-1
                fragmentTag = getString(R.string.title_section2);
                break;
            case 7:
                objFragment = createFromFileFragmentInstance(3); // SHA-256
                fragmentTag = getString(R.string.title_section2);
                break;
            case 9:
                objFragment = new fragmentPreference();
                //useNativeFragment = true;
                break;
            case 10:
                objFragment = new fragmentAbout();
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, objFragment, fragmentTag)
                .commit();
        /*
        if (useNativeFragment) {
            android.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, nativeFragment).commit();
        } else {
            if (nativeFragment != null) {
                getFragmentManager().beginTransaction().remove(nativeFragment)
                        .commit();
                nativeFragment = null;
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, objFragment, fragmentTag)
                    .commit();
        }
        */
    }

    public void onSectionAttached(int number) {
        String[] stringArray = getResources().getStringArray(R.array.sections);
        if (number >= 1) {
            mTitle = stringArray[number - 1];
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }
    */

    
    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            onNavigationDrawerItemSelected(4);
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    public void OnButtonGenerateClick(View v) {
        fragmentHashFromText fText = null;
        fText = (fragmentHashFromText) getSupportFragmentManager().findFragmentByTag(getString(R.string.title_section1));
        if (fText != null) {
            if (fText.isVisible()) {
                fText.OnButtonGenerateClick();
                return;
            }
        }
        fragmentHashFromFile fFile = null;
        fFile = (fragmentHashFromFile) getSupportFragmentManager().findFragmentByTag(getString(R.string.title_section2));
        if (fFile != null) {
            if (fFile.isVisible()) {
                fFile.OnButtonGenerateClick();
                return;
            }
        }
    }
    public void OnButtonCompareClick(View v) {
        fragmentHashFromText fText = null;
        fText = (fragmentHashFromText) getSupportFragmentManager().findFragmentByTag(getString(R.string.title_section1));
        if (fText != null) {
            if (fText.isVisible()) {
                fText.OnButtonCompareClick();
                return;
            }
        }
        fragmentHashFromFile fFile = null;
        fFile = (fragmentHashFromFile) getSupportFragmentManager().findFragmentByTag(getString(R.string.title_section2));
        if (fFile != null) {
            if (fFile.isVisible()) {
                fFile.OnButtonCompareClick();
                return;
            }
        }
    }
    public void OnButtonToClipboardClick(View v) {
        fragmentHashFromText fText = null;
        fText = (fragmentHashFromText) getSupportFragmentManager().findFragmentByTag(getString(R.string.title_section1));
        if (fText != null) {
            if (fText.isVisible()) {
                fText.OnButtonToClipboardClick();
                return;
            }
        }
        fragmentHashFromFile fFile = null;
        fFile = (fragmentHashFromFile) getSupportFragmentManager().findFragmentByTag(getString(R.string.title_section2));
        if (fFile != null) {
            if (fFile.isVisible()) {
                fFile.OnButtonToClipboardClick();
                return;
            }
        }
    }
    public void OnButtonFromClipboardClick(View v) {
        fragmentHashFromText fText = null;
        fText = (fragmentHashFromText) getSupportFragmentManager().findFragmentByTag(getString(R.string.title_section1));
        if (fText != null) {
            if (fText.isVisible()) {
                fText.OnButtonFromClipboardClick();
                return;
            }
        }
        fragmentHashFromFile fFile = null;
        fFile = (fragmentHashFromFile) getSupportFragmentManager().findFragmentByTag(getString(R.string.title_section2));
        if (fFile != null) {
            if (fFile.isVisible()) {
                fFile.OnButtonFromClipboardClick();
                return;
            }
        }
    }

    public void OnButtonChooseFileClick(View v) {
        fragmentHashFromFile fFile = null;
        fFile = (fragmentHashFromFile) getSupportFragmentManager().findFragmentByTag(getString(R.string.title_section2));
        if (fFile != null) {
            if (fFile.isVisible()) {
                fFile.OnButtonChooseFileClick();
                return;
            }
        }
    }

    public void OnButtonGetHashFromFileClick(View v) {
        fragmentHashFromFile fFile = null;
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
            fragmentHashFromFile fFile = null;
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
