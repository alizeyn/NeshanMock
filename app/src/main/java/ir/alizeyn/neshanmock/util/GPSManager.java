package ir.alizeyn.neshanmock.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;


import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.neshan.BuildConfig;
import org.neshan.core.LngLat;
import org.neshan.ui.MapView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.core.app.ActivityCompat;

/**
 * manage GPS
 */

@SuppressLint("MissingPermission")
public class GPSManager {
    public static final String TAG = GPSManager.class.getSimpleName();
    private static final int LOCATION_INTERVAL = 200;
    private static final int LOCATION_FASTEST_INTERVAL = 100;

    private static final int LOCATION_DISTANCE = 0;
    private static Context context;
    private static GPSManager instance;
    private final Map<String, LocationListener> listenersInstances = new HashMap<>();
    private final LocationManager locationManager;
    private boolean started;
    private LngLat point;
    private OnGPSFixedListener gpsFixedListener;

    private GPSManager(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public synchronized static GPSManager getInstance(Context context) {
        GPSManager.context = context;
        if (instance == null) {
            instance = new GPSManager(context.getApplicationContext());
        }
        return instance;

    }

    public static synchronized void shutdown() {
        if (instance != null) {
            instance.stop();
        }
        instance = null;
    }

    private void onLocationChange(Location location) {
        if (gpsFixedListener != null) {
            point = new LngLat(location.getLongitude(), location.getLatitude());
            if (gpsFixedListener != null) {
                gpsFixedListener.onLocationChanged(location, point);
            }
        }
    }

    public synchronized void setOnGPSFixed(OnGPSFixedListener gpsFixedListener) {
        this.gpsFixedListener = gpsFixedListener;
    }

    public static LocationRequest createHighAccuracyLocationRequest() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(LOCATION_INTERVAL);
        mLocationRequest.setFastestInterval(LOCATION_FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    public synchronized void start() {
        if (!started) {
            //get gps from providers.
            List<String> enabledProviders = this.locationManager.getProviders(true);
            for (String provider : enabledProviders) {
                setListener(provider);
            }
            started = true;
        }
    }

    private void setListener(String provider) {

        try {

            if (locationManager.isProviderEnabled(provider)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }

                LocationListener locationListenerInstance = listenersInstances.get(provider);

                if (locationListenerInstance == null) {
                    locationListenerInstance = new LocationListenerIMP(provider);
                    listenersInstances.put(provider, locationListenerInstance);
                }

                locationManager.requestLocationUpdates(
                        provider,
                        LOCATION_FASTEST_INTERVAL,
                        LOCATION_DISTANCE,
                        locationListenerInstance);
            }

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public synchronized void stop() {
        try {
            if (started) {
                started = false;
                stopLocationManager(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized boolean stopLocationManager(boolean stopGpsProvider) {
        for (Map.Entry<String, LocationListener> ins : listenersInstances.entrySet()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
            }
            if (!stopGpsProvider && "gps".equals(ins.getKey()))
                continue;

            locationManager.removeUpdates(ins.getValue());
        }
        return false;
    }

    private class LocationListenerIMP implements LocationListener {

        private String provider;

        public LocationListenerIMP(String provider) {
            this.provider = provider;
        }

        @Override
        public void onLocationChanged(Location loc) {

            if (provider.equals("gps")) {
                stopLocationManager(false);
            }

            GPSManager.this.onLocationChange(loc);
        }

        @Override
        public void onProviderDisabled(String arg0) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            setListener(provider);
        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

        }

    }

    public interface OnGPSFixedListener {
        void onLocationChanged(Location loc, LngLat pos);
    }

    private Location getMostAccurateLocation(List<Location> locations) {
        Location result = locations.get(0);
        for (Location loc : locations) {
            if (loc.getAccuracy() > result.getAccuracy()) {
                result = loc;
            }
        }
        return result;
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                List<Location> locations = locationResult.getLocations();
                if (locations.size() > 0) {
                    Location location = getMostAccurateLocation(locations);
                    point = new LngLat(location.getLongitude(), location.getLatitude());
                    if (gpsFixedListener != null && location.hasAccuracy()) {
                        gpsFixedListener.onLocationChanged(location, point);
                    }
                }
            }
        }
    };

}