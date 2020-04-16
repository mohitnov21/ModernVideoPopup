package comi.example.modern.modernvideopopup;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.Resource;
import com.jpardogo.android.googleprogressbar.library.FoldingCirclesDrawable;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import comi.example.modern.modernvideopopup.activity.ModernPlayerLandcsape;
import comi.example.modern.modernvideopopup.adapter.DirectoryAdapter;
import comi.example.modern.modernvideopopup.adapter.FilesAdapter;
import comi.example.modern.modernvideopopup.singleton.DataListsSingeton;
import comi.example.modern.modernvideopopup.util.AutoFitGridLayoutManager;


public class MainActivity extends AppCompatActivity implements FilesAdapter.OnClickVideoListener {

    private static final String TAG = "MainActivity";
    private File root;
    ArrayList<ModernDirectory> modernDirectories;
    private LinearLayout view;
    DirectoryAdapter directoryAdapter;
    public static MainActivity c;
    RecyclerView directoryRecyclerView;
    Helper helper;
    public boolean isFiles=false;
    Menu menu;
    GoogleProgressBar mBar;
    boolean isGridType = false;
    boolean isGridTypeFiles = false;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor preferenceEditor;
    private ArrayList<ModernFiles> allVideos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFiles =false;
        setContentView(R.layout.activity_main);
        c = this;
        DataListsSingeton.getInstance().setCurrentActivty(1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences
                = getSharedPreferences("MySharedPref",
                MODE_PRIVATE);

        preferenceEditor = sharedPreferences.edit();
        isGridType = sharedPreferences.getBoolean("is_grid_dir", false);
        isGridTypeFiles = sharedPreferences.getBoolean("is_grid_files", false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mBar = (GoogleProgressBar) findViewById(R.id.google_progress);

        directoryRecyclerView = (RecyclerView) findViewById(R.id.directory_recyclerview);
        //  view = (LinearLayout) findViewById(R.id.folder_layout);
        helper = new Helper();
        modernDirectories = new ArrayList<>();
     //   if (isWriteStoragePermissionGranted()) {
            //getting SDcard root path
            new LoadDataAsync().execute();
            //   loadData();


      //  }


    }
    public SearchView searchView;

    public void requestManageOverlayPermission() {
        // first check whether the permission is granted
        // NB! this is only for API level 23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Toast.makeText(FirstScreen.this,
            // "requestManageOverlayPermission",Toast.LENGTH_SHORT).show();

            if (!Settings.canDrawOverlays(this)) {
                // Toast.makeText(FirstScreen.this,
                // "requestManageOverlayPermission inside",Toast.LENGTH_SHORT).show();
                try {
                    // construct an intent
                    Intent intent = new Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    // request the permission
                    startActivityForResult(intent, REQUEST_SUPER_CONTROL);
                } catch (Exception e) {

                    try {
                        // construct an intent
                        Intent intent = new Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        // request the permission
                        startActivityForResult(intent, REQUEST_SUPER_CONTROL);
                    } catch (Exception e1) {


                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }
            }

        }
    }

    private static int REQUEST_SUPER_CONTROL = 51;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SUPER_CONTROL) {
            // check whether permission was granted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(
                            MainActivity.this,
                            "Popup mode cannot be used without this Permission",
                            Toast.LENGTH_LONG).show();
                    // SYSTEM_ALERT_WINDOW permission not granted...
                } else {
					/*Intent in = new Intent(MainActivity.this,
							NavigationActivity.class);
					in.putExtra("fullscreenmode", "popumode");
					startActivity(in);*/
                    // SYSTEM_ALERT_WINDOW permission granted
                }
            }
        }
    }

    public boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Permission is granted1");
                return true;
            } else {

                Log.e(TAG, "Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.e(TAG, "Permission is granted1");
            return true;
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
                    new LoadDataAsync().execute();

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

    private void loadData() {
        try {
            mBar.setIndeterminateDrawable(new FoldingCirclesDrawable.Builder(this)
                    .colors(getResources().getIntArray(R.array.progressLoader)).build());
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            Log.d("mBar", "onCreate() returned: " + e);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                //getting SDcard root path
                root = new File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath());
                helper.getDir1(root, MainActivity.this);
                modernDirectories.addAll(DataListsSingeton.getInstance().getDirectoryData());

            }
        }).start();


    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchViewItem
                = menu.findItem(R.id.search_bar);
        searchView
                = (SearchView) MenuItemCompat
                .getActionView(searchViewItem);

        // attach setOnQueryTextListener
        // to search view defined above
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {

                    // Override onQueryTextSubmit method
                    // which is call
                    // when submitquery is searched

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        // If the list contains the search query
                        // than filter the adapter
                        // using the filter method
                        // with the query as its argument
                        //   if (list.contains(query)) {
                        filesAdapter.filter(query);
                       /* }
                        else {
                            // Search query not found in List View
                            Toast
                                    .makeText(FilesActivity.this,
                                            "Not found",
                                            Toast.LENGTH_LONG)
                                    .show();
                        }*/
                        return false;
                    }

                    // This method is overridden to filter
                    // the adapter according to a search query
                    // when the user is typing search
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if(isFiles)
                        {
                            if(filesAdapter==null) {
                                setUpFilesView();
                            }
                            filesAdapter.filter(newText);

                        }else
                        {
                            directoryAdapter.filter(newText);

                        }
                        return false;
                    }
                });

        return true;
    }

    FilesAdapter filesAdapter;

    private void setActionIcon(boolean setmuteicon) {
        MenuItem item = menu.findItem(R.id.action_viewtype);

        if (menu != null) {
            if (setmuteicon) {
                //mute it
                //this does nothing
                item.setIcon(R.drawable.ic_grid_on_white_24dp);
            } else {
                setGridTypeinPreference();
                Log.e("action_viewtype1", "" + isGridType);
                gridLayoutManager = new GridLayoutManager(MainActivity.this, 3);
                directoryAdapter = new DirectoryAdapter(MainActivity.this, modernDirectories, isGridType);
                directoryRecyclerView.setLayoutManager(isGridType ? gridLayoutManager : linearLayoutManager);
                directoryRecyclerView.setAdapter(directoryAdapter);

                item.setIcon(isGridType ? R.drawable.ic_view_list_black_24dp : R.drawable.ic_grid_on_white_24dp);

            }
        }
    }

    public void setGridTypeinPreference() {
        preferenceEditor.putBoolean("is_grid_dir", isGridType);
        preferenceEditor.commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            //noinspection SimplifiableIfStatement
            case R.id.action_viewtype:
                Log.e("action_viewtype", "" + isGridType);
                isGridType = !isGridType;
                setGridTypeinPreference();
                setActionIcon(false);
                return true;

            case R.id.folders:
                isFiles=false;
                setActionIcon(false);
                return true;
            case R.id.videos:
                isFiles=true;
                setUpFilesView();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void setUpFilesView()
    {
        if (modernDirectories != null && modernDirectories.size() > 0) {
            isGridTypeFiles = sharedPreferences.getBoolean("is_grid_files", false);
            gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
               allVideos = new ArrayList<>();
                 allVideos.addAll(DataListsSingeton.getInstance().getVideosData());
            filesAdapter = new FilesAdapter(MainActivity.this, 0, allVideos,
                    this, isGridTypeFiles);
            directoryRecyclerView.setLayoutManager(isGridTypeFiles ? gridLayoutManager : linearLayoutManager);

            directoryRecyclerView.setAdapter(filesAdapter);
        }
    }
    @Override
    public void onClick(int position) {
        //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /*if (!Settings.canDrawOverlays(FilesActivity.this)) {
                showAlertDialog(
                        FilesActivity.this,
                        "Permission needed",
                        getString(R.string.System_permission_dialog_message));
            } else {*/
        ModernPlayerLandcsape.currentIndex=-1;
        ModernPlayerLandcsape.relaunchedScreen=false;
        ModernPlayerLandcsape.flag_fullscreen=false;

        Intent intent = new Intent(MainActivity.this, ModernPlayerLandcsape.class);
        intent.putExtra("index", position);
        intent.putExtra("index_dir", position);
        intent.putExtra("view_type_video", 1);
        startActivity(intent);
        // }
        // }
        //  requestManageOverlayPermission();
        //  Log.e("onClick","onClick mg :"+position);
    }

    @Override
    public void onLongClick() {

    }

    @Override
    public void onBackPressed() {

        showLeaveMEssage();
    }
    public AlertDialog leaveDialog;
    public void showLeaveMEssage()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Leaving already?");
        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                leaveDialog.dismiss();
            }
        });
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        leaveDialog = builder.create();
        leaveDialog.show();
    }
    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;

    class LoadDataAsync extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                mBar.setIndeterminateDrawable(new FoldingCirclesDrawable.Builder(MainActivity.this)
                        .colors(getResources().getIntArray(R.array.progressLoader)).build());
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                Log.d("mBar", "onCreate() returned: " + e);
            }
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            //getting SDcard root path
            root = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath());
       //    helper.getDir1(root, MainActivity.this);
            boolean dataLoadrunning =DataListsSingeton.getInstance().isDataLaoding();
            while (dataLoadrunning) {
                dataLoadrunning =DataListsSingeton.getInstance().isDataLaoding();
                if(!dataLoadrunning)
                {
                    break;
                }
                Log.e("dataLoadrunning",""+DataListsSingeton.getInstance().isDataLaoding());
            }
            modernDirectories.addAll(DataListsSingeton.getInstance().getDirectoryData());

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            setGridTypeinPreference();
            directoryAdapter = new DirectoryAdapter(MainActivity.this, modernDirectories, isGridType);
            linearLayoutManager = new LinearLayoutManager(MainActivity.this);
            gridLayoutManager = new GridLayoutManager(MainActivity.this, 3);
            directoryRecyclerView.setLayoutManager(isGridType ? gridLayoutManager : linearLayoutManager);

            directoryRecyclerView.setAdapter(directoryAdapter);

            if (mBar != null)
                mBar.setVisibility(View.GONE);
        }
    }
}
