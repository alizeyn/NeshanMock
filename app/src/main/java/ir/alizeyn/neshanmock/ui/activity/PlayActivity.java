package ir.alizeyn.neshanmock.ui.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import ir.alizeyn.neshanmock.R;
import ir.alizeyn.neshanmock.database.DatabaseClient;
import ir.alizeyn.neshanmock.database.MockEntity;
import ir.alizeyn.neshanmock.database.PosEntity;
import ir.alizeyn.neshanmock.util.Tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;

import org.neshan.core.LngLat;
import org.neshan.layers.VectorElementLayer;
import org.neshan.services.NeshanMapStyle;
import org.neshan.services.NeshanServices;
import org.neshan.styles.AnimationStyleBuilder;
import org.neshan.styles.AnimationType;
import org.neshan.styles.MarkerStyleCreator;
import org.neshan.ui.MapView;
import org.neshan.utils.BitmapUtils;
import org.neshan.vectorelements.Marker;

import java.util.List;

public class PlayActivity extends AppCompatActivity implements LocationListener {

    public static final String MOCK_PROVIDER = "NESHAN_MOCK";
    private MapView map;
    private VectorElementLayer markerLayer;

    private MaterialButton btnPlayTrack;
    private MaterialButton btnStopTrack;
    private ImageView ivMockStatus;

    private List<PosEntity> mockPoints;
    private MockEntity mock;
    private LocationManager locationManager;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        initViews();
        initMap();
        listener();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mock = (MockEntity) extras.getSerializable("mockData");
            if (mock != null) {
                AsyncTask.execute(() -> {
                    mockPoints = DatabaseClient.getInstance(this)
                            .getMockDatabase()
                            .getPosDao()
                            .getMockPos(mock.getId());

                    for (PosEntity pos :
                            mockPoints) {
                        LngLat lnglat = new LngLat(pos.getLng(), pos.getLat());
                        setLocationMarker(lnglat, false);
                        map.setFocalPointPosition(lnglat, 0);
                    }
                });

                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (locationManager.getProvider(MOCK_PROVIDER) == null) {
                    try {
                        locationManager.addTestProvider(MOCK_PROVIDER,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                0,
                                1);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                        allowForMock();

                    }
                }
            }
        }
    }

    private void allowForMock() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("select this app as mock location app in setting.")
                .setCancelable(false)
                .setPositiveButton("Ok", (dialogInterface, i) -> PlayActivity.this.finish())
                .create();

        alertDialog.show();
    }

    private void setLocationMarker(LngLat loc, boolean tracking) {
        MarkerStyleCreator markerStyleCreator = new MarkerStyleCreator();
        Bitmap trackingCircle = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
        if (!tracking) {
            trackingCircle = BitmapFactory.decodeResource(getResources(), R.drawable.nontrack_circle);
        }
        markerStyleCreator.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(trackingCircle));
        markerStyleCreator.setSize(16);
        Marker marker = new Marker(loc, markerStyleCreator.buildStyle());
        markerLayer.add(marker);
    }

    private void initMap() {
        map.setZoom(14f, 0f);
        map.getLayers().add(NeshanServices.createBaseMap(NeshanMapStyle.NESHAN));
        markerLayer = NeshanServices.createVectorElementLayer();
        map.getLayers().add(markerLayer);
    }

    private void initViews() {
        map = findViewById(R.id.map);
        btnPlayTrack = findViewById(R.id.btnPlayTrack);
        btnStopTrack = findViewById(R.id.btnStopTrack);
        ivMockStatus = findViewById(R.id.ivMockStatus);
    }

    @SuppressLint("MissingPermission")
    private void listener() {
        btnPlayTrack.setOnClickListener(v -> {
            btnPlayTrack.setVisibility(View.GONE);
            btnStopTrack.setVisibility(View.VISIBLE);
            ivMockStatus.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gpsActiveStatus));

            try {
                locationManager.setTestProviderEnabled(MOCK_PROVIDER, true);
                locationManager.requestLocationUpdates(MOCK_PROVIDER, 0, 0, this);

                for (PosEntity pos :
                        mockPoints) {
                    Location location = new Location(MOCK_PROVIDER);
                    location.setSpeed(pos.getSpeed());
                    location.setBearing(pos.getBearing());
                    location.setLatitude(pos.getLat());
                    location.setLongitude(pos.getLng());
                    location.setAccuracy(pos.getAccuracy());
                    long timeDiff = Math.abs(pos.getTime() - mock.getId());
                    Log.i("alizeyn-location", "timeDiff : " + timeDiff);
                    Log.i("alizeyn-location", "mockStart : " + mock.getId());
                    location.setTime(System.currentTimeMillis() + timeDiff);
                    location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos() + timeDiff);

                    locationManager.setTestProviderLocation(MOCK_PROVIDER, location);
                }
            } catch (SecurityException e) {
                e.printStackTrace();
                allowForMock();
            }


        });

        btnStopTrack.setOnClickListener(v -> {
            btnStopTrack.setVisibility(View.GONE);
            btnPlayTrack.setVisibility(View.VISIBLE);
            ivMockStatus.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gpsDeactivateStatus));

            locationManager.setTestProviderEnabled(MOCK_PROVIDER, false);
            locationManager.removeUpdates(this);

        });
    }

    @Override
    public void onLocationChanged(Location location) {
        setLocationMarker(new LngLat(location.getLongitude(), location.getLatitude()), true);
        Log.i("alizeyn-location-change", "onLocationChanged: " + location.getTime());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
