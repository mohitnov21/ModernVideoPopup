package comi.example.modern.modernvideopopup.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.app.Service;
import android.content.Intent;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import android.app.Notification;
import android.app.PendingIntent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import comi.example.modern.modernvideopopup.ModernDirectory;
import comi.example.modern.modernvideopopup.ModernFiles;
import comi.example.modern.modernvideopopup.R;
import comi.example.modern.modernvideopopup.Utilities;
import comi.example.modern.modernvideopopup.VodView;
import comi.example.modern.modernvideopopup.activity.ModernPlayerLandcsape;
import comi.example.modern.modernvideopopup.singleton.DataListsSingeton;
import comi.example.modern.modernvideopopup.util.Constants;

import static comi.example.modern.modernvideopopup.activity.ModernPlayerLandcsape.currentIndex;

public class NotificationService extends Service {
    private final String LOG_TAG = "NotificationService";
    private Utilities utilities;
    Intent intent;
    private ArrayList<ModernFiles> modernFiles;
    private ArrayList<ModernDirectory> modernDirectories;

    int index_dir;
    int view_type_video = 0;
    private MediaPlayer mediaPlayer;
    private boolean repeatON = false;
    private NotificationManager mNotificationManager;

    @Override
    public void onDestroy() {
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();

        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //     intent = getIntent();
        utilities = new Utilities();

        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            currentIndex = intent.getIntExtra("index", -1);
            index_dir = intent.getIntExtra("index_dir", -1);
            view_type_video = intent.getIntExtra("view_type_video", 0);
            currentDuration = intent.getIntExtra("cur_pos", 0);
            modernDirectories = new ArrayList<>();
            modernFiles = new ArrayList<>();
            modernDirectories.addAll(DataListsSingeton.getInstance().getDirectoryData());
            Log.e("onStartCommand", "currentDuration: " + currentDuration);
            if (view_type_video == 1) {
                modernFiles.addAll(DataListsSingeton.getInstance().getVideosData());

            } else {
                modernFiles.addAll(modernDirectories.get(index_dir).getInsideData());
            }
             Log.e("oncreate", "currentIndex" + currentIndex);

            playAudioinBackGround();
            Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();

        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
            --currentIndex;
            updatePlayerWithNotification();
            Toast.makeText(this, "Clicked Previous", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Clicked Previous");
        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
            if (mediaPlayer != null)
                if (mediaPlayer.isPlaying()) {
                    views.setImageViewResource(R.id.notify_play_pause,
                            R.drawable.ic_play);
                    mediaPlayer.pause();
                } else {
                    views.setImageViewResource(R.id.notify_play_pause,
                            R.drawable.ic_pause_24dp);
                    mediaPlayer.start();

                }

            if (Build.VERSION.SDK_INT < 16) {
                mNotificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
            } else {
                mNotificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE /* Request Code */, notification);
                // nManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
            }

            Toast.makeText(this, "Clicked Play", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Clicked Play");
        } else if (intent.getAction().equals(Constants.ACTION.OPEN_ACTION)) {
            mHandler.removeCallbacks(mUpdateTimeTask);
           stopForeground(true);

            notificationCancel();
            Intent openIntent = new Intent(this, ModernPlayerLandcsape.class);
            openIntent.setAction(Constants.ACTION.OPEN_ACTION);
            openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            openIntent.putExtra("index", currentIndex);
            openIntent.putExtra("index_dir", index_dir);
            openIntent.putExtra("view_type_video", view_type_video);
            openIntent.putExtra("cur_pos", ((int)currentDuration));
            startActivity(openIntent);
            ModernPlayerLandcsape.currentIndex=-1;
            stopSelf();
        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
            currentIndex++;
            updatePlayerWithNotification();
            Toast.makeText(this, "Clicked Next", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Clicked Next");
        } else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            Toast.makeText(this, "Service Stoped", Toast.LENGTH_SHORT).show();
            mHandler.removeCallbacks(mUpdateTimeTask);
           stopForeground(true);

            notificationCancel();
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    public void updatePlayerWithNotification() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                //  mediaPlayer.release();
                //  mHandler.removeCallbacks(mUpdateTimeTask);
            }
            mediaPlayer.setDataSource(modernFiles.get(currentIndex).getLocation());
            mediaPlayer.prepare();

        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
                views.setImageViewUri(R.id.image,
                        Uri.parse(modernFiles.get(currentIndex).getImg()));
                views.setTextViewText(R.id.title, modernFiles.get(currentIndex).getName());
                if (!mediaPlayer.isPlaying()) {
                    views.setImageViewResource(R.id.notify_play_pause,
                            R.drawable.ic_play);
                } else {
                    views.setImageViewResource(R.id.notify_play_pause,
                            R.drawable.ic_pause_24dp);

                }
                if (Build.VERSION.SDK_INT < 16) {
                    mNotificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
                } else {
                    mNotificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE /* Request Code */, notification);
                    // nManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
                }
                // mHandler.post(mUpdateTimeTask);

            }
        });

    }

    protected void playAudioinBackGround() {

		/*Log.e("mediaPlayer",
                "mediaPlayer is"+mediaPlayer+"  and previous_url_service1=="
						+ previous_url_service4);*/

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(modernFiles.get(currentIndex).getLocation());
            mediaPlayer.prepare();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            //Log.e("DefaulyService", "IllegalArgumentException=="+e);
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            //Log.e("DefaulyService", "SecurityException=="+e);
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            //Log.e("DefaulyService", "IllegalStateException=="+e);
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            //Log.e("DefaulyService", "IOException=="+e);
            e.printStackTrace();
        }
        //mediaPlayer.setLooping(true);
        mediaPlayer.seekTo((int) currentDuration);
        mediaPlayer.start();

        //Log.e("DefaultServ", "mediaPlayer.isPlaying()=="+mediaPlayer.isPlaying());
        if (!mediaPlayer.isPlaying()) {
        } else {

            playNotification();
            mHandler.post(mUpdateTimeTask);
            //    foregroundBackNotification(11);


            Toast.makeText(getApplicationContext(),
                    "You can reopen the Popup From Notifications",
                    Toast.LENGTH_LONG).show();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0);
                    if (repeatON) {
                        mediaPlayer.start();

                    }

                }
            });
        }
        // TODO Auto-generated method stub

    }

    long totalDuration;
    long currentDuration;
    static long savedCurrentDuration;
    private Handler mHandler = new Handler();
    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer.isPlaying()) {
                totalDuration = mediaPlayer.getDuration();
                currentDuration = mediaPlayer.getCurrentPosition();
                //    Log.e("mUpdateTimeTask", "totalDuration : " + totalDuration + "currentDuration : " + currentDuration);

                views.setTextViewText(R.id.duration_notify, utilities.milliSecondsToTimer(currentDuration) + "/" + utilities.milliSecondsToTimer(totalDuration));
                // Log.d("Progress", ""+progress);
                if (Build.VERSION.SDK_INT < 16) {
                    mNotificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
                } else {
                    mNotificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE /* Request Code */, notification);
                    // nManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
                }
            }
            mHandler.postDelayed(this, 1000);

        }
    };
    RemoteViews views;

    private RemoteViews setViews() {
// Using RemoteViews to bind custom layouts into Notification
        views = new RemoteViews(getPackageName(),
                R.layout.custom_push);

// showing default album image
        views.setViewVisibility(R.id.image, View.VISIBLE);


        Intent previousIntent = new Intent(this, NotificationService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, NotificationService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, NotificationService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Intent openIntent = new Intent(this, NotificationService.class);
        openIntent.setAction(Constants.ACTION.OPEN_ACTION);
        PendingIntent popenIntent = PendingIntent.getService(this, 0,
                openIntent, 0);

        Intent closeIntent = new Intent(this, NotificationService.class);
        closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);

        views.setOnClickPendingIntent(R.id.notify_play_pause, pplayIntent);

        views.setOnClickPendingIntent(R.id.notify_next, pnextIntent);

        views.setOnClickPendingIntent(R.id.notify_prev, ppreviousIntent);
        views.setOnClickPendingIntent(R.id.close_notify, pcloseIntent);
        views.setOnClickPendingIntent(R.id.image, popenIntent);

        // views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        views.setImageViewUri(R.id.image,
                Uri.parse(modernFiles.get(currentIndex).getImg()));
        views.setTextViewText(R.id.title, modernFiles.get(currentIndex).getName());
        if (!mediaPlayer.isPlaying()) {
            views.setImageViewResource(R.id.notify_play_pause,
                    R.drawable.ic_play);
        } else {
            views.setImageViewResource(R.id.notify_play_pause,
                    R.drawable.ic_pause_24dp);

        }

