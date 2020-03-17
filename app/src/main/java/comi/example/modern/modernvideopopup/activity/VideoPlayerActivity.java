package comi.example.modern.modernvideopopup.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import comi.example.modern.modernvideopopup.R;

public class VideoPlayerActivity extends Activity {
    public static String VIDEO_PATH = "VideoPath";

    private VideoView mVideoView;
    private MediaController mVideoController;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.video_player_activity);

        init();

        if (icicle != null) {
            String path = icicle.getString("path");

            playVideo(path);
        }
    }

    public void onPause() {
        super.onPause();

        mVideoView.stopPlayback();
    }

    private void init() {
        mVideoView = (VideoView) findViewById(R.id.video_view);

        mVideoController = new MediaController(this, false);

        mVideoView.setMediaController(mVideoController);

        mVideoView.requestFocus();
        mVideoView.setZOrderOnTop(true);
    }

    private void playVideo(String path) {
        Uri uri = Uri.parse(path);
        mVideoView.setVideoURI(uri);

        mVideoView.start();
    }
}
