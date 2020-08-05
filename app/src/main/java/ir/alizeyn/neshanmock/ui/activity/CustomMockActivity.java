package ir.alizeyn.neshanmock.ui.activity;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.neshan.core.LngLat;
import org.neshan.core.LngLatVector;
import org.neshan.graphics.ARGB;
import org.neshan.layers.VectorElementLayer;
import org.neshan.services.NeshanMapStyle;
import org.neshan.services.NeshanServices;
import org.neshan.styles.AnimationStyleBuilder;
import org.neshan.styles.AnimationType;
import org.neshan.styles.LineStyleCreator;
import org.neshan.styles.MarkerStyleCreator;
import org.neshan.ui.MapView;
import org.neshan.utils.BitmapUtils;
import org.neshan.vectorelements.Line;
import org.neshan.vectorelements.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ir.alizeyn.neshanmock.R;
import ir.alizeyn.neshanmock.database.DatabaseClient;
import ir.alizeyn.neshanmock.database.MockEntity;
import ir.alizeyn.neshanmock.database.PosEntity;
import ir.alizeyn.neshanmock.mock.MockType;
import ir.alizeyn.neshanmock.request.RequestFactory;
import ir.alizeyn.neshanmock.request.RouteResponse;
import ir.alizeyn.neshanmock.request.WebServices;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomMockActivity extends AppCompatActivity {

    private MapView map;
    private VectorElementLayer layer;

    private LngLat origin;
    private LngLat dest;
    private List<LngLatVector> mockLine;

    private MaterialButton btnSelectOrigin;
    private MaterialButton btnSelectDest;
    private MaterialButton btnSaveMock;
    private MaterialButton btnRefresh;
    private ImageView ivCompass;
    private ImageView ivCenterMarker;
    private ProgressBar pb;
    private Dialog saveMockDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_mock);
        mockLine = new ArrayList<>();
        initMap();
        initViews();
        listeners();
    }

    private void initViews() {
        btnSelectOrigin = findViewById(R.id.btnSelectOrigin);
        btnSelectDest = findViewById(R.id.btnSelectDest);
        btnSaveMock = findViewById(R.id.btnSaveMock);
        btnRefresh = findViewById(R.id.btnRefresh);
        pb = findViewById(R.id.pb);
        ivCompass = findViewById(R.id.ivCompass);
        ivCenterMarker = findViewById(R.id.ivCenterMarker);
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
            layer.clear();
            origin = map.getFocalPointPosition();
            btnSelectOrigin.setVisibility(View.GONE);
            btnSelectDest.setVisibility(View.VISIBLE);
            setMarker(new LngLat(origin.getX(), origin.getY()), R.drawable.marker_origin);
        });

        btnSelectDest.setOnClickListener(v -> {
            dest = map.getFocalPointPosition();
            btnSelectDest.setVisibility(View.GONE);
            ivCenterMarker.setVisibility(View.GONE);

            WebServices webServices = RequestFactory.neshanWebServices();
            HashMap<String, String> params = new HashMap<>();
            params.put("origin", origin.getY() + "," + origin.getX());
            params.put("destination", dest.getY() + "," + dest.getX());
            params.put("avoidTrafficZone", "false");
            params.put("avoidOddEvenZone", "false");
            params.put("alternative", "false");
            Call<RouteResponse> call = webServices.getRoute(params);
            Log.i("alizeyn_request", "call: " + call.request().url());
            call.enqueue(new Callback<RouteResponse>() {
                @Override
                public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {

                    Log.i("alizeyn_request", "onResponse: " + call.request().url());
                    Log.i("alizeyn_request", "onResponse: " + response.code());

                    if (response.body() != null) {
                        RouteResponse.Route route = response.body().getRoutes().get(0);
                        for (LngLatVector lineVector :
                                route.getRouteLineVector()) {
                            setLine(lineVector, false);
                            mockLine.add(lineVector);
                        }

                        pb.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            btnSaveMock.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(CustomMockActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            btnSelectOrigin.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<RouteResponse> call, Throwable t) {
                    Log.i("alizeyn_request", "onFailure: " + t.getMessage());
                    Toast.makeText(CustomMockActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                    btnSelectOrigin.setVisibility(View.VISIBLE);
                }
            });
            pb.setVisibility(View.VISIBLE);

            setMarker(new LngLat(dest.getX(), dest.getY()), R.drawable.marker_dest);
        });

        btnSaveMock.setOnClickListener(v -> {
            saveMockDialog = createSaveMockDialog();
            saveMockDialog.show();
        });

        ivCompass.setOnClickListener(v -> {
            map.setBearing(0f, 0.2f);
        });

        btnRefresh.setOnClickListener(v -> {
            btnSelectDest.setVisibility(View.GONE);
            btnSaveMock.setVisibility(View.GONE);
            btnSelectOrigin.setVisibility(View.VISIBLE);
            layer.clear();
        });
    }

    private void setLine(LngLatVector lineVector, boolean tracking) {

        LineStyleCreator lineStyleCreator = new LineStyleCreator();
        lineStyleCreator.setWidth(8);
        ARGB greenColor = new ARGB(0Xff00ff00);
        ARGB redColor = new ARGB(0Xffff0000);
        lineStyleCreator.setColor(tracking ? greenColor : redColor);
        Line line = new Line(lineVector, lineStyleCreator.buildStyle());
        layer.add(line);
    }

    class SaveMockTask extends AsyncTask<List<LngLatVector>, Void, Void> {

        private MockEntity mockEntity;

        public SaveMockTask(MockEntity mockEntity) {
            this.mockEntity = mockEntity;
        }

        @Override
        protected void onPreExecute() {
            btnSaveMock.setVisibility(View.GONE);
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pb.setVisibility(View.GONE);
            btnSaveMock.setVisibility(View.VISIBLE);
            btnRefresh.performClick();
            ivCenterMarker.setVisibility(View.VISIBLE);
            Toast.makeText(CustomMockActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(List<LngLatVector>... lists) {

            for (LngLatVector lngLatVector :
                    mockLine) {
                for (int i = 0; i < lngLatVector.size(); i++) {
                    LngLat lngLat = lngLatVector.get(i);

                    PosEntity pos = new PosEntity(mockEntity.getId(),
                            lngLat.getX(),
                            lngLat.getY(),
                            30,
                            System.currentTimeMillis(),
                            SystemClock.elapsedRealtimeNanos(),
                            1,
                            0,
                            PlayCustomActivity.MOCK_PROVIDER);

                    DatabaseClient.getInstance(CustomMockActivity.this)
                            .getMockDatabase()
                            .getPosDao()
                            .insertPos(pos);
                }
            }

            return null;
        }
    }

    private Dialog createSaveMockDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setTitle("Save Mock");
        ViewGroup saveMockView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.dialog_save_mock, null);
        MaterialButton saveMock = saveMockView.findViewById(R.id.btnSaveMock);
        MaterialButton cancel = saveMockView.findViewById(R.id.btnCancel);
        TextInputEditText etDesc = saveMockView.findViewById(R.id.etDesc);

        saveMock.setOnClickListener(v -> {

            String desc = etDesc.getText() == null ? "No Description" : etDesc.getText().toString();
            MockEntity mock = new MockEntity(System.currentTimeMillis(), MockType.CUSTOM.name());
            mock.setDesc(desc);
            AsyncTask.execute(() ->
                    DatabaseClient.getInstance(this)
                            .getMockDatabase()
                            .getMockDao()
                            .insert(mock));
            dialog.dismiss();

            new SaveMockTask(mock).execute(mockLine);
            saveMockDialog.dismiss();
        });

        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.setContentView(saveMockView);
        return dialog;
    }

    private void setMarker(LngLat loc, int drawableId) {
        MarkerStyleCreator markerStyleCreator = new MarkerStyleCreator();
        Bitmap trackingCircle = BitmapFactory.decodeResource(getResources(), drawableId);
        markerStyleCreator.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(trackingCircle));
        markerStyleCreator.setSize(32);
        AnimationStyleBuilder animationStyleBuilder = new AnimationStyleBuilder();
        animationStyleBuilder.setSizeAnimationType(AnimationType.ANIMATION_TYPE_SPRING);

        markerStyleCreator.setAnimationStyle(animationStyleBuilder.buildStyle());

        Marker marker = new Marker(loc, markerStyleCreator.buildStyle());
        layer.add(marker);
    }
}
