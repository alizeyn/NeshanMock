package ir.alizeyn.neshanmock.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

import org.neshan.core.LngLat;
import org.neshan.core.LngLatVector;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author alizeyn
 * Created at 2/9/19
 */
public class Tools {

    public static boolean getLocationPermission(Activity activity, boolean openPermissionDialog) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (openPermissionDialog) {
                    activity.requestPermissions(new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            1000);
                }
                return false;
            }
        }
        return true;
    }

    public static boolean getStoragePermisstion(Activity activity, boolean openPermissionDialog) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (openPermissionDialog) {
                    activity.requestPermissions(new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1001);
                }
                return false;
            }
        }
        return true;
    }

    public static String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return format.format(date);
    }

    public static boolean isGPSEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                int locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
                return locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY;
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                LocationManager systemService = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (systemService.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                String locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                if (locationProviders.contains("network") && locationProviders.contains("gps")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isGooglePlayServicesAvailable(Context context) {
        try {
            GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
            int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
            return resultCode == ConnectionResult.SUCCESS;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     * <p>
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     *
     * @returns Distance in Meters
     */
    public static double distance(LngLat p1, LngLat p2) {

        double lat1 = p1.getX();
        double lat2 = p2.getX();
        double lon1 = p1.getY();
        double lon2 = p2.getY();

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }

    public static LineString convertLineToLineString(LngLatVector poses) {
        Coordinate[] lineCoordinate = new Coordinate[(int) poses.size()];
        for (int j = 0; j < poses.size(); j++) {
            LngLat mapPos = poses.get(j);
            lineCoordinate[j] = new Coordinate(mapPos.getY(), mapPos.getX());
        }
        return new GeometryFactory().createLineString(lineCoordinate);
    }
}