//        views.setTextViewText(R.id.title, "Song Title");
//
//        views.setTextViewText(R.id.duration_notify, "Artist Name");

        return views;
    }

    public void notificationCancel() {

        if (mNotificationManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (isNotificatioAlive(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE)) {
                    mNotificationManager.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
                }
            } else {
                mNotificationManager.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    boolean isNotificatioAlive(int id) {
        StatusBarNotification[] notifications;

        notifications = mNotificationManager.getActiveNotifications();
        for (StatusBarNotification notification : notifications) {
            if (notification.getId() == id) {
                return true;
            }
        }
        return false;
    }

    Notification notification;
    public NotificationCompat.Builder myNotification;

    public void playNotification() {
        int notifyID = 1;
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "NOTIFI_CHA_NAME";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        Intent myIntent = new Intent(this, ModernPlayerLandcsape.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1225,
                myIntent, 0);
//        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_push);
//        contentView.setImageViewResource(R.id.image, R.drawable.vlx_icon);
//        contentView.setTextViewText(R.id.title, modernFiles.get(currentIndex).getName());
//        contentView.setTextViewText(R.id.duration_notify, utilities.milliSecondsToTimer(currentDuration)+"/"+utilities.milliSecondsToTimer(totalDuration));
//        contentView.setOnClickPendingIntent(R.id.image, pendingIntent);
        views = setViews();
        myNotification =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setWhen(new Date().getTime())
                        .setSmallIcon(R.drawable.vlx_icon_bg)
                        .setContent(views)
                        .setOnlyAlertOnce(true)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setChannelId(CHANNEL_ID);
        myNotification.setStyle(new NotificationCompat.DecoratedCustomViewStyle());


        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
        }
        notification = myNotification.build();
// Issue the notification.
        //  mNotificationManager.notify(notifyID, notification);
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
    }

}
