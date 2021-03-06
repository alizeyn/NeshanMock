package ir.alizeyn.neshanmock.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.button.MaterialButton;

import ir.alizeyn.neshanmock.R;
import ir.alizeyn.neshanmock.util.Tools;

public class MenuActivity extends AppCompatActivity {

    private MaterialButton btnRecordTrack;
    private MaterialButton btnMockArchive;
    private MaterialButton btnMockImport;
    private MaterialButton btnCustomMock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        initViews();
        listeners();

        Tools.getLocationPermission(this, true);
        Tools.getStoragePermisstion(this, true);

    }


    private void initViews() {
        btnRecordTrack = findViewById(R.id.btnRecordTrack);
        btnMockArchive = findViewById(R.id.btnMockArchive);
        btnMockImport = findViewById(R.id.btnMockImport);
        btnCustomMock = findViewById(R.id.btnCustomMock);
    }

    private void listeners() {
        btnRecordTrack.setOnLongClickListener(view -> {
            startActivity(new Intent(MenuActivity.this, AuthAcitivity.class));
            return true;
        });

        btnRecordTrack.setOnClickListener(view -> startActivity(new Intent(MenuActivity.this, TrackActivity.class)));
        btnMockArchive.setOnClickListener(view -> startActivity(new Intent(MenuActivity.this, MockArchiveActivity.class)));
        btnMockImport.setOnClickListener(view -> startActivity(new Intent(MenuActivity.this, ImportActivity.class)));
        btnCustomMock.setOnClickListener(view -> startActivity(new Intent(MenuActivity.this, CustomMockActivity.class)));
    }

}
