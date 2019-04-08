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

    public static String convertTime(long time){
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
}
