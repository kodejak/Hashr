package de.kodejak.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

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

public class hashgen {
    private static final String TAG = "Hashr";
    final protected static char[] hexDigits = "0123456789abcdef".toCharArray();

    public static String generateHashFromText(String input, String type) {
        String hash = null;
        try
        {
            byte[] bytes = input.getBytes("UTF-8");

            if (type.equalsIgnoreCase("crc32")) {
                Checksum crc32 = new CRC32();
                crc32.update(bytes, 0, bytes.length);
                long chksum = crc32.getValue();

                hash = Long.toHexString(chksum);
            } else {
                MessageDigest digest = MessageDigest.getInstance(type);
                digest.update(bytes, 0, bytes.length);
                bytes = digest.digest();

                // This is ~55x faster than looping and String.formating()
                hash = bytesToHex( bytes );
            }


        }
        catch( NoSuchAlgorithmException e )
        {
            e.printStackTrace();
        }
        catch( UnsupportedEncodingException e )
        {
            e.printStackTrace();
        }

        Log.d(TAG, hash);
        return hash;
    }

    public static String generateHashFromFile(File aFile, String type) {
        MessageDigest digest = null;
        Checksum crc32 = null;
        String output;

        try {
            if (type.equalsIgnoreCase("crc32")) {
                crc32 = new CRC32();
            } else {
                digest = MessageDigest.getInstance(type);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(aFile);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Exception while getting FileInputStream", e);
            return null;
        }

        byte[] buffer = new byte[10240];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                if (type.equalsIgnoreCase("crc32")) {
                    crc32.update(buffer, 0, read);
                } else {
                    digest.update(buffer, 0, read);
                }
            }
            byte[] hash = null;
            long chksum = 0;

            if (type.equalsIgnoreCase("crc32")) {
                chksum = crc32.getValue();
                output = Long.toHexString(chksum);
            } else {
                hash = digest.digest();
                BigInteger bigInt = new BigInteger(1, hash);
                output = bigInt.toString(16);
                // Fill to 32 chars
                output = String.format("%32s", output).replace(' ', '0');
            }

            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for Hash", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "Exception on closing Hash input stream", e);
            }
        }
    }

    // http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
    public static String bytesToHex( byte[] bytes )
    {
        char[] hexChars = new char[ bytes.length * 2 ];
        for( int j = 0; j < bytes.length; j++ )
        {
            int v = bytes[ j ] & 0xFF;
            hexChars[ j * 2 ] = hexDigits[ v >>> 4 ];
            hexChars[ j * 2 + 1 ] = hexDigits[ v & 0x0F ];
        }
        return new String( hexChars );
    }

}
