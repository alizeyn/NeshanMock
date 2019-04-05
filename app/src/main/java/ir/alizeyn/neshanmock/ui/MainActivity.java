package ir.alizeyn.neshanmock.ui;

import android.location.Location;
import android.os.Bundle;

import org.neshan.core.LngLat;
import org.neshan.services.NeshanMapStyle;
import org.neshan.services.NeshanServices;
import org.neshan.ui.MapView;

import androidx.appcompat.app.AppCompatActivity;
import ir.alizeyn.neshanmock.R;
import ir.alizeyn.neshanmock.util.GPSManager;
import ir.alizeyn.neshanmock.util.Tools;

public class MainActivity extends AppCompatActivity {

    private boolean firstRecenter;

    private MapView map;


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
        map.getLayers().add(NeshanServices.createBaseMap(NeshanMapStyle.NESHAN));


        GPSManager.getInstance(this).setOnGPSFixed((loc, pos) -> {
            if (!firstRecenter) {
                map.setFocalPointPosition(new LngLat(loc.getLongitude(), loc.getLatitude()), 1f);
                map.setZoom(17f, 0f);
                firstRecenter = true;
            }
//                ivGpsStatus.setImageResource(R.drawable.circle_green);
        });

        GPSManager.getInstance(this).start();


    }

    @Override
    protected void onDestroy() {
        GPSManager.getInstance(this).stop();
        super.onDestroy();
    }





}
