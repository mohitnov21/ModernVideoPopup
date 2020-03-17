package comi.example.modern.modernvideopopup;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import comi.example.modern.modernvideopopup.singleton.DataListsSingeton;

/**
 * Created by Instafeed2 on 10/24/2019.
 */

public class Helper {
    ArrayList<String> folders = new ArrayList<>();
    private ArrayList<ModernFiles> insideData = new ArrayList<>();
    private ModernDirectory modernDirectory;
    //  private ArrayList<ModernDirectory> modernDirectories = new ArrayList<>();
    ArrayList<String> foldersNames;
    DataListsSingeton dataListsSingeton;
    String title, path, duration, mimeType, id, folderName, date_addded;
    private ArrayList<ModernDirectory> modernDirectories = new ArrayList<>();
    private ArrayList<String> allDirectoryNames = new ArrayList<>();

    public ArrayList<ModernDirectory> getDir1(File dir, MainActivity mainActivity) {
        folders.clear();
        foldersNames = new ArrayList<>();
        dataListsSingeton = DataListsSingeton.getInstance();
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    folders.add(listFile[i].getName());
                }
            }
        }
        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] proj = {MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.SIZE
                , MediaStore.Video.Media.MIME_TYPE, MediaStore.Video.Media._ID};

        modernDirectories.clear();
        insideData.clear();

        Cursor videocursor = mainActivity.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                proj, null, null, MediaStore.Video.Media.DATE_TAKEN + " DESC");
        String[] thumbColumns = {
                MediaStore.Video.Thumbnails.DATA, MediaStore.Video.Thumbnails.VIDEO_ID};
        int matchCount = 0, existingDirPosition = -1;
        ModernDirectory modernDirectory;
        assert videocursor != null;
        if (videocursor.getCount() > 0) {
            Log.e("videocursor outside", "count : " + videocursor.getCount());
            insideData.clear();

            while (videocursor.moveToNext()) {
                matchCount = 0;
                id = videocursor.getString(
                        videocursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));


                folderName = videocursor.getString(

                        videocursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
                title = videocursor.getString(
                        videocursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                Log.e("VideoGallery", "folderName: " + folderName + " Title : " + title);
                //   Log.e("videocursor", "Title : " +title);
                path = videocursor.getString(
                        videocursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                duration = videocursor.getString(
                        videocursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                Log.e("videocursor", "duration : " + duration);
                mimeType = videocursor.getString(
                        videocursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                date_addded = videocursor.getString(
                        videocursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                String bMap = getThumbnailPathForLocalFile(mainActivity, Long.parseLong(id));
                duration = convertMillieToHMmSs(Long.parseLong(duration));
                //  duration = secondsToString(Integer.parseInt(duration));
                Log.e("videocursor", "duration : " + duration);
                //  Log.e("videocursor", "Title : " + length + "path :" + path);
                insideData.add(new ModernFiles(id, title
                        , path, duration, mimeType, bMap, date_addded));
                for (int l = 0; l < allDirectoryNames.size(); l++) {
                 //   Log.e("allDirectoryNames", "iterating  l : " + l + " name :" + allDirectoryNames.get(l) + " folderName :" + folderName);

                    if (allDirectoryNames.get(l).equalsIgnoreCase(folderName)) {
                        existingDirPosition = l;
                        break;
                    } else {
                        existingDirPosition = -1;

                    }
                }
               // Log.e("videocursor", "existingDirPosition : " + existingDirPosition);
                if (existingDirPosition != -1) {
                    ModernDirectory modernDirectory1 = modernDirectories.get(existingDirPosition);
                    modernDirectory1.addFile(new ModernFiles(id, title
                            , path, duration, mimeType, bMap, date_addded));
                } else {
                    modernDirectory = new ModernDirectory(folderName);
                    allDirectoryNames.add(folderName);
                    modernDirectory.addFile(new ModernFiles(id, title
                            , path, duration, mimeType, bMap, date_addded));
                    modernDirectories.add(modernDirectory);

                }

            }
            Log.e("videocursor", "insideData : " +videocursor.getCount());


        }
        videocursor.close();
        Collections.sort(modernDirectories, ModernDirectory.DirectoryNameComparator);
        dataListsSingeton.setModernDirectories(modernDirectories);
        dataListsSingeton.setVideosFiles(insideData);
        return modernDirectories;

    }

    public ArrayList<ModernFiles> getVideos()
    {
     return insideData;
    }
    public static String convertMillieToHMmSs(long millie) {
        long seconds = (millie / 1000);
        long second = seconds % 60;
        long minute = (seconds / 60) % 60;
        long hour = (seconds / (60 * 60)) % 24;

        String result = "";
        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            return String.format("%02d:%02d", minute, second);
        }

    }

    public static boolean isFileExist(String filepath) {
        File file = new File(filepath);
        //Do something
        return file.exists();

    }


    public static void OpenApp(Context con) {
        if (con == null) {
            return;
        }

        if (isAppIsInBackground(con.getApplicationContext())) {
            try {
                Intent intent = new Intent(con,
                        MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // You
                // need
                // this
                // if
                // starting
                // the activity from a service
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                con.startActivity(intent);
            } catch (Exception e) {
                // Toast.makeText(getApplicationContext(), "" + e,
                // Toast.LENGTH_SHORT).show();
            }
        }
    }


    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am
                    .getRunningAppProcesses();
            try {
                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(context.getPackageName())) {
                                isInBackground = false;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am
                    .getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    private String placeZeroIfNeede(int number) {
        return (number >= 10) ? Integer.toString(number) : String.format("0%s", Integer.toString(number));
    }

    private String secondsToString(int pTime) {
        final int min = pTime / 60;
        final int sec = pTime - (min * 60);

        final String strMin = placeZeroIfNeede(min);
        final String strSec = placeZeroIfNeede(sec);
        return String.format("%s:%s", strMin, strSec);
    }


    public static String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA};
    public static String[] mediaColumns = {MediaStore.Video.Media._ID};

    public static String getThumbnailPathForLocalFile(Context context,
                                                      long fileId) {

        // long fileId = getFileId(context, fileUri);

        MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(),
                fileId, MediaStore.Video.Thumbnails.MICRO_KIND, null);

        Cursor thumbCursor = null;
        try {

            thumbCursor = context.getContentResolver().query(
                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID + " = "
                            + fileId, null, null);

            if (thumbCursor.moveToFirst()) {
                String thumbPath = thumbCursor.getString(thumbCursor
                        .getColumnIndex(MediaStore.Video.Thumbnails.DATA));

                return thumbPath;
            }

        } finally {
        }

        return null;
    }


    public ArrayList<String> getfiles1(File dir) {
        File listFile[] = dir.listFiles();
        ModernDirectory modernDirectory = null;
        ArrayList<ModernFiles> insideData = new ArrayList<>();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {
                //   Log.e("outside ", "listFile[i] :" + listFile[i].getName());

                if (listFile[i].isDirectory()) {
                    folders.add(listFile[i].getName());
                    getfiles1(listFile[i]);
                    //       Log.e("inside :", "listFile[i] :" + listFile[i].getName());
                } else {
                }


            }
//            if (folders.size() > 0)
//                modernDirectory = new ModernDirectory(dir.getName(), "", insideData);

        }
        return folders;
    }

}
