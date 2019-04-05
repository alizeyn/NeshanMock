package ir.alizeyn.neshanmock.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import org.neshan.core.LngLat;
import org.neshan.services.NeshanMapStyle;
import org.neshan.services.NeshanServices;
import org.neshan.ui.MapView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import ir.alizeyn.neshanmock.R;
import ir.alizeyn.neshanmock.util.GPSManager;
import ir.alizeyn.neshanmock.util.Tools;

public class MenuActivity extends AppCompatActivity {

    private static final int ENABLING_GPS_RESULT = 1;
    private static final int LOCATION_REQUEST_CODE = 1000;
    private static final long LOCATION_INTERVAL_TIME = 200;
    private static final long LOCATION_FASTEST_INTERVAL_TIME = 100;
    private MapView map;
    private MaterialButton btnRecordTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        initViews();
        initMap();
        listeners();

        if (Tools.getLocationPermission(this, true)) {
            initgps();
        }
    }

    private void initgps() {
        // Start getting gps
        GPSManager.getInstance(this).setOnGPSFixed((loc, pos) -> {
            map.setFocalPointPosition(new LngLat(loc.getLongitude(), loc.getLatitude()), 1f);
            map.setZoom(17f, 0f);

        });
        GPSManager.getInstance(this).start();
    }

    private void initMap() {
        //set map focus position
        LngLat focalPoint = new LngLat(59.5521763, 36.3076283);
        map.setFocalPointPosition(focalPoint, 0f);
        map.setZoom(14f, 0f);
        //add basemap layer
        map.getLayers().add(NeshanServices.createBaseMap(NeshanMapStyle.NESHAN));
    }

    private void initViews() {
        map = findViewById(R.id.map);
        btnRecordTrack = findViewById(R.id.btnRecordTrack);

    }

    private void listeners() {
        btnRecordTrack.setOnClickListener(view -> startActivity(new Intent(MenuActivity.this, TrackActivity.class)));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                            grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        initgps();
                    } else {
                        Toast.makeText(this, R.string.please_grant_location_permission, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case ENABLING_GPS_RESULT:

                break;
        }
    }
}
