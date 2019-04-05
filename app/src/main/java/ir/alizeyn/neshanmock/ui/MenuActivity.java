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

public class MenuActivity extends AppCompatActivity {

    MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        map = findViewById(R.id.map);
        //set map focus position
        LngLat focalPoint = new LngLat(59.5521763, 36.3076283);
        map.setFocalPointPosition(focalPoint, 0f);
        map.setZoom(14f, 0f);
        //add basemap layer
        map.getLayers().add(NeshanServices.createBaseMap(NeshanMapStyle.NESHAN));


        GPSManager.getInstance(this).setOnGPSFixed(new GPSManager.OnGPSFixedListener() {
            @Override
            public void onLocationChanged(Location loc, LngLat pos) {
                map.setFocalPointPosition(new LngLat(loc.getLongitude(), loc.getLatitude()), 1f);
                map.setZoom(17f, 0f);

            }
        });

        GPSManager.getInstance(this).start();
    }
}
