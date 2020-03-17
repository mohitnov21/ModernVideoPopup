package comi.example.modern.modernvideopopup.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import comi.example.modern.modernvideopopup.FilesActivity;
import comi.example.modern.modernvideopopup.MainActivity;
import comi.example.modern.modernvideopopup.ModernDirectory;
import comi.example.modern.modernvideopopup.R;

/**
 * Created by Instafeed2 on 10/24/2019.
 */

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.MyViewHolder> {
    private String[] mDataset;
    boolean isGridType;
    private MainActivity mainActivity;
    ArrayList<ModernDirectory> modernDirectories;
    public DirectoryAdapter(MainActivity mainActivity, ArrayList<ModernDirectory> modernDirectories) {
        this.mainActivity = mainActivity;
        this.modernDirectories = modernDirectories;


    }

    public DirectoryAdapter(MainActivity mainActivity, ArrayList<ModernDirectory> modernDirectories, boolean isGridType) {
        this.mainActivity = mainActivity;
        this.modernDirectories = modernDirectories;
        this.isGridType = isGridType;

    }


    // Create new views (invoked by the layout manager)
    @Override
    public DirectoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View v;
        Log.e("onCreateViewHolder","action_viewtype "+isGridType);
        if(isGridType) {
            // create a new view
            v = (View) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_view_item_grid, parent, false);
        }else
        {
             v = (View) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.direcotry_layout, parent, false);
        }
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ModernDirectory modernDirectory = modernDirectories.get(position);
     //   Log.e("videocursor outside21", "count : " + modernDirectories.get(position).getInsideData().get(position) );

        holder.name.setText(modernDirectory.getName());
        holder.size.setText(modernDirectory.getSize()+" videos ");
        holder.directory_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("videocursor outside21", "count : " + modernDirectories.get(position).getInsideData().size() );
                Intent intent = new Intent(mainActivity, FilesActivity.class);
                intent.putExtra("index",position);
                mainActivity.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return modernDirectories.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name,size;
        public LinearLayout directory_open;

        public MyViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.directory_name);
            size = v.findViewById(R.id.directory_size);;
            directory_open = v.findViewById(R.id.directory_open);;
        }
    }
}
