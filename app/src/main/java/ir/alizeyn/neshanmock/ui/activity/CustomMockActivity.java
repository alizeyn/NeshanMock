package ir.alizeyn.neshanmock.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.material.button.MaterialButton;

import org.neshan.core.LngLat;
import org.neshan.layers.VectorElementLayer;
import org.neshan.services.NeshanMapStyle;
import org.neshan.services.NeshanServices;
import org.neshan.ui.MapView;

import androidx.appcompat.app.AppCompatActivity;
import ir.alizeyn.neshanmock.R;

public class CustomMockActivity extends AppCompatActivity {

    private MapView map;
    private VectorElementLayer layer;

    private MaterialButton btnSelectOrigin;
    private MaterialButton btnSelectDest;
    private MaterialButton btnSaveMock;
    private ImageView ivCompass;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_mock);
        initMap();
        initViews();
        listeners();
    }

    private void initViews() {
        btnSelectOrigin = findViewById(R.id.btnSelectOrigin);
        btnSelectDest = findViewById(R.id.btnSelectDest);
        btnSaveMock = findViewById(R.id.btnSaveMock);
        pb = findViewById(R.id.pb);
        ivCompass = findViewById(R.id.ivCompass);
    }

    private void initMap() {
        map = findViewById(R.id.map);
        map.setZoom(10f, 0f);
        map.setFocalPointPosition(new LngLat(51.3890, 35.6892), 0f);
        map.getLayers().add(NeshanServices.createBaseMap(NeshanMapStyle.NESHAN));
        layer = NeshanServices.createVectorElementLayer();
        map.getLayers().add(layer);
    }

    private void listeners() {

        btnSelectOrigin.setOnClickListener(v -> {
            btnSelectOrigin.setVisibility(View.GONE);
            btnSelectDest.setVisibility(View.VISIBLE);
        });

        btnSelectDest.setOnClickListener(v -> {
            btnSelectDest.setVisibility(View.GONE);
            pb.setVisibility(View.VISIBLE);
        });

        btnSaveMock.setOnClickListener(v -> {


        });

        ivCompass.setOnClickListener(v -> {
            map.setBearing(0f, 0.2f);
        });
    }
}
