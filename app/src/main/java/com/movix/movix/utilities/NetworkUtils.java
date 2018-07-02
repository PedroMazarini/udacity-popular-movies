/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.movix.movix.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.movix.movix.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the movie server.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String BASE_URL =
            "http://api.themoviedb.org/3/movie";

    private static final String BASE_POSTER_URL =
            "http://image.tmdb.org/t/p/w185/";

    private static final String BASE_TRAILER_THUMB_URL =
            "http://img.youtube.com/vi/";

    public static URL buildUrl(Context context, String sortOrder) {
        // COMPLETED (1) Fix this method to return the URL used to query Open Weather Map's API
        Uri builtUri = Uri.parse(BASE_URL+"/"+sortOrder).buildUpon()
                .appendQueryParameter("api_key",context.getResources().getString(R.string.api_key))
                .appendQueryParameter("language", Locale.getDefault().getISO3Language())
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }
    public static URL buildTraillersURL(Context context, Integer id) {
        // COMPLETED (1) Fix this method to return the URL used to query Open Weather Map's API
        Uri builtUri = Uri.parse(BASE_URL+"/"+String.valueOf(id)+"/videos").buildUpon()
                .appendQueryParameter("api_key",context.getResources().getString(R.string.api_key))
                .appendQueryParameter("language", Locale.getDefault().getISO3Language())
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }
    public static URL buildDetailsURL(Context context, Integer id) {
        // COMPLETED (1) Fix this method to return the URL used to query Open Weather Map's API
        Uri builtUri = Uri.parse(BASE_URL+"/"+String.valueOf(id)).buildUpon()
                .appendQueryParameter("api_key",context.getResources().getString(R.string.api_key))
                .appendQueryParameter("language", Locale.getDefault().getISO3Language())
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static String buildBannerUrl(String posterPath) {
        return BASE_POSTER_URL+posterPath;
    }
    public static String buildTrailerThumbUrl(String trailerId) {
        return BASE_TRAILER_THUMB_URL+trailerId+"/0.jpg";
    }


    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}