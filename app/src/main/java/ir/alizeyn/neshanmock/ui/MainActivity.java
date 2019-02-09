package ir.alizeyn.neshanmock.ui;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.neshan.core.LngLat;
import org.neshan.services.NeshanMapStyle;
import org.neshan.services.NeshanServices;
import org.neshan.ui.MapView;

import java.util.Locale;

import ir.alizeyn.neshanmock.R;
import ir.alizeyn.neshanmock.util.GPSManager;
import ir.alizeyn.neshanmock.util.Tools;

public class MainActivity extends AppCompatActivity {

    private boolean firstRecenter;

    private MapView map;


    private ImageView ivGpsStatus;
    private TextView tvGpsInfo;
    private TextView tvProvider;
    private TextView tvSpeed;
    private TextView tvAccuracy;
    private TextView tvTime;
    private View vStartTracking;
    private View vStopTracking;
    private View vCustomMock;
    private View vNavigation;
    private View vLoadTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Tools.getLocationPermission(this, true);

        map = findViewById(R.id.map);
        //set map focus position
        LngLat focalPoint = new LngLat(59.5521763, 36.3076283);
        map.setFocalPointPosition(focalPoint, 0f);
        map.setZoom(14f, 0f);
        //add basemap layer
        map.getLayers().add(NeshanServices.createBaseMap(NeshanMapStyle.NESHAN));

        ivGpsStatus = findViewById(R.id.ivStatus);
        tvGpsInfo = findViewById(R.id.tvGpsInfo);
        tvProvider = findViewById(R.id.tvProvider);
        tvSpeed = findViewById(R.id.tvSpeed);
        tvAccuracy = findViewById(R.id.tvAccuracy);
        tvTime = findViewById(R.id.tvTime);
        vStartTracking = findViewById(R.id.vStartTracking);
        vStopTracking = findViewById(R.id.vStopTracking);
        vCustomMock = findViewById(R.id.vCustomMock);
        vNavigation = findViewById(R.id.vNavigation);
        vLoadTrack = findViewById(R.id.vLoadTrack);

        setMenuInitState();

        GPSManager.getInstance(this).setOnGPSFixed(new GPSManager.OnGPSFixedListener() {
            @Override
            public void onLocationChanged(Location loc, LngLat pos) {
                if (!firstRecenter) {
                    map.setFocalPointPosition(new LngLat(loc.getLongitude(), loc.getLatitude()), 1f);
                    map.setZoom(17f, 0f);
                    firstRecenter = true;
                }
                ivGpsStatus.setImageResource(R.drawable.circle_green);
                setLocationInfo(loc);
            }
        });

        GPSManager.getInstance(this).start();


        vStartTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMenuStartState();
            }
        });

        vStopTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMenuInitState();
            }
        });

        vCustomMock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMenuCustomMock();
            }
        });
    }

    @Override
    protected void onDestroy() {
        GPSManager.getInstance(this).stop();
        super.onDestroy();
    }


    private void setLocationInfo(Location loc) {
        String gpsInfo = "GPS Info "
                + String.format(Locale.getDefault(), "%.5f", loc.getLatitude())
                + " , "
                + String.format(Locale.getDefault(), "%.5f", loc.getLongitude());
        tvGpsInfo.setText(gpsInfo);

        String provider = "Provider: " + loc.getProvider();
        tvProvider.setText(provider);

        String speed = "Speed: " + (int) loc.getSpeed();
        tvSpeed.setText(speed);

        String accuracy = "Accuracy: " + loc.getAccuracy();
        tvAccuracy.setText(accuracy);

        String time = "Time: " + Tools.convertTime(loc.getTime());
        tvTime.setText(time);
    }

    private void setMenuInitState() {
        vStartTracking.setVisibility(View.VISIBLE);
        vStopTracking.setVisibility(View.GONE);
        vCustomMock.setVisibility(View.VISIBLE);
        vNavigation.setVisibility(View.GONE);
        vLoadTrack.setVisibility(View.VISIBLE);
    }

    private void setMenuStartState() {
        vStartTracking.setVisibility(View.GONE);
        vStopTracking.setVisibility(View.VISIBLE);
        vCustomMock.setVisibility(View.GONE);
        vNavigation.setVisibility(View.GONE);
        vLoadTrack.setVisibility(View.GONE);
    }


    private void setMenuCustomMock() {
        vStartTracking.setVisibility(View.GONE);
        vStopTracking.setVisibility(View.GONE);
        vCustomMock.setVisibility(View.VISIBLE);
        vNavigation.setVisibility(View.GONE);
        vLoadTrack.setVisibility(View.GONE);
    }


}
