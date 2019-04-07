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
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.List;

import ir.alizeyn.neshanmock.R;
import ir.alizeyn.neshanmock.database.DatabaseClient;
import ir.alizeyn.neshanmock.database.MockEntity;
import ir.alizeyn.neshanmock.database.PosEntity;
import ir.alizeyn.neshanmock.mock.MockShareModel;

public class ImportActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        Intent intent = getIntent();

        Uri uri = intent.getData();
        Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();


        String type;
        ContentResolver cr = this.getContentResolver();
        type = cr.getType(uri);
        Log.i("alizeyn_loc", "onCreate: " + uri.toString());
        Log.i("alizeyn_loc", "onCreate: " + type);
        Log.i("alizeyn_loc", "onCreate: " + intent.getType());
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

}
