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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.linearref.LengthIndexedLine;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
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

public class PlayCustomActivity extends AppCompatActivity implements LocationListener {

    public static final String MOCK_PROVIDER = "gps";
    private MapView map;
    private VectorElementLayer layerMove;
    private VectorElementLayer layerStatic;

    private MaterialButton btnPlayTrack;
    private MaterialButton btnStopTrack;
    private ImageView ivMockStatus;
    private TableLayout tableDetails;
    private TextView tvLatValue;
    private TextView tvLngValue;
    private TextView tvSpeedValue;
    private TextView tvAccValue;
    private TextView tvTimeValue;
    private TextView tvProviderValue;
    private DiscreteSeekBar sbSpeed;


    private List<PosEntity> mockPoints;
    private LengthIndexedLine lengthIndexedLine;
    private MockEntity mock;
    private LocationManager locationManager;
    private LngLatVector lineVector;

    private Handler gpsHandler = new Handler();

    private volatile int speed = 25;
    private double ratio = 0.00006;

    double index = 0;
    private Runnable provideGpsRunnable;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_custom);
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

                    lengthIndexedLine = new LengthIndexedLine(Tools.convertLineToLineString(lineVector));


//                    for (int i = 0; i < 100; i++) {
//                    Coordinate c1 = lengthIndexedLine.extractPoint(0);
//                    Coordinate c2 = lengthIndexedLine.extractPoint(0.00006);
//                    Log.i("alizeyn_distance", "c1 : " + c1.y + ", " + c1.x);
//                    Log.i("alizeyn_distance", "c2 : " + c2.y + ", " + c2.x);
//                    Log.i("alizeyn_distance", "diff: " + Tools.distance(new LngLat(c1.y, c1.x), new LngLat(c2.y, c2.x)));
//                    }

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
                .setPositiveButton("Ok", (dialogInterface, i) -> PlayCustomActivity.this.finish())
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

    private Line setLine(LngLatVector lngLatVector, VectorElementLayer layer, boolean tracking) {

        LineStyleCreator lineStyleCreator = new LineStyleCreator();
        lineStyleCreator.setWidth(8);
        ARGB greenColor = new ARGB(0Xff00ff00);
        ARGB redColor = new ARGB(0Xffff0000);
        lineStyleCreator.setColor(tracking ? greenColor : redColor);
        Line line = new Line(lngLatVector, lineStyleCreator.buildStyle());
        layer.add(line);
        return line;
    }

    private void initMap() {
        map.setZoom(14f, 0f);
        map.getLayers().add(NeshanServices.createBaseMap(NeshanMapStyle.NESHAN));
        layerMove = NeshanServices.createVectorElementLayer();
        layerStatic = NeshanServices.createVectorElementLayer();
        map.getLayers().add(layerStatic);
        map.getLayers().add(layerMove);
    }

    private void initViews() {
        map = findViewById(R.id.map);
        btnPlayTrack = findViewById(R.id.btnPlayTrack);
        btnStopTrack = findViewById(R.id.btnStopTrack);
        ivMockStatus = findViewById(R.id.ivMockStatus);

        tvLatValue = findViewById(R.id.tvLatValue);
        tvLngValue = findViewById(R.id.tvLngValue);
        tvSpeedValue = findViewById(R.id.tvSpeedValue);
        tvAccValue = findViewById(R.id.tvAccValue);
        tvTimeValue = findViewById(R.id.tvTimeValue);
        tvProviderValue = findViewById(R.id.tvProviderValue);
        sbSpeed = findViewById(R.id.sbSpeed);

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
            sbSpeed.setVisibility(View.VISIBLE);
            ivMockStatus.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gpsActiveStatus));

            provideGpsRunnable = () -> {

                try {
                    Log.i("alizeyn_thread", ": runned");
                    addMockProvider();

                    Coordinate c1;
                    Coordinate c2;
                    if (lengthIndexedLine.isValidIndex(index)) {
                        c1 = lengthIndexedLine.extractPoint(index);
                        c2 = lengthIndexedLine.extractPoint(index + ratio);
                        index += ratio;
                    } else {
                        Toast.makeText(this, "finished", Toast.LENGTH_SHORT).show();
                        btnStopTrack.performClick();
                        return;
                    }

                    double distance = Tools.distance(new LngLat(c1.y, c1.x), new LngLat(c2.y, c2.x));
                    double delay = (distance / (speed / 3.6)) * 1000;
                    Location location = new Location(MOCK_PROVIDER);
                    location.setSpeed((float) (speed / 3.6));
                    location.setBearing(0);
                    location.setLatitude(c1.x);
                    location.setLongitude(c1.y);
                    location.setAccuracy((float) ratio);
                    location.setTime(System.currentTimeMillis());
                    location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());

                    locationManager.setTestProviderLocation(MOCK_PROVIDER, location);

                    gpsHandler.postDelayed(provideGpsRunnable, (long) delay);

                } catch (SecurityException e) {
                    e.printStackTrace();
                    allowForMock();
                }

            };

            gpsHandler.post(provideGpsRunnable);

        });

        btnStopTrack.setOnClickListener(v -> {
            btnStopTrack.setVisibility(View.GONE);
            btnPlayTrack.setVisibility(View.VISIBLE);
            sbSpeed.setVisibility(View.GONE);
            ivMockStatus.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gpsDeactivateStatus));

            locationManager.setTestProviderEnabled(MOCK_PROVIDER, false);
            locationManager.removeUpdates(this);
            gpsHandler.removeCallbacks(provideGpsRunnable);

            layerMove.clear();
            cleanLocationDetails();
        });

        sbSpeed.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                speed = seekBar.getProgress();
            }
        });
    }

    LngLat lastLocation;

    @Override
    public void onLocationChanged(Location location) {
        runOnUiThread(() -> {
            setLocationDetails(location);
        });
//        map.setFocalPointPosition(new LngLat(location.getLongitude(), location.getLatitude()), 0f);
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
        map.moveToCameraBounds(new Bounds(lineVector.get(0), lineVector.get((int) (lineVector.size() - 1))),
                new ViewportBounds(new ViewportPosition(0, tableDetails.getHeight()), new ViewportPosition(map.getWidth(), map.getHeight())),
                true,
                0.5f);
    }


}
