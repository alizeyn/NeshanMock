package ir.alizeyn.neshanmock.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

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

    public static String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return format.format(date);
    }
}
