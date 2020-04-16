package comi.example.modern.modernvideopopup.adapter;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import comi.example.modern.modernvideopopup.FilesActivity;
import comi.example.modern.modernvideopopup.MainActivity;
import comi.example.modern.modernvideopopup.ModernDirectory;
import comi.example.modern.modernvideopopup.ModernFiles;
import comi.example.modern.modernvideopopup.R;
import comi.example.modern.modernvideopopup.activity.ModernPlayerLandcsape;
import comi.example.modern.modernvideopopup.activity.popupActivity;
import comi.example.modern.modernvideopopup.service.FloatingViewService;
import comi.example.modern.modernvideopopup.singleton.DataListsSingeton;

/**
 * Created by Instafeed2 on 10/25/2019.
 */

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.MyViewHolder> {
    private final Context mainActivity;
    int index;
    OnClickVideoListener onClickVideoListener;
    private ArrayList<ModernFiles> modernFiles;
    private ArrayList<ModernFiles> modernFilesCopy;
    boolean isGridType;


    public boolean showPopup;

    public FilesAdapter(Context mainActivity, int index) {
        this.mainActivity = mainActivity;

    }

    public FilesAdapter(Context mainActivity, int index, ArrayList<ModernFiles> modernFiles, OnClickVideoListener onClickVideoListener, boolean isGridType) {
        this.mainActivity = mainActivity;
        this.index = index;
        this.onClickVideoListener = onClickVideoListener;
        Log.e("isisGridType", "" + isGridType);
        this.isGridType = isGridType;

        this.modernFiles = modernFiles;
        this.modernFilesCopy = new ArrayList<ModernFiles>();
        this.modernFilesCopy.addAll(modernFiles);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (!isGridType) {
            view = (View) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.files_item_layout, parent, false);
        } else {
            view = (View) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.grid_viewtype, parent, false);

        }
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        modernFiles.clear();
        if (charText.length() == 0) {
            modernFiles.addAll(modernFilesCopy);
        } else {
            for (ModernFiles wp : modernFilesCopy) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    modernFiles.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if (isGridType) {
            holder.vidTitle.setText(modernFiles.get(position).getName());
            holder.vidDuration.setText(modernFiles.get(position).getDuration());
            Glide.with(mainActivity)
                    .load(modernFiles.get(position).getImg())
                    .centerCrop()
                    .placeholder(R.drawable.vlx_icon)
                    .into(holder.vid_previewGrid);
            holder.linearLayoutVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("modernFilesCopy.size()", "location : " + modernFilesCopy.size());
                    for (int k = 0; k < modernFilesCopy.size(); k++) {
                        if (modernFiles.get(position).getLocation().equals(modernFilesCopy.get(k).getLocation())) {
                            onClickVideoListener.onClick(k);
                            Log.e("matched", "location : " + k);
                        }
                    }
                    if (showPopup) {
                        Intent intent = new Intent(mainActivity, FloatingViewService.class);
                        intent.putExtra("index", position);
                        intent.putExtra("index_dir", index);

                        if (!isMyServiceRunning(FloatingViewService.class, mainActivity)) {
                            intent.putExtra("update_service", false);

                        } else {
                            Log.e("filesadapter outside2", "count : ");
                            intent.putExtra("update_service", true);

                        }
                        mainActivity.startService(intent);
                    } else {
                  /*  Intent intent = new Intent(mainActivity, ModernPlayerLandcsape.class);
                    intent.putExtra("index", position);
                    intent.putExtra("index_dir", index);
                    mainActivity.startActivity(intent);*/

                    }

                }
            });

        } else {
            holder.video_name.setText(modernFiles.get(position).getName());
            holder.duration_textview_files.setText(modernFiles.get(position).getDuration());
            Glide.with(mainActivity)
                    .load(modernFiles.get(position).getImg())
                    .centerCrop()
                    .placeholder(R.drawable.vlx_icon)
                    .into(holder.imageView);

            holder.date_files.setText("" + getFileSize(Long.parseLong(modernFiles.get(position).getDate())));
            holder.linearLayoutVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int k = 0; k < modernFilesCopy.size(); k++) {
                        if (modernFiles.get(position).getLocation().equals(modernFilesCopy.get(k).getLocation())) {
                            onClickVideoListener.onClick(k);
                            Log.e("matched", "location : " + k);
                        }
                    }
                    if (showPopup) {
                        Intent intent = new Intent(mainActivity, FloatingViewService.class);
                        intent.putExtra("index", position);
                        intent.putExtra("index_dir", index);

                        if (!isMyServiceRunning(FloatingViewService.class, mainActivity)) {
                            intent.putExtra("update_service", false);

                        } else {
                            Log.e("filesadapter outside2", "count : ");
                            intent.putExtra("update_service", true);

                        }
                        mainActivity.startService(intent);
                    } else {
                  /*  Intent intent = new Intent(mainActivity, ModernPlayerLandcsape.class);
                    intent.putExtra("index", position);
                    intent.putExtra("index_dir", index);
                    mainActivity.startActivity(intent);*/

                    }

                }
            });
        }
    }

    public interface OnClickVideoListener {
        void onClick(int position);

        void onLongClick();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass, Context con) {
        ActivityManager manager = (ActivityManager) con
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String getFileSize(long size) {
        if (size <= 0)
            return "0";

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    @Override
    public int getItemCount() {

        if (modernFiles == null) {
            return 0;
        } else {
            Log.e("size ", "modernFiles.size(); " + modernFiles.size());
            return modernFiles.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView video_name, date_files, duration_textview_files;
        ImageView imageView;
        LinearLayout linearLayoutVideo;

        ImageView vid_previewGrid, moreOption;
        TextView vidTitle, vidDuration;

        public MyViewHolder(View itemView) {
            super(itemView);
            if (isGridType) {
                vid_previewGrid = (ImageView) itemView.findViewById(R.id.vid_preview);
                moreOption = (ImageView) itemView.findViewById(R.id.vid_options);
                vidDuration = (TextView) itemView.findViewById(R.id.vidDuration);
                vidTitle = (TextView) itemView.findViewById(R.id.vid_title);
            } else {
                video_name = (TextView) itemView.findViewById(R.id.video_name);
                date_files = (TextView) itemView.findViewById(R.id.date_files);
                duration_textview_files = (TextView) itemView.findViewById(R.id.duration_textview_files);
                imageView = (ImageView) itemView.findViewById(R.id.imageview_files);
            }
            linearLayoutVideo = (LinearLayout) itemView.findViewById(R.id.video_click);
        }
    }
}

