package ir.alizeyn.neshanmock.ui.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ir.alizeyn.neshanmock.R;
import ir.alizeyn.neshanmock.database.DatabaseClient;
import ir.alizeyn.neshanmock.database.MockEntity;
import ir.alizeyn.neshanmock.database.PosEntity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import org.neshan.core.Bounds;
import org.neshan.core.LngLat;
import org.neshan.core.LngLatVector;
import org.neshan.core.ViewportBounds;
import org.neshan.core.ViewportPosition;
import org.neshan.graphics.ARGB;
import org.neshan.layers.VectorElementLayer;
import org.neshan.services.NeshanMapStyle;
import org.neshan.services.NeshanServices;
import org.neshan.styles.LineStyleCreator;
import org.neshan.ui.MapView;
import org.neshan.vectorelements.Line;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PlayActivity extends AppCompatActivity implements LocationListener {

    public static final String MOCK_PROVIDER = "gps";
    private MapView map;
    private VectorElementLayer layerMove;
    private VectorElementLayer layerStatic;
    private LngLatVector lineVector;

    private MaterialButton btnPlayTrack;
    private MaterialButton btnStopTrack;
    private MaterialButton btnShare;
    private ImageView ivMockStatus;
    private TableLayout tableDetails;
    private TextView tvLatValue;
    private TextView tvLngValue;
    private TextView tvSpeedValue;
    private TextView tvAccValue;
    private TextView tvTimeValue;
    private TextView tvProviderValue;


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

                    lineVector = new LngLatVector();
                    for (PosEntity pos : mockPoints) {
                        LngLat lnglat = new LngLat(pos.getLng(), pos.getLat());
                        lineVector.add(lnglat);
                    }
                    setLine(lineVector, layerStatic, false);
                });

                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            }
        }

    }

    @SuppressLint("MissingPermission")
    private void addMockProvider() {
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

        locationManager.setTestProviderEnabled(MOCK_PROVIDER, true);
        locationManager.requestLocationUpdates(MOCK_PROVIDER, 0, 0, this);

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

    private Line setLine(LngLat p1, LngLat p2, VectorElementLayer layer, boolean tracking) {

        LineStyleCreator lineStyleCreator = new LineStyleCreator();
        lineStyleCreator.setWidth(8);
        ARGB greenColor = new ARGB(0Xff00ff00);
        ARGB redColor = new ARGB(0Xffff0000);
        lineStyleCreator.setColor(tracking ? greenColor : redColor);
        LngLatVector lngLatVector = new LngLatVector();
        lngLatVector.add(p1);
        lngLatVector.add(p2);
        Line line = new Line(lngLatVector, lineStyleCreator.buildStyle());
        layer.add(line);
        return line;
    }

    private Line setLine(LngLatVector lineVector, VectorElementLayer layer, boolean tracking) {

        LineStyleCreator lineStyleCreator = new LineStyleCreator();
        lineStyleCreator.setWidth(8);
        ARGB greenColor = new ARGB(0Xff00ff00);
        ARGB redColor = new ARGB(0Xffff0000);
        lineStyleCreator.setColor(tracking ? greenColor : redColor);
        Line line = new Line(lineVector, lineStyleCreator.buildStyle());
        layer.add(line);
        return line;
    }

    private void initMap() {
        map.setZoom(14f, 0f);
        map.getLayers().add(NeshanServices.createBaseMap(NeshanMapStyle.NESHAN));
        layerStatic = NeshanServices.createVectorElementLayer();
        layerMove = NeshanServices.createVectorElementLayer();
        map.getLayers().add(layerStatic);
        map.getLayers().add(layerMove);
    }

    private void initViews() {
        map = findViewById(R.id.map);
        btnPlayTrack = findViewById(R.id.btnPlayTrack);
        btnStopTrack = findViewById(R.id.btnStopTrack);
        ivMockStatus = findViewById(R.id.ivMockStatus);
        btnShare = findViewById(R.id.btnShare);

        tvLatValue = findViewById(R.id.tvLatValue);
        tvLngValue = findViewById(R.id.tvLngValue);
        tvSpeedValue = findViewById(R.id.tvSpeedValue);
        tvAccValue = findViewById(R.id.tvAccValue);
        tvTimeValue = findViewById(R.id.tvTimeValue);
        tvProviderValue = findViewById(R.id.tvProviderValue);

        tableDetails = findViewById(R.id.tableDetails);
    }

    private void setLocationDetails(Location loc) {
        String lat = String.format(Locale.getDefault(), "%.5f", loc.getLatitude());
        tvLatValue.setText(lat);
        String lng = String.format(Locale.getDefault(), "%.5f", loc.getLongitude());
        tvLngValue.setText(lng);
        String speed = String.format(Locale.getDefault(), "%.2f", loc.getSpeed() * 3.6);
        tvSpeedValue.setText(speed);
        String acc = String.format(Locale.getDefault(), "%.2f", loc.getAccuracy());
        tvAccValue.setText(acc);
        tvProviderValue.setText(loc.getProvider());
        tvTimeValue.setText(DateFormat.format("hh:mm:ss", loc.getTime()));
    }

    private void cleanLocationDetails() {
        tvLatValue.setText("...");
        tvLngValue.setText("...");
        tvSpeedValue.setText("...");
        tvAccValue.setText("...");
        tvProviderValue.setText("...");
        tvTimeValue.setText("...");
    }

    @SuppressLint("MissingPermission")
    private void listener() {
        btnPlayTrack.setOnClickListener(v -> {
            btnPlayTrack.setVisibility(View.GONE);
            btnStopTrack.setVisibility(View.VISIBLE);
            ivMockStatus.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gpsActiveStatus));

            try {
                addMockProvider();

                for (PosEntity pos :
                        mockPoints) {
                    Location location = new Location(MOCK_PROVIDER);
                    location.setSpeed(pos.getSpeed());
                    location.setBearing(pos.getBearing());
                    location.setLatitude(pos.getLat());
                    location.setLongitude(pos.getLng());
                    location.setAccuracy(pos.getAccuracy());
                    long timeDiff = Math.abs(pos.getTime() - mock.getId());
                    location.setTime(System.currentTimeMillis() + timeDiff);
                    location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos() + timeDiff);

                    Observable<Location> locationPublisher = Observable.just(location)
                            .observeOn(Schedulers.computation())
                            .subscribeOn(Schedulers.computation())
                            .delay(timeDiff, TimeUnit.MILLISECONDS);

                    locationPublisher.subscribe(new Observer<Location>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Location location) {
                            locationManager.setTestProviderLocation(MOCK_PROVIDER, location);
                            Log.i("alizeyn-location", "onNext: published");
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });

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
//            locationManager.removeTestProvider(MOCK_PROVIDER);
            locationManager.removeUpdates(this);

            layerMove.clear();
            cleanLocationDetails();
        });

        btnShare.setOnClickListener(v -> {
            LngLat origin = lineVector.get((int) (lineVector.size() - 1));
            Uri.Builder directionsBuilder = new Uri.Builder()
                    .scheme("https")
                    .authority("www.google.com")
                    .appendPath("maps")
                    .appendPath("dir")
                    .appendPath("")
                    .appendQueryParameter("api", "1")
                    .appendQueryParameter("destination", origin.getY() + "," + origin.getX());

            startActivity(new Intent(Intent.ACTION_VIEW, directionsBuilder.build()));
        });

    }

    LngLat lastLocation;

    @Override
    public void onLocationChanged(Location location) {
        setLocationDetails(location);

        if (lastLocation == null) {
            lastLocation = new LngLat(location.getLongitude(), location.getLatitude());
            return;
        }

        LngLat lngLat = new LngLat(location.getLongitude(), location.getLatitude());
        setLine(lastLocation, lngLat, layerMove, true);
//        setLocationMarker(lngLat, true);
        lastLocation = lngLat;
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (lineVector.size() > 0) {
            map.moveToCameraBounds(new Bounds(lineVector.get(0), lineVector.get((int) (lineVector.size() - 1))),
                    new ViewportBounds(new ViewportPosition(0, tableDetails.getHeight()), new ViewportPosition(map.getWidth(), map.getHeight())),
                    true,
                    0.5f);
        }
    }
}
