package ir.alizeyn.neshanmock.ui.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import ir.alizeyn.neshanmock.R;
import ir.alizeyn.neshanmock.database.DatabaseClient;
import ir.alizeyn.neshanmock.database.MockEntity;
import ir.alizeyn.neshanmock.database.PosEntity;
import ir.alizeyn.neshanmock.mock.MockShareModel;
import ir.alizeyn.neshanmock.ui.fragment.CustomMockListFragment;
import ir.alizeyn.neshanmock.ui.fragment.TrackMockListFragment;

public class ImportActivity extends AppCompatActivity {

    public static final int IMPORT_MOCK_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Import Mock");
        }
    }

    public void importMock(View view) {
        Intent intent = new Intent()
                .setAction(Intent.ACTION_GET_CONTENT)
                .setType("*/*");
        startActivityForResult(intent, IMPORT_MOCK_CODE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IMPORT_MOCK_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();

                    try {
                        ObjectInputStream in = new ObjectInputStream(getContentResolver().openInputStream(uri));
                        MockShareModel shareMock = (MockShareModel) in.readObject();
                        MockEntity mock = shareMock.getMockEntity();
                        List<PosEntity> poses = shareMock.getMockPoses();
                        AsyncTask.execute(() -> {
                            DatabaseClient.getInstance(this)
                                    .getMockDatabase()
                                    .getMockDao()
                                    .insert(mock);

                            for (PosEntity pos :
                                    poses) {
                                DatabaseClient.getInstance(this)
                                        .getMockDatabase()
                                        .getPosDao()
                                        .insertPos(pos);
                            }

                            new Handler(Looper.getMainLooper()).post(() -> {
                                Toast.makeText(this, "Imported", Toast.LENGTH_SHORT).show();
                            });

                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
