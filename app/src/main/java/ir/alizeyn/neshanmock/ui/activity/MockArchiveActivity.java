package ir.alizeyn.neshanmock.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;
import ir.alizeyn.neshanmock.R;
import ir.alizeyn.neshanmock.database.DatabaseClient;
import ir.alizeyn.neshanmock.database.MockEntity;
import ir.alizeyn.neshanmock.ui.adapter.MockArchiveAdapter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.List;

public class MockArchiveActivity extends AppCompatActivity {

    private RecyclerView rlMockArchive;
    private MockArchiveAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_archive);
        initViews();
        listener();
        adapter = new MockArchiveAdapter(this);
        rlMockArchive.setLayoutManager(new LinearLayoutManager(this));
        rlMockArchive.setAdapter(adapter);
        new showMockArchiveTask().execute();
    }

    private void listener() {

    }

    private void initViews() {
        rlMockArchive = findViewById(R.id.rlMockArchive);
    }

    class showMockArchiveTask extends AsyncTask<Void, Void, List<MockEntity>> {

        @Override
        protected List<MockEntity> doInBackground(Void... voids) {
            return DatabaseClient.getInstance(MockArchiveActivity.this)
                    .getMockDatabase()
                    .getMockDao()
                    .getAll();
        }

        @Override
        protected void onPostExecute(List<MockEntity> mockEntities) {
            adapter.setData(mockEntities);
        }
    }
}
