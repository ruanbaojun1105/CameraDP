package com.hwx.camera_doul.cameradp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class PlayActivity extends Activity {
    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private String FilePath;

    private SurfaceView surfaceView2;
    private MediaPlayer mediaPlayer2;
    private String FilePath2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String path=getIntent().getExtras().getString("path");

        initData1(path);
        initData2(CActivity.getVideoTwoPath(path));
    }

    private void initData1(String path) {
//      FilePath="/sdcard/video/sishui.avi";
        FilePath=path;
        surfaceView = (SurfaceView) findViewById(R.id.sv);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置视频流类型
        mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
                Log.i("sno","start mediaplayer1----------------");
            }
        });
        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Toast.makeText(PlayActivity.this,"播放结束",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        mediaPlayer.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                finish();
                return true;
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    mediaPlayer.setDisplay(surfaceView.getHolder());
                    mediaPlayer.setDataSource(FilePath);
                    mediaPlayer.prepareAsync();
                } catch (Exception e) {   ///在这里增加播放失败.
                    mediaPlayer.release();
                    if(mediaPlayer!=null)
                        Log.i("sno","eeeeeeeeeeeeerrormediaPlayer!=null");
                    e.printStackTrace();
                }
            }
        }, 200);
    }

    private void initData2(String path) {
        FilePath2=path;
        surfaceView2 = (SurfaceView) findViewById(R.id.sv2);
        mediaPlayer2 = new MediaPlayer();
        mediaPlayer2.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置视频流类型

        mediaPlayer2.setOnPreparedListener(new OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer2.start();
                Log.i("sno","start mediaPlayer2----------------");
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    mediaPlayer2.setDisplay(surfaceView2.getHolder());
                    mediaPlayer2.setDataSource(FilePath2);
                    mediaPlayer2.prepareAsync();
                } catch (Exception e) {   ///在这里增加播放失败.
                    mediaPlayer2.release();
                    if(mediaPlayer2!=null)
                        Log.i("sno","eeeeeeeeeeeeerrormediaPlayer!=null");
                    e.printStackTrace();
                }
            }
        }, 200);
    }

}
