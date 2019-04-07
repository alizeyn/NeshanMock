package ir.alizeyn.neshanmock.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;
import ir.alizeyn.neshanmock.R;
import ir.alizeyn.neshanmock.util.Tools;

public class MenuActivity extends AppCompatActivity {

    private MaterialButton btnRecordTrack;
    private MaterialButton btnMockArchive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        initViews();
        listeners();

        Tools.getLocationPermission(this, true);
        Tools.getStoragePermisstion(this, true);
    }


    private void initViews() {
        btnRecordTrack = findViewById(R.id.btnRecordTrack);
        btnMockArchive = findViewById(R.id.btnMockArchive);
    }

    private void listeners() {
        btnRecordTrack.setOnClickListener(view -> startActivity(new Intent(MenuActivity.this, TrackActivity.class)));
        btnMockArchive.setOnClickListener(view -> startActivity(new Intent(MenuActivity.this, MockArchiveActivity.class)));
    }

}
