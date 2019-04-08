package ir.alizeyn.neshanmock.util;

/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

import android.util.Base64;

import org.neshan.core.LngLat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class that encodes and decodes Polylines.
 * <p>
 * <p>See <a href="https://developers.google.com/maps/documentation/utilities/polylinealgorithm">
 * https://developers.google.com/maps/documentation/utilities/polylinealgorithm</a> for detailed
 * description of this format.
 */
public class PolylineEncoding {

    public static class LatLng {
        double lat;
        double lng;

        public LatLng(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }
    }

    /**
     * Decodes an encoded path string into a sequence of LatLngs.
     */
    public static List<LatLng> decodeToLatLng(final String encodedPath) {

        int len = encodedPath.length();

        final List<LatLng> path = new ArrayList<>(len / 2);
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new LatLng(lat * 1e-5, lng * 1e-5));
        }

        return path;
    }

    public static List<LngLat> decode(final String encodedPath) {
        List<LngLat> newPath = new ArrayList<>();
        List<LatLng> path = decodeToLatLng(encodedPath);
        for (LatLng latlng :
                path) {
            newPath.add(new LngLat(latlng.lng, latlng.lat));
        }
        return newPath;
    }

    // Rajman coding
    public static String decodeBase64(String string) {
        int l = string.length();
        string = string.substring(l - 1) + string.substring(1, l - 1) + string.substring(0, 1);
        string = new String(Base64.decode(string, 0));
        l = string.length();
        string = string.substring(l - 1) + string.substring(1, l - 1) + string.substring(0, 1);
        return string;
    }
}