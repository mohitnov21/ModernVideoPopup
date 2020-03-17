package comi.example.modern.modernvideopopup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import comi.example.modern.modernvideopopup.activity.ModernPlayerLandcsape;
import comi.example.modern.modernvideopopup.adapter.DirectoryAdapter;
import comi.example.modern.modernvideopopup.adapter.FilesAdapter;
import comi.example.modern.modernvideopopup.singleton.DataListsSingeton;

/**
 * Created by Instafeed2 on 10/24/2019.
 */

public class FilesActivity extends AppCompatActivity implements FilesAdapter.OnClickVideoListener {
    FilesAdapter filesAdapter;
    private DataListsSingeton dataListsSingeton;
    private int index;
    RecyclerView recyclerview_files;
    private ArrayList<ModernFiles> modernFiles;
    private ArrayList<ModernDirectory> modernDirectories;
    public static FilesActivity filesActivity;
    public SearchView searchView;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor preferenceEditor;
    boolean isGridType = false;
    public LinearLayoutManager linearLayoutManager;
    public  GridLayoutManager gridLayoutManager;
    String fileDirName=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.files_layout);
        filesActivity = this;
        DataListsSingeton.getInstance().setCurrentActivty(2);
        sharedPreferences
                = getSharedPreferences("MySharedPref",
                MODE_PRIVATE);

        preferenceEditor = sharedPreferences.edit();
        isGridType = sharedPreferences.getBoolean("is_grid_files",false);
        Log.e("isisGridType",""+isGridType);

        index = getIntent().getIntExtra("index", -1);
        Log.e("FilesActivity : ", "index :" + index);
        modernDirectories = new ArrayList<>();
        modernFiles = new ArrayList<>();
        filesActivity = this;
        modernDirectories.addAll(DataListsSingeton.getInstance().getDirectoryData());
        Log.e("FilesActivity outside2", "count : " + modernDirectories.size());
       /* for (int i = 0; i < modernDirectories.get(index).getInsideData().size(); i++) {
            String nmae = modernDirectories.get(index).getInsideData().get(i).getName();
            Log.e("FilesActivity outside2", "count : " + nmae);


        }*/
       fileDirName = modernDirectories.get(index).getName();
       getSupportActionBar().setTitle(fileDirName);
        modernFiles.addAll(modernDirectories.get(index).getInsideData());
        Log.e("FilesActivity : ", "" + modernFiles.size());
        recyclerview_files = (RecyclerView) findViewById(R.id.recyclerview_files);
        linearLayoutManager = new LinearLayoutManager(FilesActivity.this);
        gridLayoutManager = new GridLayoutManager(FilesActivity.this, 2);
        filesAdapter = new FilesAdapter(FilesActivity.this, index, modernFiles, this,isGridType);
        recyclerview_files.setLayoutManager(isGridType ? gridLayoutManager : linearLayoutManager);
        recyclerview_files.setAdapter(filesAdapter);
   }
    public void setGridTypeinPreference()
    {
        preferenceEditor.putBoolean("is_grid_files",isGridType);
        preferenceEditor.commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.file_viewtype)
        {
            if(modernFiles!=null)
            {
                if(modernFiles.size()>0)
                {
                    isGridType = !isGridType;
                    setGridTypeinPreference();
                    item.setIcon(isGridType ? R.drawable.ic_view_list_black_24dp : R.drawable.ic_grid_on_white_24dp);
                    filesAdapter = new FilesAdapter(FilesActivity.this, index, modernFiles, this,isGridType);
                    recyclerview_files.setLayoutManager(isGridType ? gridLayoutManager : linearLayoutManager);
                    recyclerview_files.setAdapter(filesAdapter);
                    return true;

                }
            }

        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu with items using MenuInflator
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        // Initialise menu item search bar
        // with id and take its object
        MenuItem searchViewItem
                = menu.findItem(R.id.search_bar);

        MenuItem fileViewType
                = menu.findItem(R.id.file_viewtype);
        fileViewType.setIcon(isGridType ? R.drawable.ic_view_list_black_24dp : R.drawable.ic_grid_on_white_24dp);

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
                        filesAdapter.filter(newText);
                        return false;
                    }
                });

        return super.onCreateOptionsMenu(menu);
    }

    public void showAlertDialog(Activity activity, String title,
                                CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if (title != null)
            builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.setNegativeButton("Open Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        requestManageOverlayPermission();
                    }
                });
        // builder.setPositiveButton("OK", null);
        // builder.setNegativeButton("Cancel", null);
        builder.show();
    }

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

    private static int REQUEST_SUPER_CONTROL = 101;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SUPER_CONTROL) {
            // check whether permission was granted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(
                            FilesActivity.this,
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

    @Override
    public void onClick(int position) {
        //   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /*if (!Settings.canDrawOverlays(FilesActivity.this)) {
                showAlertDialog(
                        FilesActivity.this,
                        "Permission needed",
                        getString(R.string.System_permission_dialog_message));
            } else {*/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        ModernPlayerLandcsape.currentIndex=-1;
        ModernPlayerLandcsape.relaunchedScreen=false;
        ModernPlayerLandcsape.flag_fullscreen=false;
        Intent intent = new Intent(FilesActivity.this, ModernPlayerLandcsape.class);
        intent.putExtra("index", position);
        intent.putExtra("index_dir", index);
        intent.putExtra("view_type_video", 0);
        startActivity(intent);
        // }
        //  }
        //  requestManageOverlayPermission();
        //  Log.e("onClick","onClick mg :"+position);
    }

    @Override
    public void onLongClick() {

    }
}
