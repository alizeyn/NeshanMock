package ir.alizeyn.neshanmock.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Database;
import ir.alizeyn.neshanmock.R;
import ir.alizeyn.neshanmock.database.DatabaseClient;
import ir.alizeyn.neshanmock.database.MockEntity;

import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class MockArchiveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_archive);

        List<MockEntity> mockEntities = DatabaseClient.getInstance(this)
                .getMockDatabase()
                .getMockDao()
                .getAll();

        for (MockEntity m :
                mockEntities) {
            Log.i("alizeyn-mock", "lat " + m.getId());
        }
    }
}
