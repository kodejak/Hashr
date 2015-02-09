package de.kodejak.hashr;

import java.util.ArrayList;
import java.util.TreeSet;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

public class mListAdapter extends BaseAdapter {

        private static final int TYPE_ITEM = 0;
        private static final int TYPE_SEPARATOR = 1;

        private ArrayList<String> mData = new ArrayList<String>();
        private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

        private LayoutInflater mInflater;

        private Context mContext;

        public mListAdapter(Context context) {
            mContext = context;

            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final String item) {
            mData.add(item);
            notifyDataSetChanged();
        }

        public void addSectionHeaderItem(final String item) {
            mData.add(item);
            sectionHeader.add(mData.size() - 1);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean isEnabled(int position) {
            if (getItemViewType(position) == TYPE_SEPARATOR) {
                return false;
            }

            return true;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            int rowType = getItemViewType(position);

            if (convertView == null) {
                holder = new ViewHolder();
                switch (rowType) {
                    case TYPE_ITEM:
                        convertView = mInflater.inflate(R.layout.simple_list_item, null);
                        holder.textView = (TextView) convertView.findViewById(R.id.textNormal);

                        String[] stringArray = mContext.getResources().getStringArray(R.array.sections_icons);

                        if (stringArray[position].length() > 0) {
                            final int resourceId = mContext.getResources().getIdentifier(stringArray[position], "drawable",
                                    mContext.getPackageName());
                            Drawable drawable = null;
                            drawable = mContext.getResources().getDrawable(resourceId);

                            if (drawable != null) {
                                ((TextView) holder.textView).setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                            }
                        }

                        break;
                    case TYPE_SEPARATOR:
                        convertView = mInflater.inflate(R.layout.simple_list_section, null);
                        holder.textView = (TextView) convertView.findViewById(R.id.textSeparator);
                        break;
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textView.setText(mData.get(position));

            return convertView;
        }

        public static class ViewHolder {
            public TextView textView;
        }

    }
