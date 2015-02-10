package de.kodejak.utils;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

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

public class fileWork {
    static final int READ_BLOCK_SIZE = 100;
    private String TAG = "Hashr";

    public static void writeTextToFile(String path, String fileName, String text) {
        try {
            FileOutputStream fos = new FileOutputStream(path + "/" + fileName);
            fos.write(text.toString().getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isExtStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExtStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static String createExternalAppDir(String dirName) {
        File f = new File(android.os.Environment.getExternalStorageDirectory(), File.separator + dirName + "/");
        if (f.exists()) {
            return f.toString();
        }

        f.mkdirs();

        if (f.exists()) {
            return f.toString();
        }
        return "";
    }

    public String getFilePathFromContentUri (Uri selectedVideoUri, ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    public static String getFileNameWithPathByUri(Context context, Uri uri)
    {
        String fileName="unknown";
        Uri filePathUri = uri;

        if (uri.getScheme().toString().compareTo("content")==0)
        {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst())
            {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                filePathUri = Uri.parse(cursor.getString(column_index));
                fileName = filePathUri.toString();
            }
        }
        else if (uri.getScheme().compareTo("file")==0)
        {
            fileName = filePathUri.getPath();
        }
        else
        {
            fileName = fileName + "_" + filePathUri.getLastPathSegment();
        }
        return fileName;
    }

    public String getFirstLineFromFile(String fileName) {
        String oneLine = null;
        File file = new File(fileName);

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
                Log.d(TAG, "Read Line: " + line.toString());
            }
            br.close();
        }
        catch (IOException e) {
        }

        // we only need the first line
        Scanner scan = new Scanner(text.toString()); // I have named your StringBuilder object sb
        if (scan.hasNextLine() ){
            oneLine = scan.nextLine();
            Log.d(TAG, "Extract Line: " + oneLine);
        }

        return oneLine;
    }

    public static String getFileExtension(String fileName)
    {
        return fileName.substring((fileName.lastIndexOf(".") + 1), fileName.length());
    }

}