package ir.alizeyn.neshanmock.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import org.neshan.core.LngLat;
import org.neshan.geometry.PointGeom;
import org.neshan.layers.VectorElementLayer;
import org.neshan.services.NeshanMapStyle;
import org.neshan.services.NeshanServices;
import org.neshan.styles.AnimationStyle;
import org.neshan.styles.AnimationStyleBuilder;
import org.neshan.styles.AnimationType;
import org.neshan.styles.BaseMarkerStyleCreator;
import org.neshan.styles.MarkerStyle;
import org.neshan.styles.MarkerStyleCreator;
import org.neshan.ui.MapView;
import org.neshan.utils.BitmapUtils;
import org.neshan.vectorelements.Marker;

import java.util.Date;
import java.util.Locale;

import androidx.core.content.ContextCompat;
import ir.alizeyn.neshanmock.R;
import ir.alizeyn.neshanmock.util.GPSManager;

public class TrackActivity extends AppCompatActivity {

    private MapView map;

    private VectorElementLayer markerLayer;

    private MaterialButton btnRecordTrack;
    private MaterialButton btnStopTrack;

    private TextView tvLatValue;
    private TextView tvLngValue;
    private TextView tvSpeedValue;
    private TextView tvAccValue;
    private TextView tvTimeValue;
    private TextView tvBearingValue;

    private ImageView ivGpsStatus;

    private boolean tracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        initViews();
        initMap();
        listener();

        // Start getting gps
        GPSManager.getInstance(this).setOnGPSFixed((loc, pos) -> {
            LngLat currentPos = new LngLat(loc.getLongitude(), loc.getLatitude());
            map.setFocalPointPosition(currentPos, 0f);
            map.setZoom(17.5f, 0f);
            ivGpsStatus.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gpsActiveStatus));
            setLocationDetails(loc);
            setLocationMarker(currentPos);

        });

    }

    private void initMap() {
        map.setZoom(14f, 0f);
        map.getLayers().add(NeshanServices.createBaseMap(NeshanMapStyle.NESHAN));
        markerLayer = NeshanServices.createVectorElementLayer();
        map.getLayers().add(markerLayer);
    }

    private void initViews() {
        map = findViewById(R.id.map);

        btnRecordTrack = findViewById(R.id.btnRecordTrack);
        btnStopTrack = findViewById(R.id.btnStopTrack);

        tvLatValue = findViewById(R.id.tvLatValue);
        tvLngValue = findViewById(R.id.tvLngValue);
        tvSpeedValue = findViewById(R.id.tvSpeedValue);
        tvAccValue = findViewById(R.id.tvAccValue);
        tvTimeValue = findViewById(R.id.tvTimeValue);
        tvBearingValue = findViewById(R.id.tvBearingValue);

        ivGpsStatus = findViewById(R.id.ivGpsStatus);

    }

    private void listener() {

        btnRecordTrack.setOnClickListener(v -> {
            tracking = true;
            btnRecordTrack.setVisibility(View.GONE);
            btnStopTrack.setVisibility(View.VISIBLE);
        });

        btnStopTrack.setOnClickListener(v -> {
            tracking = false;
            btnRecordTrack.setVisibility(View.VISIBLE);
            btnStopTrack.setVisibility(View.GONE);
        });
    }

    private void setLocationDetails(Location loc) {

        String lat = String.format(Locale.getDefault(), "%.5f", loc.getLatitude());
        tvLatValue.setText(lat);

        String lng = String.format(Locale.getDefault(), "%.5f", loc.getLongitude());
        tvLngValue.setText(lng);

        String speed = String.format(Locale.getDefault(), "%.2f", loc.getSpeed());
        tvSpeedValue.setText(speed);

        String acc = String.format(Locale.getDefault(), "%.2f", loc.getAccuracy());
        tvAccValue.setText(acc);

        String bearing = String.format(Locale.getDefault(), "%.2f", loc.getBearing());
        tvBearingValue.setText(bearing);

        tvTimeValue.setText(DateFormat.format("hh:mm:ss", loc.getTime()));
    }

    private void setLocationMarker(LngLat loc) {
        MarkerStyleCreator markerStyleCreator = new MarkerStyleCreator();
        Bitmap trackingCircle = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
        if (!tracking) {
            trackingCircle = BitmapFactory.decodeResource(getResources(), R.drawable.nontrack_circle);
        }
        markerStyleCreator.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(trackingCircle));
        markerStyleCreator.setSize(16);
        AnimationStyleBuilder animationStyleBuilder = new AnimationStyleBuilder();
        animationStyleBuilder.setSizeAnimationType(AnimationType.ANIMATION_TYPE_SPRING);

        markerStyleCreator.setAnimationStyle(animationStyleBuilder.buildStyle());

        Marker marker = new Marker(loc, markerStyleCreator.buildStyle());
        markerLayer.add(marker);
    }
}
