package ir.alizeyn.neshanmock.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

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
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

import androidx.core.content.ContextCompat;
import ir.alizeyn.neshanmock.R;
import ir.alizeyn.neshanmock.database.DatabaseClient;
import ir.alizeyn.neshanmock.database.MockDao;
import ir.alizeyn.neshanmock.database.MockDatabase;
import ir.alizeyn.neshanmock.database.MockEntity;
import ir.alizeyn.neshanmock.database.PosDao;
import ir.alizeyn.neshanmock.database.PosEntity;
import ir.alizeyn.neshanmock.mock.MockType;
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
    private TextView tvProviderValue;

    private ImageView ivGpsStatus;

    private boolean tracking;
    private boolean firstRecenter = true;
    private long mockStartTime;
    private MockEntity mock;

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
            float mapMoveSpeed = 1f;
            if (firstRecenter) {
                firstRecenter = false;
                mapMoveSpeed = 0f;
            }
            map.setFocalPointPosition(currentPos, mapMoveSpeed);
            map.setZoom(17.5f, 0f);
            ivGpsStatus.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gpsActiveStatus));
            setLocationDetails(loc);
            setLocationMarker(currentPos);

            if (tracking) {
                PosEntity posEntity = new PosEntity(mockStartTime,
                        loc.getLongitude(),
                        loc.getLatitude(),
                        loc.getSpeed(),
                        loc.getTime(),
                        loc.getElapsedRealtimeNanos(),
                        loc.getAccuracy(),
                        loc.getBearing(),
                        loc.getProvider());

                AsyncTask.execute(() -> DatabaseClient.getInstance(this)
                        .getMockDatabase()
                        .getPosDao()
                        .insertPos(posEntity));
            }

        });
        GPSManager.getInstance(this).start();

    }

    private void initMap() {
        map.getOptions().setUserInput(false);
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
        tvProviderValue = findViewById(R.id.tvProviderValue);

        ivGpsStatus = findViewById(R.id.ivGpsStatus);

    }

    private void listener() {

        btnRecordTrack.setOnClickListener(v -> {
            tracking = true;
            btnRecordTrack.setVisibility(View.GONE);
            btnStopTrack.setVisibility(View.VISIBLE);
            mockStartTime = System.currentTimeMillis();
            mock = new MockEntity(mockStartTime, MockType.TRACK.name());

            AsyncTask.execute(() -> DatabaseClient.getInstance(this)
                    .getMockDatabase()
                    .getMockDao()
                    .insert(mock));

        });

        btnStopTrack.setOnClickListener(v -> {
            Dialog saveMockDialog = createSaveMockDialog();
            saveMockDialog.show();
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
        tvProviderValue.setText(loc.getProvider());
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

    private Dialog createSaveMockDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setTitle("Save Mock");
        ViewGroup saveMockView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.dialog_save_mock, null);
        MaterialButton saveMock = saveMockView.findViewById(R.id.btnSaveMock);
        MaterialButton cancel = saveMockView.findViewById(R.id.btnCancel);
        TextInputEditText etDesc = saveMockView.findViewById(R.id.etDesc);

        saveMock.setOnClickListener(v -> {
            tracking = false;
            btnRecordTrack.setVisibility(View.VISIBLE);
            btnStopTrack.setVisibility(View.GONE);
            String desc = etDesc.getText() == null ? "No Description" : etDesc.getText().toString();
            mock.setDesc(desc);
            AsyncTask.execute(() ->
                    DatabaseClient.getInstance(this)
                            .getMockDatabase()
                            .getMockDao()
                            .update(mock));
            dialog.dismiss();

            AsyncTask.execute(() -> {
                List<MockEntity> mockEntities = DatabaseClient.getInstance(this)
                        .getMockDatabase()
                        .getMockDao()
                        .getAll();

                for (MockEntity m :
                        mockEntities) {
                    Log.i("alizeyn-mock", "lat " + m.getDesc());
                }
            });

        });

        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.setContentView(saveMockView);
        return dialog;
    }

    @Override
    public void onBackPressed() {
        if (tracking) {
            btnStopTrack.performClick();
        } else {
            super.onBackPressed();
        }
    }
}