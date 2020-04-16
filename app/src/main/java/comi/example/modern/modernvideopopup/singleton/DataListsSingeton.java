package comi.example.modern.modernvideopopup.singleton;

import java.util.ArrayList;
import java.util.Collection;

import comi.example.modern.modernvideopopup.ModernDirectory;
import comi.example.modern.modernvideopopup.ModernFiles;

/**
 * Created by Instafeed2 on 10/25/2019.
 */


public class DataListsSingeton  {

    private static DataListsSingeton INSTANCE = null;

    // other instance variables can be here
    private ArrayList<ModernDirectory> modernDirectories ;
    private ArrayList<ModernFiles> videosFiles ;
    private boolean DataLaoding = false;
    private DataListsSingeton() {};
    private int currentActivty=0;
    public static DataListsSingeton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataListsSingeton();
        }
        return(INSTANCE);
    }
    public ArrayList<ModernDirectory> getDirectoryData()
    {
        return modernDirectories;
    }

    public void setModernDirectories(ArrayList<ModernDirectory> modernDirectories) {
        this.modernDirectories = modernDirectories;
    }

    public int getCurrentActivty() {
        return currentActivty;
    }

    public void setCurrentActivty(int currentActivty) {
        this.currentActivty = currentActivty;
    }

    public ArrayList<ModernFiles> getVideosData() {
        return videosFiles;
    }

    public void setVideosFiles(ArrayList<ModernFiles> videosFiles) {
        this.videosFiles = videosFiles;
    }

    public boolean isDataLaoding() {
        return DataLaoding;
    }

    public void setDataLaoding(boolean dataLaoding) {
        DataLaoding = dataLaoding;
    }
}