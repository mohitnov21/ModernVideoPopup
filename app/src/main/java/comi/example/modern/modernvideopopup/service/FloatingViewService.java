package comi.example.modern.modernvideopopup.service;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import comi.example.modern.modernvideopopup.FilesActivity;
import comi.example.modern.modernvideopopup.Helper;
import comi.example.modern.modernvideopopup.MainActivity;
import comi.example.modern.modernvideopopup.ModernDirectory;
import comi.example.modern.modernvideopopup.ModernFiles;
import comi.example.modern.modernvideopopup.R;
import comi.example.modern.modernvideopopup.ScreenOrientationSwitcher;
import comi.example.modern.modernvideopopup.Utilities;
import comi.example.modern.modernvideopopup.VodView;
import comi.example.modern.modernvideopopup.singleton.DataListsSingeton;

public class FloatingViewService extends Service implements SeekBar.OnSeekBarChangeListener {
    int index;
    int index_dir;
    private WindowManager mWindowManager;
    private View mFloatingView;
    VodView videoView;
    int currentIndex = -1;
    TextView video_title;
    public static int Portrait_Height = 310;
    public static int Portrait_Width = 200;
    public static int Landscape_Height = 225;
    public static int Landscape_width = 360;
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;
    private Utilities utilities;

    public FloatingViewService() {
    }

