package comi.example.modern.modernvideopopup.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.jpardogo.android.googleprogressbar.library.FoldingCirclesDrawable;

import java.io.File;

import comi.example.modern.modernvideopopup.Helper;
import comi.example.modern.modernvideopopup.MainActivity;
import comi.example.modern.modernvideopopup.R;
import comi.example.modern.modernvideopopup.adapter.DirectoryAdapter;
import comi.example.modern.modernvideopopup.singleton.DataListsSingeton;

public class SplashScreen extends AppCompatActivity {
    private File root;
    Helper helper;
    private static final String TAG = "SplashScreen";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashh);
        helper = new Helper();
        //  getSupportActionBar().hide();

/*
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
*/
        if (isWriteStoragePermissionGranted()) {
            new LoadDataAsyncInSplash().execute();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                }
            }, 3000);
        }
    }
    public boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Permission is granted2");
                return true;
            } else {

                Log.e(TAG, "Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.e(TAG, "Permission is granted2");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                Log.d(TAG, "External storage2");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                    //resume tasks needing this permission
                    new LoadDataAsyncInSplash().execute();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            startActivity(new Intent(SplashScreen.this, MainActivity.class));
                            finish();
                        }
                    }, 3000);
                    //  loadData();
                } else {
                    // progress.dismiss();
                }
                break;

            case 3:
                Log.d(TAG, "External storage1");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                    //resume tasks needing this permission
                    // SharePdfFile();
                } else {
                    // progress.dismiss();
                }
                break;
        }
    }

    class LoadDataAsyncInSplash extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            //getting SDcard root path
            DataListsSingeton.getInstance().setDataLaoding(true);

            root = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath());
            helper.getDir1(root, SplashScreen.this);
            DataListsSingeton.getInstance().setDataLaoding(false);

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

        }
    }

}
