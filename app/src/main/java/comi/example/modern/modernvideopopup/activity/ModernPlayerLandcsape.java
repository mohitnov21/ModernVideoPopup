package comi.example.modern.modernvideopopup.activity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import comi.example.modern.modernvideopopup.FilesActivity;
import comi.example.modern.modernvideopopup.Helper;
import comi.example.modern.modernvideopopup.MainActivity;
import comi.example.modern.modernvideopopup.ModernDirectory;
import comi.example.modern.modernvideopopup.ModernFiles;
import comi.example.modern.modernvideopopup.R;
import comi.example.modern.modernvideopopup.Utilities;
import comi.example.modern.modernvideopopup.VodView;
import comi.example.modern.modernvideopopup.service.FloatingViewService;
import comi.example.modern.modernvideopopup.singleton.DataListsSingeton;

/**
 * Created by Instafeed2 on 11/15/2019.
 */

public class ModernPlayerLandcsape
        extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private static final String CHANNEL_ID = "123";
    int index = -1;
    int index_dir;
    private Utilities utilities;
    Intent intent;
    private ArrayList<ModernFiles> modernFiles;
    private ArrayList<ModernDirectory> modernDirectories;
    VodView videoView;
    public static int currentIndex = -1;
    LinearLayout player_control;
    RelativeLayout title_layout;
    TextView initial_pos, end_pos;
    SeekBar seekBar;
    boolean backgroundPlay = false;
    boolean updateSong = false;
    MediaPlayer mediaPlayer;
    TextView video_title;
    ImageView fullscreen_button, closeButtonCollapsed, minimize, openButton, prevButton, nextButton, playButton, repeat_btn, revindBtn, forwardBtn;
    private boolean isMediaPlayerRunning = false;
    public static boolean flag_fullscreen = false;
    public static boolean relaunchedScreen = false;
    private View mVolumeBrightnessLayout;
    private ImageView mOperationBg;
    private ImageView mOperationPercent;
    private GestureDetector bGestureDetector;
    int screenwidth, screenhieght, screenwidthlanscape, screenhieght1;
    int videowidth, videoheight;
    private ViewGroup.LayoutParams mRootParam;
    int view_type_video = 0;
    private boolean playerPaused = false;
    public long playerPausedDurationPosition;
    private float videoProportion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landscape_player_layout);
        intent = getIntent();
        utilities = new Utilities();
        index = intent.getIntExtra("index", -1);
        index_dir = intent.getIntExtra("index_dir", -1);
        view_type_video = intent.getIntExtra("view_type_video", 0);
        modernDirectories = new ArrayList<>();
        modernFiles = new ArrayList<>();
        modernDirectories.addAll(DataListsSingeton.getInstance().getDirectoryData());
        Log.e("FilesActivity outside2", "else count : " + modernDirectories.size());
        if (view_type_video == 1) {
            modernFiles.addAll(DataListsSingeton.getInstance().getVideosData());

        } else {
            modernFiles.addAll(modernDirectories.get(index_dir).getInsideData());
        }
        Log.e("oncreate", "currentIndex" + currentIndex);
        if (currentIndex == -1)
            currentIndex = index;
        mRootParam = (ViewGroup.LayoutParams) findViewById(R.id.root_view).getLayoutParams();

        videoView = (VodView) findViewById(R.id.video_popup);
        video_title = (TextView) findViewById(R.id.video_title);
        player_control = (LinearLayout) findViewById(R.id.player_control);
        title_layout = (RelativeLayout) findViewById(R.id.title_layout);
        initial_pos = (TextView) findViewById(R.id.initial_pos);
        end_pos = (TextView) findViewById(R.id.end_pos);
        seekBar = (SeekBar) findViewById(R.id.vid_seekbar);
        seekBar.setOnSeekBarChangeListener(ModernPlayerLandcsape.this);
        seekBar.setProgress(0);
        seekBar.setMax(100);
        video_title.setText(modernFiles.get(currentIndex).getName());
        //The root element of the collapsed view layout
        //Set the close button
        closeButtonCollapsed = (ImageView) findViewById(R.id.close_btn);
        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SourceLockedOrientationActivity")
            @Override
            public void onClick(View v) {
                // Log.e("fullscreen", "else  inisde");
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                finish();
            }
        });
        fullscreen_button = (ImageView) findViewById(R.id.fullscreen_button);
        minimize = (ImageView) findViewById(R.id.minimize);
        fullscreen_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relaunchedScreen = true;
                savedCurrentDuration = currentDuration;
                if (flag_fullscreen == false) {
                    flag_fullscreen = true;
                    togglevisibility();
                    updateViewInFullscreen();
                    fullscreen_button.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_exit_black_24dp));


                } else {
                    flag_fullscreen = false;
                    fullscreen_button.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_black_24dp));
                    //     fullScreenModeLandscape.stop();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                }
            }
        });

        mVolumeBrightnessLayout =
                findViewById(R.id.operation_volume_brightness);
        mOperationBg = (ImageView) findViewById(R.id.operation_bg);
        mOperationPercent = (ImageView)
                findViewById(R.id.operation_percent);
        mAudioManager = (AudioManager) getSystemService(
                Context.AUDIO_SERVICE);
        assert mAudioManager != null;
        mMaxVolume = mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        bGestureDetector = new GestureDetector(ModernPlayerLandcsape.this,
                new MyGestureListener1());


        minimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close the service and remove the from from the window
                mHandler.removeCallbacks(mUpdateTimeTask);
                if (videoView.isPlaying()) {
                    backgroundPlay = true;

                }
                if (backgroundPlay) {
                    //  videoView.pause();
                    playAudioinBackGround();
                }
                finish();
                //     stopSelf();
            }
        });

        //Set the view while floating view is expanded.
        //Set the play button.
        playButton = (ImageView) findViewById(R.id.play_btn);
        repeat_btn = (ImageView) findViewById(R.id.repeat_btn);
        //    final ImageView pauseButton = (ImageView) findViewById(R.id.pause_btn);
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
                //              Toast.makeText(ModernPlayerLandcsape.this, "Playing the song.", Toast.LENGTH_LONG).show();
            }
        });
        //Set the next button.
        nextButton = (ImageView) findViewById(R.id.next_btn);
        revindBtn = (ImageView) findViewById(R.id.rev_btn);
        forwardBtn = (ImageView) findViewById(R.id.forward_btn);
        revindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int seek = (int) (currentDuration - 10000);
                if (seek > 0) {
                    videoView.seekTo(seek);
                } else {
                    videoView.seekTo(0);

                }
            }
        });
        forwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int seek = (int) (currentDuration + 10000);
                if (seek < videoView.getDuration()) {
                    videoView.seekTo(seek);
                } else {
                    videoView.seekTo(videoView.getDuration());

                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("nextButton", currentIndex + "");
                if (currentIndex < modernFiles.size() - 1) {
                    currentIndex = ++currentIndex;
                    relaunchedScreen = false;
                    playVideobyItsPosition(currentIndex);
                    if (flag_fullscreen) {
                        togglevisibility();

                    }
                } else
                    Toast.makeText(ModernPlayerLandcsape.this, "" +
                            "Last song.", Toast.LENGTH_LONG).show();
            }
        });

        //Set the pause button.
        prevButton = (ImageView) findViewById(R.id.prev_btn);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex >= 0) {
                    currentIndex = --currentIndex;
                    relaunchedScreen = false;
                    playVideobyItsPosition(currentIndex);
                    if (flag_fullscreen) {
                        togglevisibility();

                    }
                }
                Toast.makeText(ModernPlayerLandcsape.this, "Playing previous song.", Toast.LENGTH_LONG).show();
            }
        });
        videoView.setVideoPath(modernFiles.get(currentIndex).getLocation());
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!repeatON) {
                    relaunchedScreen = false;
                    nextButton.performClick();
                } else {
                    relaunchedScreen = false;
                    playVideobyItsPosition(currentIndex);
                }

                //playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
                //videoView.seekTo(0);
            }
        });
        Display mDisplay = getWindowManager().getDefaultDisplay();
        int width = mDisplay.getWidth();
        int height = mDisplay.getHeight();
        screenwidth = width;
        screenhieght1 = width;
        screenhieght = height;
        videowidth = videoView.getWidth();
        videoheight = videoView.getHeight();
        Log.e("onPrepared not ", "videoheight" + videoheight + " videowidth : " + videowidth);

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
        }
        updateProgressBar();
        mScaleGestureDetector = new ScaleGestureDetector(this, new MyScaleGestureListener());
        mGestureDetector = new GestureDetector(this, new TapGestureListener());


        frame = (FrameLayout) findViewById(R.id.root_view);
        frame.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                mScaleGestureDetector.onTouchEvent(event);
                bGestureDetector.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.e("ACTION_DOWN", "" + event.getX());

                    mTouchTime = System.currentTimeMillis();

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    long time = System.currentTimeMillis() - mTouchTime;
                    endGesture();
                }

                return true;
            }
        });
        Log.e("oonCreate", "flag_fullscreen" + flag_fullscreen);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (!relaunchedScreen) {
                    videoheight = mp.getVideoHeight();
                    videowidth = mp.getVideoWidth();
                    Log.e("onPrepared ", "videoheight" + videoheight + " videowidth : " + videowidth);
                    videoProportion = (float) videowidth / (float) videoheight;
               /* if (flag_fullscreen) {
                    videoheight = screenwidth;
                    screenwidth = (int) ((float) screenhieght * videoProportion);

                    if (screenwidth > screenhieght) {
                        screenwidth = screenhieght;
                        screenhieght = (int) ((float) screenwidth / videoProportion);
                    }

                    videoView.setFixedVideoSize(screenwidth, screenhieght);
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                } else {
                    if (videoheight > videowidth) {
                        //Log.d("height==", "ld" + "cds");
                        screenwidth = convertToDp(Portrait_Width);

                        videoheight = (int) ((float) screenwidth / videoProportion);
                        videoView.setFixedVideoSize(screenwidth, videoheight);
                    } else {

                        screenwidth = convertToDp(Landscape_width);
                        // videoheight =
                        // convertToDp(StaaticVariable.Landscape_Height);
                        videoheight = (int) ((float) screenwidth / videoProportion);
                        videoView.setFixedVideoSize(screenwidth, videoheight);
                        // screenwidth=convertToDp(320);

                    }
                }*/
                    if (videoheight > videowidth) {
//                    flag_fullscreen=true;
//                    fullscreen_button.performClick();
                        Log.e("check ", "portrait");
                        if (flag_fullscreen) {
                            fullscreen_button.performClick();

                        }
                    } else {
                        flag_fullscreen = false;
                        fullscreen_button.performClick();
                        // flag_fullscreen=true;
                        fullscreen_button.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_exit_black_24dp));
                        Log.e("check ", "lscape");

                    }

                    //   flag_fullscreen = !flag_fullscreen;
                } else {
                    currentDuration = savedCurrentDuration;
                    videoView.seekTo((int) currentDuration);
                    if (flag_fullscreen) {
                        fullscreen_button.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_exit_black_24dp));

                    } else {
                        fullscreen_button.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_black_24dp));

                    }
                }
                videoView.start();
            }
        });

    }

    public static int Portrait_Width = 200;
    public static int Landscape_Height = 225;
    public static int Landscape_width = 360;


    void updateViewInFullscreen() {
        //  Log.e("onSingleTapConfirmed", "flag_fullscreen run:" + fullScreenModeLandscape.isRunning());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // settings.fullScreenModeLandscape = true;
            if (flag_fullscreen) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            }


        }
    }

    private class TapGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            //  mHandler1.removeCallbacks(mRunnable);
            //  mHandler1.postDelayed(mRunnable, 20000);
            togglevisibility();
            return true;


        }
    }

    private long mTouchTime;
    private boolean barShow = false;
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;
    private int mVolume = -1;
    public static boolean back_ground_flag_on = false;
    private int mMaxVolume;
    FrameLayout frame;
    private float mBrightness = -1f;
    private AudioManager mAudioManager;
    boolean landscape = false;

    public int convertToDp(int input) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        //Log.i("PlayGame","scale="+scale);
        // Convert the dps to pixels, based on density scale
        return (int) (input * scale + 0.5f);
    }

    @SuppressLint("NewApi")
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
            try {
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                //   flag_fullscereen = true;
                landscape = true;
                // int width = this.getResources().getDisplayMetrics().widthPixels;
                //int height = this.getResources().getDisplayMetrics().heightPixels;
                Display display = getWindowManager().getDefaultDisplay();
                int width = display.getWidth();
                int height = display.getHeight();
                screenwidth = width;

                mRootParam.width = convertToDp(width);
                mRootParam.height = convertToDp(height);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
            try {

                mRootParam.width = screenhieght1;
                mRootParam.height = screenhieght;

//                actiobar.setVisibility(View.VISIBLE);
//                actiobar.invalidate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class MyGestureListener1 extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float mOldX = e1.getX(), mOldY = e1.getY();
            int y = (int) e2.getRawY();
            Display disp = ModernPlayerLandcsape.this.getWindowManager().getDefaultDisplay();
            int windowWidth = disp.getWidth();
            int windowHeight = disp.getHeight();
            // Log.e("ACTION_DOWN", " scrolling from : " + e1.getX() + " to: " + e2.getX());

            if (mOldX > windowWidth * 4.0 / 5)
                onVolumeSlide((mOldY - y) / windowHeight);
            else if (mOldX < windowWidth / 5.0)
                onBrightnessSlide((mOldY - y) / windowHeight);
            else {
                if (e1.getX() < e2.getX()) {
                    if (videoView.getDuration() > 60000 && videoView.getDuration() < 180000) {
                        currentDuration += 800;
                        videoView.seekTo((int) currentDuration);
                    } else if (videoView.getDuration() > 60000 && videoView.getDuration() < 300000) {
                        currentDuration += 1000;
                        videoView.seekTo((int) currentDuration);
                    } else if (videoView.getDuration() < 60000 && videoView.getDuration() > 30000) {
                        currentDuration += 200;
                        videoView.seekTo((int) currentDuration);
                    } else if (videoView.getDuration() < 30000) {
                        currentDuration += 100;
                        videoView.seekTo((int) currentDuration);

                    } else if (videoView.getDuration() > 300000) {
                        if (videoView.getDuration() > 600000)
                            currentDuration += 3000;
                        else
                            currentDuration += 1500;
                        videoView.seekTo((int) currentDuration);

                    }


                } else {
                    if (videoView.getDuration() > 60000 && videoView.getDuration() < 180000) {
                        currentDuration -= 800;
                        videoView.seekTo((int) currentDuration);
                    } else if (videoView.getDuration() > 60000 && videoView.getDuration() < 300000) {
                        currentDuration -= 1000;
                        videoView.seekTo((int) currentDuration);
                    } else if (videoView.getDuration() < 60000 && videoView.getDuration() > 30000) {
                        currentDuration -= 600;
                        videoView.seekTo((int) currentDuration);
                    } else if (videoView.getDuration() < 30000) {
                        currentDuration -= 200;
                        videoView.seekTo((int) currentDuration);

                    } else if (videoView.getDuration() > 300000) {
                        if (videoView.getDuration() > 600000)
                            currentDuration -= 3000;
                        else
                            currentDuration -= 1500;

                        videoView.seekTo((int) currentDuration);

                    }
                }
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }


    }

    private void endGesture() {
        mVolume = -1;
        mBrightness = -1f;
        mVolumeBrightnessLayout.setVisibility(View.GONE);
    }

    private void onBrightnessSlide(float percent) {
        if (mBrightness < 0) {
            mBrightness = ModernPlayerLandcsape.this.getWindow().getAttributes().screenBrightness;
            if (mBrightness <= 0.00f)
                mBrightness = 0.50f;
            if (mBrightness < 0.01f)
                mBrightness = 0.01f;


            mOperationBg.setImageResource(R.drawable.video_brightness_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }
        WindowManager.LayoutParams lpa = ModernPlayerLandcsape.this.getWindow()
                .getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f)
            lpa.screenBrightness = 1.0f;
        else if (lpa.screenBrightness < 0.01f)
            lpa.screenBrightness = 0.01f;
        ModernPlayerLandcsape.this.getWindow().setAttributes(lpa);

        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = (int) (findViewById(R.id.operation_full)
                .getLayoutParams().width * lpa.screenBrightness);
        mOperationPercent.setLayoutParams(lp);
    }

    private class MyScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
        private int mW, mH;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // scale our video view
            mW *= detector.getScaleFactor();
            mH *= detector.getScaleFactor();
            if (mW > screenwidth && mH < screenhieght) {
                videoView.setFixedVideoSize(mW, mH); // important
                mRootParam.width = mW;
                mRootParam.height = mH;

                videoView.setLayoutParams(mRootParam);
            } else if (landscape == true) {
                if (mW > screenwidth && mH < screenhieght) {
                    videoView.setFixedVideoSize(mW, mH); // important
                    mRootParam.width = mW;
                    mRootParam.height = mH;

                    videoView.setLayoutParams(mRootParam);
                }
            }

            return true;

        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mW = videoView.getWidth();
            mH = videoView.getHeight();
            //Log.d("onScaleBegin", "scale=" + detector.getScaleFactor() + ", w=" + mW + ", h=" + mH);
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            //Log.d("onScaleEnd", "scale=" + detector.getScaleFactor() + ", w=" + mW + ", h=" + mH);
        }

    }

    private void onVolumeSlide(float percent) {
        if (mVolume == -1) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0)
                mVolume = 0;


            mOperationBg.setImageResource(R.drawable.video_volumn_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }

        int index = (int) (percent * mMaxVolume) + mVolume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = findViewById(R.id.operation_full).getLayoutParams().width
                * index / mMaxVolume;
        mOperationPercent.setLayoutParams(lp);
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

    void updateRepeat() {
        if (!repeatON) {
            // repeatON = false;
            repeat_btn.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_black_24dp));
        } else {
            // repeatON = true;
            repeat_btn.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_one_black_24dp));

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
            playNotification();
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
                    if (repeatON) {
                        mediaPlayer.start();

                    }

                }
            });
        }
        // TODO Auto-generated method stub

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy", "onDestroy" + currentIndex);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (playerPaused) {
            playerPaused = false;
            videoView.seekTo((int) playerPausedDurationPosition);
            Log.e("onResume", "" + playerPausedDurationPosition);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        playerPaused = true;
        Log.e("onPause", "" + currentDuration);
        playerPausedDurationPosition = currentDuration;
        if (videoView.isPlaying()) {
            playButton.performClick();
        }
    }

    long totalDuration;
    long currentDuration;
    static long savedCurrentDuration;
    private Handler mHandler = new Handler();
    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            if (videoView.isPlaying()) {
                totalDuration = videoView.getDuration();
                currentDuration = videoView.getCurrentPosition();
                //    Log.e("mUpdateTimeTask", "totalDuration : " + totalDuration + "currentDuration : " + currentDuration);

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
            }
            mHandler.postDelayed(this, 100);

        }
    };
    NotificationManager notificationManager;
    public NotificationCompat.Builder myNotification;

    public static final String NOTIFICATION_CHANNEL_ID = "2019";
    private NotificationManager mNotificationManager;
    private boolean repeatON = false;

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
        Intent myIntent = new Intent(this, ModernPlayerLandcsape.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getService(this, 5,
                myIntent, 0);

        //  File f = new File(previous_url_service1);
      /*  myNotification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Video Popup Player")
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)

                .setContentText(modernFiles.get(currentIndex).getName()).setTicker("Notification!")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent).setOngoing(true)
                .setAutoCancel(false).setSmallIcon(R.drawable.ic_info_24dp);
*/

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_push);
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        contentView.setTextViewText(R.id.title, "Video Popup Player");
        contentView.setTextViewText(R.id.text, modernFiles.get(currentIndex).getName());
        contentView.setOnClickPendingIntent(R.id.image, pendingIntent);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_info_24dp)
                .setContent(contentView);
        mBuilder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        Notification notification = mBuilder.build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        mNotificationManager.notify(5, notification);
		/*startForeground(notificationId, myNotification);
        return myNotification;*/

        assert mNotificationManager != null;
        mNotificationManager.notify(5 /* Request Code */, notification);
    }
    public void playNotification()
    {
        int notifyID = 1;
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "NOTIFI_CHA_NAME";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        Intent myIntent = new Intent(this, ModernPlayerLandcsape.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1225,
                myIntent, 0);
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_push);
        contentView.setImageViewResource(R.id.image, R.drawable.vlx_icon);
        contentView.setTextViewText(R.id.title, modernFiles.get(currentIndex).getName());
        contentView.setTextViewText(R.id.text, "");
        contentView.setOnClickPendingIntent(R.id.image, pendingIntent);

        Notification notification =
                new NotificationCompat.Builder(this,CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContent(contentView)
                       .setChannelId(CHANNEL_ID).build();



        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
        }

// Issue the notification.
        mNotificationManager.notify(notifyID , notification);
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = "My Notification";
            String description = "My notification description";
            //importance of your notification
            int importance = NotificationManager.IMPORTANCE_DEFAULT;


            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);

            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