    private boolean isMediaPlayerRunning = false;
    private ArrayList<ModernFiles> modernFiles;
    private ArrayList<ModernDirectory> modernDirectories;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    float videoProportion;
    boolean flag_fullscreen = false;
    public WindowManager.LayoutParams params;
    public int videoHeight, videoWidth;
    LinearLayout player_control;
    RelativeLayout title_layout;
    TextView initial_pos, end_pos;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;

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
        isMediaPlayerRunning = true;
        //Log.e("DefaultServ", "mediaPlayer.isPlaying()=="+mediaPlayer.isPlaying());
        if (!mediaPlayer.isPlaying()) {
//            video1080error = true;
//            foregroundNotification(1);
//            //Log.e("DefaultServ", "minimize backplay but videoHeight greater"+backgroundPlay );
//            Toast.makeText(getApplicationContext(), "Audio Format Not Supported in background for this video.", Toast.LENGTH_LONG).show();
//            //mHomeWatcher.stopWatch();
//            ConServ = null;
//            GalleryActivityNew.counter = GalleryActivityNew.counter - 1;
//            GalleryActivityNew.servic1 = true;
//            GalleryActivityNew.flag_check_popup2 = 2;
//            previous_url_service1 = null; //Log.e("counterrr",""+GalleryActivityNew.counter);
            stopSelf();
        } else {
            try {
               /* if (flag_volume == true) {
                    mediaPlayer.setVolume(0, 0);
                }*/
                if (!videoView.isPlaying()) {
                    mediaPlayer.pause();
                }
            } catch (Exception e) {
                Log.e("exception", "line 2227" + e.toString());

            }
            foregroundBackNotification(11);


            Toast.makeText(getApplicationContext(),
                    "You can reopen the Popup From Notifications",
                    Toast.LENGTH_LONG).show();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0);
                    isMediaPlayerRunning = false;
                    if(repeatON)
                    {
                        mediaPlayer.start();

                    }
/*                    if (repeatCounter == 2) {
                        // shuffle is on - play a random song

                        if (currentSongIndex < (GridViewAdapterVideoNew.mGalleryModelList
                                .size() - 1)) {

                            Log.e("GridViewAds", "flag==" + GridViewAdapterVideoNew.mGalleryModelList.get(currentSongIndex + 1).getFlag() + "and position==" + currentSongIndex + 1);

                            if (!GridViewAdapterVideoNew.mGalleryModelList.get(currentSongIndex + 1).getFlag()) {
                                playAudiobyItsPosition(currentSongIndex + 1);
                            } else {
                                if (currentSongIndex < (GridViewAdapterVideoNew.mGalleryModelList
                                        .size() - 2)) {
                                    playAudiobyItsPosition(currentSongIndex + 2);
                                } else {
                                    playAudiobyItsPosition(0);
                                }
                            }

                        } else {
                            playAudiobyItsPosition(0);
                        }
                    } else if (repeatCounter == 1) {
                        playAudiobyItsPosition(currentSongIndex);
                    } else {
                        //play.setBackgroundResource(R.drawable.play_1);
                    }*/

                }
            });
        }
        // TODO Auto-generated method stub

    }

    NotificationManager notificationManager;
    public NotificationCompat.Builder myNotification;

    public static final String NOTIFICATION_CHANNEL_ID = "2019";
    private NotificationManager mNotificationManager;
    private boolean repeatON=false;
    protected void foregroundBackNotification(int notificationId) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            //     notificationChannel.enableVibration(true);
            //   notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            // myNotification.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        Intent myIntent = new Intent(this, FloatingViewService.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getService(this, 5,
                myIntent, 0);

        //  File f = new File(previous_url_service1);
        myNotification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Video Popup Player")

                .setContentText(modernFiles.get(currentIndex).getName()).setTicker("Notification!")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent).setOngoing(true)
                .setAutoCancel(false).setSmallIcon(R.drawable.ic_info_24dp);

		/*startForeground(notificationId, myNotification);
        return myNotification;*/

        assert mNotificationManager != null;
        mNotificationManager.notify(5 /* Request Code */, myNotification.build());
    }

    boolean backgroundPlay = false;
    boolean updateSong = false;
    ScreenOrientationSwitcher fullScreenModeLandscape;
    ImageView fullscreen_button, closeButtonCollapsed, minimize, openButton, prevButton, nextButton, playButton,repeat_btn;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("onStartCommand outside2", "count : ");
        utilities = new Utilities();
        if (backgroundPlay) {
            mNotificationManager.cancel(5);

            if (mediaPlayer != null) {
                currentDuration = mediaPlayer.getCurrentPosition() + 100;
                if (mediaPlayer.isPlaying()) {
                    isMediaPlayerRunning = true;
                } else {
                    isMediaPlayerRunning = false;
                }
                //Log.e("DefaultServ", "onStart pos5== " + pos);
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            updateSong = intent.getBooleanExtra("update_service", false);
            if (updateSong) {
                repeatON = false;
                currentDuration = 0;
                index = intent.getIntExtra("index", -1);
                index_dir = intent.getIntExtra("index_dir", -1);
                updateSong = false;
                if (mFloatingView.getParent() != null) {
                    mWindowManager.removeView(mFloatingView);
                }
                modernDirectories = new ArrayList<>();
                modernFiles = new ArrayList<>();
                modernDirectories.addAll(DataListsSingeton.getInstance().getDirectoryData());
                Log.e("FilesActivity outside2", "if count : " + modernDirectories.size());
                modernFiles.addAll(modernDirectories.get(index_dir).getInsideData());
                currentIndex = index;
                // currentDuration=0;
            }
            Log.e("onStartCommand outside2", " if index_dir : " + repeatON);

        } else {
            if (mWindowManager != null) {
                mWindowManager.removeView(mFloatingView);
            }
            index = intent.getIntExtra("index", -1);
            index_dir = intent.getIntExtra("index_dir", -1);
            modernDirectories = new ArrayList<>();
            modernFiles = new ArrayList<>();
            modernDirectories.addAll(DataListsSingeton.getInstance().getDirectoryData());
            Log.e("FilesActivity outside2", "else count : " + modernDirectories.size());
            modernFiles.addAll(modernDirectories.get(index_dir).getInsideData());
            currentIndex = index;

        }

        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
        //  Log.e("onCreate outside2", "count : ");
        fullScreenModeLandscape = new ScreenOrientationSwitcher(FloatingViewService.this);
        fullscreen_button = (ImageView) mFloatingView.findViewById(R.id.fullscreen_button);
        //Add the view to the window.
        if (flag_fullscreen) {
            fullScreenModeLandscape.start();
            fullscreen_button.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_exit_black_24dp));

        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);
        }
        //Specify the view position
        params.gravity = Gravity.CENTER | Gravity.CENTER;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mScaleGestureDetector = new ScaleGestureDetector(this,
                new MyScaleGestureListener());
        mGestureDetector = new GestureDetector(this,
                new MySimpleOnGestureListener());
        mWindowManager.addView(mFloatingView, params);
        videoView = (VodView) mFloatingView.findViewById(R.id.video_popup);
        video_title = (TextView) mFloatingView.findViewById(R.id.video_title);
        player_control = (LinearLayout) mFloatingView.findViewById(R.id.player_control);
        title_layout = (RelativeLayout) mFloatingView.findViewById(R.id.title_layout);
        initial_pos = (TextView) mFloatingView.findViewById(R.id.initial_pos);
        end_pos = (TextView) mFloatingView.findViewById(R.id.end_pos);
        seekBar = (SeekBar) mFloatingView.findViewById(R.id.vid_seekbar);
        seekBar.setOnSeekBarChangeListener(FloatingViewService.this);
        seekBar.setProgress(0);
        seekBar.setMax(100);
        video_title.setText(modernFiles.get(currentIndex).getName());
        //The root element of the collapsed view layout
        //Set the close button
        closeButtonCollapsed = (ImageView) mFloatingView.findViewById(R.id.close_btn);
        minimize = (ImageView) mFloatingView.findViewById(R.id.minimize);
        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close the service and remove the from from the window
                mHandler.removeCallbacks(mUpdateTimeTask);
                if (flag_fullscreen == true) {
                    flag_fullscreen = false;
                    fullScreenModeLandscape.stop();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // settings.fullScreenModeLandscape = true;
                    if (DataListsSingeton.getInstance().getCurrentActivty() == 1) {
                        //  Log.e("fullscreen", "GalleryActivityNew.navigation==null");
                        MainActivity.c.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                    }
                    if (DataListsSingeton.getInstance().getCurrentActivty() == 2) {
                        // Log.e("fullscreen", "else  inisde");
                        FilesActivity.filesActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                    }
                }
                stopSelf();
            }
        });
        minimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close the service and remove the from from the window
                mHandler.removeCallbacks(mUpdateTimeTask);
                if (videoView.isPlaying()) {
                    backgroundPlay = true;

                }
                if (backgroundPlay) {
                    mWindowManager.removeView(mFloatingView);
                    //  videoView.pause();
                    playAudioinBackGround();
                }
                //     stopSelf();
            }
        });

        //Set the view while floating view is expanded.
        //Set the play button.
        playButton = (ImageView) mFloatingView.findViewById(R.id.play_btn);
        repeat_btn = (ImageView) mFloatingView.findViewById(R.id.repeat_btn);
        //    final ImageView pauseButton = (ImageView) mFloatingView.findViewById(R.id.pause_btn);
        updateRepeat();
        repeat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatON = !repeatON;
                updateRepeat();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView != null) {
                    if (videoView.isPlaying()) {
                        playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
                        //   playButton.setVisibility(View.GONE);
                        //  pauseButton.setVisibility(View.VISIBLE);
                        videoView.pause();
                    } else {
                        playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_24dp));
                        videoView.start();
                        updateProgressBar();

                    }
                }
                //              Toast.makeText(FloatingViewService.this, "Playing the song.", Toast.LENGTH_LONG).show();
            }
        });
    /*    pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (videoView != null) {
                    playButton.setVisibility(View.VISIBLE);
                    pauseButton.setVisibility(View.GONE);
                    videoView.start();
                    updateProgressBar();
                }
                Toast.makeText(FloatingViewService.this, "pause the song.", Toast.LENGTH_LONG).show();
            }
        });
*/
        //Set the next button.
        nextButton = (ImageView) mFloatingView.findViewById(R.id.next_btn);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex < modernFiles.size() - 1) {
                    currentIndex = ++currentIndex;
                    playVideobyItsPosition(currentIndex);
                    if (flag_fullscreen) {
                        togglevisibility();
                        // Helper.OpenApp(FloatingViewService.this);
                        updateViewInFullscreen();
                    }
                } else
                    Toast.makeText(FloatingViewService.this, "" +
                            "Last song.", Toast.LENGTH_LONG).show();
            }
        });

        //Set the pause button.
        prevButton = (ImageView) mFloatingView.findViewById(R.id.prev_btn);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex >= 0) {
                    currentIndex = --currentIndex;
                    playVideobyItsPosition(currentIndex);
                    if (flag_fullscreen) {
                        togglevisibility();
                        //  Helper.OpenApp(FloatingViewService.this);
                        updateViewInFullscreen();
                    }
                }
                Toast.makeText(FloatingViewService.this, "Playing previous song.", Toast.LENGTH_LONG).show();
            }
        });

        //Set the close button
        fullscreen_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (flag_fullscreen == false) {
                    flag_fullscreen = true;
                    togglevisibility();
                    fullScreenModeLandscape.stop();
                    Helper.OpenApp(FloatingViewService.this);

                    Toast.makeText(FloatingViewService.this, getString(R.string.landscape_msg), Toast.LENGTH_LONG).show();
                    updateViewInFullscreen();
                    fullscreen_button.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_exit_black_24dp));


                } else {
                    flag_fullscreen = false;
                    fullscreen_button.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_black_24dp));
                    fullScreenModeLandscape.stop();
                    if (DataListsSingeton.getInstance().getCurrentActivty() == 1) {
                        //  Log.e("fullscreen", "GalleryActivityNew.navigation==null");
                        MainActivity.c.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                    if (DataListsSingeton.getInstance().getCurrentActivty() == 2) {
                        // Log.e("fullscreen", "else  inisde");
                        FilesActivity.filesActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                    if (videoHeight > videoWidth) {
                        //Log.d("height==", "ld" + "cds");
                        params.width = convertToDp(Portrait_Width);

                        params.height = (int) ((float) params.width / videoProportion);
                        videoView.setFixedVideoSize(params.width, params.height);
                        mWindowManager.updateViewLayout(mFloatingView, params);
                    } else {

                        params.width = convertToDp(Landscape_width);
                        // params.height =
                        // convertToDp(StaaticVariable.Landscape_Height);
                        params.height = (int) ((float) params.width / videoProportion);
                        videoView.setFixedVideoSize(params.width, params.height);
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        // params.width=convertToDp(320);

                    }
                }
            }
        });

        //Open the application on thi button click
        openButton = (ImageView) mFloatingView.findViewById(R.id.open_button);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open the application  click.
                Intent intent = new Intent(FloatingViewService.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                //close the service and remove view from the view hierarchy
                stopSelf();
            }
        });

        //Drag and move floating view using user's touch action.
        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            WindowManager.LayoutParams updatedParameters = params;

            double x;
            double y;
            double pressedX;
            double pressedY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScaleGestureDetector.onTouchEvent(event);
                mGestureDetector.onTouchEvent(event);
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        x = updatedParameters.x;
                        y = updatedParameters.y;

                        pressedX = event.getRawX();
                        pressedY = event.getRawY();

                        break;

                    case MotionEvent.ACTION_MOVE:
                        updatedParameters.x = (int) (x + (event.getRawX() - pressedX));
                        updatedParameters.y = (int) (y + (event.getRawY() - pressedY));

					/*Log.e("Moving", "updatedParameters.y=="
                            + updatedParameters.y);
					Log.e("Moving", "updatedParameters.x=="
							+ updatedParameters.x);*/

                        if (flag_fullscreen == true) {
                            if (updatedParameters.y > screenwidth / 2) {
                                // Log.e("Moving","lllooowww");
                                updatedParameters.y = screenwidth / 2;
                            }
                            if (updatedParameters.y < -screenwidth / 2) {
                                // Log.e("Moving","lllooowww");
                                updatedParameters.y = -screenwidth / 2;
                            }
                            // if(updatedParameters.x>
                            // (screenwidth/2)+video.getWidth())
                            if (updatedParameters.x > (screenheight / 2)) {
                                // Log.e("Moving","lllooowww");
                                // updatedParameters.x=(screenwidth/2)+video.getWidth();
                                updatedParameters.x = (screenheight / 2);
                            }
                            // if(updatedParameters.x< -(video.getWidth()/2))
                            if (updatedParameters.x < -(screenheight / 2)) {
                                // Log.e("Moving","lllooowww");
                                // updatedParameters.x=-(video.getWidth()/2);
                                updatedParameters.x = -(screenheight / 2);
                            }
                        } else {
                            if (updatedParameters.y > screenheight / 2) {
                                // Log.e("Moving","lllooowww");
                                updatedParameters.y = screenheight / 2;
                            }
                            if (updatedParameters.y < -screenheight / 2) {
                                // Log.e("Moving","lllooowww");
                                updatedParameters.y = -screenheight / 2;
                            }
                            // if(updatedParameters.x>
                            // (screenwidth/2)+video.getWidth())
                            if (updatedParameters.x > (screenwidth / 2)) {
                                // Log.e("Moving","lllooowww");
                                // updatedParameters.x=(screenwidth/2)+video.getWidth();
                                updatedParameters.x = (screenwidth / 2);
                            }
                            // if(updatedParameters.x< -(video.getWidth()/2))
                            if (updatedParameters.x < -(screenwidth / 2)) {
                                // Log.e("Moving","lllooowww");
                                // updatedParameters.x=-(video.getWidth()/2);
                                updatedParameters.x = -(screenwidth / 2);
                            }
                        }

                        mWindowManager.updateViewLayout(mFloatingView, updatedParameters);

                    default:
                        break;
                }
                return false;
            }
        });
        videoView.setVideoPath(modernFiles.get(currentIndex).getLocation());
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(!repeatON) {
                    nextButton.performClick();
                }else
                {
                    playVideobyItsPosition(currentIndex);
                }

                //playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
                //videoView.seekTo(0);
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {


            @Override
            public void onPrepared(MediaPlayer mp) {
                // TODO Auto-generated method stub

                videoHeight = mp.getVideoHeight();
                videoWidth = mp.getVideoWidth();

                videoProportion = (float) videoWidth / (float) videoHeight;

                //Log.e("Defaultservice", "videoProportion==" + videoProportion);
                if (flag_fullscreen) {
                    params.x = 0;
                    params.y = 0;
                    params.gravity = Gravity.CENTER | Gravity.CENTER;
                    params.height = screenwidth;
                    params.width = (int) ((float) params.height * videoProportion);

                    if (params.width > screenheight) {
                        params.width = screenheight;
                        params.height = (int) ((float) params.width / videoProportion);
                    }

                    videoView.setFixedVideoSize(params.width, params.height);
                    // params.width
                    // =convertToDp(StaaticVariable.Landscape_width);
                    // fullscreen.setBackgroundResource(R.drawable.screen_zoomout2);
                    mWindowManager.updateViewLayout(mFloatingView, params);
                } else {
                    if (videoHeight > videoWidth) {
                        //Log.d("height==", "ld" + "cds");
                        params.width = convertToDp(Portrait_Width);

                        params.height = (int) ((float) params.width / videoProportion);
                        videoView.setFixedVideoSize(params.width, params.height);
                        mWindowManager.updateViewLayout(mFloatingView, params);
                    } else {

                        params.width = convertToDp(Landscape_width);
                        // params.height =
                        // convertToDp(StaaticVariable.Landscape_Height);
                        params.height = (int) ((float) params.width / videoProportion);
                        videoView.setFixedVideoSize(params.width, params.height);
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        // params.width=convertToDp(320);

                    }
                }

                //  sound.setBackgroundResource(R.drawable.volume_icon);
                //   mp.setVolume(100, 100);
                //  bottm_layout.setVisibility(View.VISIBLE);
                ///  up_layout.setVisibility(View.VISIBLE);

                // Log.i("onprepare call", "yes");

            }
        });
        if (backgroundPlay) {
            backgroundPlay = false;
            videoView.seekTo((int) currentDuration);
            if (isMediaPlayerRunning) {
                playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_24dp));
                videoView.start();
            } else {
                playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));

            }
        } else {
            videoView.start();
        }
        updateProgressBar();

        return START_NOT_STICKY;
    }
    void updateRepeat()
    {
        if(!repeatON)
        {
           // repeatON = false;
            repeat_btn.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_black_24dp));
        }else
        {
           // repeatON = true;
            repeat_btn.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_one_black_24dp));

        }
    }
    void updateViewInFullscreen() {
        Log.e("onSingleTapConfirmed", "flag_fullscreen run:" + fullScreenModeLandscape.isRunning());
        if (!fullScreenModeLandscape.isRunning()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // settings.fullScreenModeLandscape = true;
                if (flag_fullscreen) {
                    if (DataListsSingeton.getInstance().getCurrentActivty() == 1) {
                        //  Log.e("fullscreen", "GalleryActivityNew.navigation==null");
                        MainActivity.c.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                    if (DataListsSingeton.getInstance().getCurrentActivty() == 2) {
                        // Log.e("fullscreen", "else  inisde");
                        FilesActivity.filesActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                } else {
                    if (DataListsSingeton.getInstance().getCurrentActivty() == 1) {
                        //  Log.e("fullscreen", "GalleryActivityNew.navigation==null");
                        MainActivity.c.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                    if (DataListsSingeton.getInstance().getCurrentActivty() == 2) {
                        // Log.e("fullscreen", "else  inisde");
                        FilesActivity.filesActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }

                }
            }
            fullScreenModeLandscape.start();

            Log.e("onSingleTapConfirmed", "flag_fullscreen :" + flag_fullscreen);
            WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Point size = new Point();
                windowManager.getDefaultDisplay().getSize(size);
                screenheight = size.x;
                screenwidth = size.y;
                Log.e("onSingleTapConfirmed", "ORIENTATION_LANDSCAPE screenheight :" + screenheight + " screenwidth :" + screenwidth);
                //  screenheight = windowManager.getDefaultDisplay().getWidth();
                //  screenwidth = windowManager.getDefaultDisplay().getHeight();
                //Do some stuff
            } else {
                Log.e("onSingleTapConfirmed", "ORIENTATION_portrait :");
                Point size = new Point();
                windowManager.getDefaultDisplay().getSize(size);
                screenwidth = size.x;
                screenheight = size.y;
                // fullScreenModeLandscape.stop();
                //   screenwidth = windowManager.getDefaultDisplay().getWidth();
                //   screenheight = windowManager.getDefaultDisplay().getHeight();
            }


        }
        params.x = 0;
        params.y = 0;
        params.gravity = Gravity.CENTER | Gravity.CENTER;
        Log.e("onSingleTapConfirmed", "screenheight" + screenheight + "screenwidth " + screenwidth + "flag_fullscreen :" + flag_fullscreen);
        params.height = screenheight;
        params.width = screenwidth;


        mWindowManager.updateViewLayout(mFloatingView, params);
        videoView.setFixedVideoSize(params.width, params.height);
    }


    @Override
    public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekbar) {
        mHandler.removeCallbacks(mUpdateTimeTask);

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekbar) {
        // TODO Auto-generated method stub
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = videoView.getDuration();
        int currentPosition = utilities.progressToTimer(seekbar.getProgress(),
                totalDuration);

        // forward or backward to certain seconds
        videoView.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    long totalDuration;
    long currentDuration;
    private Handler mHandler = new Handler();
    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            totalDuration = videoView.getDuration();
            currentDuration = videoView.getCurrentPosition();

            // Displaying Total Duration time
            end_pos.setText("" + utilities.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            initial_pos.setText("" + utilities.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (utilities.getProgressPercentage(currentDuration,
                    totalDuration));
            // Log.d("Progress", ""+progress);
            seekBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    int screenwidth, screenheight;

    private class MySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            // TODO Auto-generated method stub
            togglevisibility();
            Helper.OpenApp(FloatingViewService.this);
            updateViewInFullscreen();
            Log.e("onDoubleTapEvent", "line 2550" + e.toString());
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

        /*    mHandler1.removeCallbacks(mRunnable);
            mHandler1.postDelayed(mRunnable, 20000);
            togglevisibility();
*/
            Log.e("onSingleTapConfirmed", "line 2550" + e.toString());

            togglevisibility();
            return true;

        }
    }

    private void playVideobyItsPosition(int position) {

        try {
            video_title.setText(modernFiles.get(position).getName());
            videoView.setVideoPath(modernFiles.get(position).getLocation());
            seekBar.setProgress(0);
            seekBar.setMax(100);
            // Log.i("index=", ""+currentSongIndex);
            videoView.start();
            updateProgressBar();
            //  play.setBackgroundResource(R.drawable.pause_1);
        } catch (Exception e) {
            Log.e("exception", "line 2550" + e.toString());

        }
    }

    public void togglevisibility() {

        if ((player_control.isShown()) && (title_layout.isShown())) {

            player_control.setVisibility(View.GONE);
            title_layout.setVisibility(View.GONE);

        } else {
            player_control.setVisibility(View.VISIBLE);
            title_layout.setVisibility(View.VISIBLE);
            title_layout.invalidate();
            player_control.invalidate();

        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Inflate the floating view layout we created
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Point size = new Point();
            windowManager.getDefaultDisplay().getSize(size);
            screenheight = size.x;
            screenwidth = size.y;
            //  screenheight = windowManager.getDefaultDisplay().getWidth();
            //  screenwidth = windowManager.getDefaultDisplay().getHeight();
            //Do some stuff
        } else {
            Point size = new Point();
            windowManager.getDefaultDisplay().getSize(size);
            screenwidth = size.x;
            screenheight = size.y;
            //   screenwidth = windowManager.getDefaultDisplay().getWidth();
            //   screenheight = windowManager.getDefaultDisplay().getHeight();
        }

    }

    public int convertToDp(int input) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Log.i("PlayGame","scale="+scale);
        // Convert the dps to pixels, based on density scale
        return (int) (input * scale + 0.5f);
    }

    private class MyScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
        private int mW, mH;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            mW *= detector.getScaleFactor();
            mH *= detector.getScaleFactor();
            //    bottm_layout.setVisibility(View.GONE);
            //    up_layout.setVisibility(View.GONE);

            if (videoHeight > videoWidth) {
                // if (mW > convertToDp(225) && mH < screenheight) {
                if (mW > convertToDp(Portrait_Width)
                        && mH < screenheight) {
                    //Log.i("inside the height", "yes");
                    // video.setFixedVideoSize(mW, mH);

                    params.width = mW;
                    params.height = mH;
                    //Log.e("Defaultservice", "Portrait video videoProportion=="
                    //		+ videoProportion);
                    videoView.setFixedVideoSize(params.width, params.height);
//                    videof.setLayoutParams(new
//                            RelativeLayout.LayoutParams(params.width,
//                            params.height));
//                    videof.requestLayout();
//                    videof.invalidate();
                    mWindowManager.updateViewLayout(mFloatingView, params);
                }
            } else {
                // if (mW > convertToDp(310) && mH < screenheight) {
                if (mW > convertToDp(Landscape_width)
                        && mH < screenheight) {
                    //Log.i("inside the width", "yes");
                    // video.setFixedVideoSize(mW, mH);

                    params.width = mW;
                    params.height = mH;
                    /*Log.e("Defaultservice", "Landscape video videoProportion=="
							+ videoProportion);
					Log.e("Defaultservice", "params.width==" + params.width
							+ " params.height==" + params.height
							+ " videoview height=="
							+ (int) ((float) params.width / videoProportion));*/
                    videoView.setFixedVideoSize(params.width, params.height);
//                    videof.setLayoutParams(new
//                            RelativeLayout.LayoutParams(params.width,
//                            params.height));
//                    videof.requestLayout();
//                    videof.invalidate();
                    mWindowManager.updateViewLayout(mFloatingView, params);
                    // video.setLayoutParams(params);
                }
            }

            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {

//            videof.setBackgroundResource(R.color.popup_background_transparent);
//            bottm_layout.setVisibility(View.GONE);
//            up_layout.setVisibility(View.GONE);
            // myView.setVisibility(View.GONE);

            mW = videoView.getWidth();
            mH = videoView.getHeight();
            // Log.d("onScaleBegin", "scale=" + detector.getScaleFactor() +
            // ", w=" + mW + ", h=" + mH);
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

            // myView.setVisibility(View.GONE);
            // Toast.makeText(getApplicationContext(), "="+mW,
            // Toast.LENGTH_LONG).show();
            // Toast.makeText(getApplicationContext(), "="+mH,
            // Toast.LENGTH_LONG).show();

            // Log.d("onScaleEnd", "scale=" + detector.getScaleFactor() + ", w="
            // + mW + ", h=" + mH);

        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) {
            mWindowManager.removeView(mFloatingView);
            videoView = null;
        }
    }

}