/*
 *  UVCCamera
 *  library and sample to access to UVC web camera on non-rooted Android device
 *
 * Copyright (c) 2014-2017 saki t_saki@serenegiant.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *  All files in the folder are under this Apache License, Version 2.0.
 *  Files in the libjpeg-turbo, libusb, libuvc, rapidjson folder
 *  may have a different license, see the respective files.
 */

package com.hwx.camera_doul.cameradp;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.serenegiant.common.BaseActivity;
import com.serenegiant.encoder.MediaMuxerWrapper;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.USBMonitor.OnDeviceConnectListener;
import com.serenegiant.usb.USBMonitor.UsbControlBlock;
import com.serenegiant.usbcameracommon.UVCCameraHandler;
import com.serenegiant.widget.CameraViewInterface;
import com.serenegiant.widget.UVCCameraTextureView;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Show side by side view from two camera.
 * You cane record video images from both camera, but secondarily started recording can not record
 * audio because of limitation of Android AudioRecord(only one instance of AudioRecord is available
 * on the device) now.
 */
public final class CActivity extends BaseActivity  {
    private static final boolean DEBUG = false;    // FIXME set false when production
    private static final String TAG = "PlayActivity";

    private static final float[] BANDWIDTH_FACTORS = {0.5f, 0.5f};
    @BindView(R.id.recod_time)
    Chronometer recod_time;
    @BindView(R.id.count_time)
    TextView countTime;
    @BindView(R.id.recod_btn)
    ImageButton recodBtn;
    @BindView(R.id.capture_btn)
    ImageButton captureBtn;
    @BindView(R.id.image_lib)
    ImageButton imageLib;

    // for accessing USB and USB camera
    private USBMonitor mUSBMonitor;
    private UVCCameraHandler mHandlerR;
    private CameraViewInterface mUVCCameraViewR;
    private UVCCameraHandler mHandlerL;
    private CameraViewInterface mUVCCameraViewL;
    private MediaRecorder mediarecorder;// 录制视频的类
    private String mOutputPath;
    private boolean hasOpenedL,hasOpenedR;
    public void recod_timebtnClick() {
        recod_time.setBase(SystemClock.elapsedRealtime());//计时器清零
        int hour = (int) ((SystemClock.elapsedRealtime() - recod_time.getBase()) / 1000 / 60);
        recod_time.setFormat("0" + String.valueOf(hour) + ":%s");
        recod_time.start();
    }
    @OnClick(R.id.image_lib)
    public void onClickimage_lib(View v) {
        startActivity(new Intent(this,ImageLibActivity.class));
    }
    @OnClick(R.id.capture_btn)
    public void onClickcapture_btn(View v) {
        if (mHandlerL!=null){
            if (mHandlerL.isOpened()) {
                if (checkPermissionWriteExternalStorage()) {
                    mHandlerL.captureStill(MediaMuxerWrapper.getCaptureFile(Environment.DIRECTORY_DCIM, ".png").getPath());
                }
            }
        }
        if (mHandlerR!=null){
            if (mHandlerR.isOpened()) {
                if (checkPermissionWriteExternalStorage()) {
                    mHandlerR.captureStill(getVideoTwoPath(MediaMuxerWrapper.getCaptureFile(Environment.DIRECTORY_DCIM, ".png").getPath()));
                }
            }
        }

    }
    @OnClick(R.id.recod_btn)
    public void onClickrecod_btn(ImageButton v) {
        try {
            mOutputPath = MediaMuxerWrapper.getCaptureFile(Environment.DIRECTORY_MOVIES, ".mp4").toString();
        } catch (final NullPointerException e) {
            throw new RuntimeException("This app has no permission of writing external storage");
        }
        if (mHandlerL == null)
            return;
        if (mHandlerL.isOpened()) {
            if (checkPermissionWriteExternalStorage() && checkPermissionAudio()) {
                if (!mHandlerL.isRecording()) {
                    mHandlerL.startRecording(true, mOutputPath);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recodBtn.setColorFilter(0xffff0000);    // turn red
                            recod_time.setVisibility(View.VISIBLE);
                            recod_timebtnClick();
                        }
                    });
                } else {
                    mHandlerL.stopRecording();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recodBtn.setColorFilter(0);    // return to default color
                            recod_time.stop();
                            recod_time.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }
        if (mHandlerR == null)
            return;
        if (mHandlerR.isOpened()) {
            if (checkPermissionWriteExternalStorage() && checkPermissionAudio()) {
                if (!mHandlerR.isRecording()) {
                    recodBtn.setColorFilter(0xffff0000);    // turn red
                    mHandlerR.startRecording(false, getVideoTwoPath(mOutputPath));
                } else {
                    recodBtn.setColorFilter(0);    // return to default color
                    mHandlerR.stopRecording();
                }
            }
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_1);
        ButterKnife.bind(this);
        final String a=getPackageManager().hasSystemFeature("android.hardware.usb.host")?getString(R.string.ddfa):getString(R.string.fvd);
        if (a==getString(R.string.fvd)){
            Toast.makeText(this,a,Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void init(){
        //setPause();
        mUSBMonitor = new USBMonitor(this, mOnDeviceConnectListener);

        //["640x480","352x288","320x240","176x144","160x120","800x600","1600x1200"]
        mUVCCameraViewL = (CameraViewInterface) findViewById(R.id.camera_view_L);
        mUVCCameraViewL.setAspectRatio(1);
        //mHandlerL = UVCCameraHandler.createHandler(this, mUVCCameraViewL,1, 800, 600,1);
        mHandlerL = UVCCameraHandler.createHandler(this, mUVCCameraViewL, 960, 720, BANDWIDTH_FACTORS[0]);

        mUVCCameraViewR = (CameraViewInterface) findViewById(R.id.camera_view_R);
        mUVCCameraViewR.setAspectRatio(1);
        //((UVCCameraTextureView) mUVCCameraViewR).setOnClickListener(mOnClickListener);
        //mHandlerR = UVCCameraHandler.createHandler(this, mUVCCameraViewR,1, 960, 720 ,1);
        mHandlerR = UVCCameraHandler.createHandler(this, mUVCCameraViewR, 960, 720, BANDWIDTH_FACTORS[0]);

        recodBtn.setVisibility(View.INVISIBLE);
        recod_time.setVisibility(View.INVISIBLE);

        mUSBMonitor.register();
        if (mUVCCameraViewR != null)
            mUVCCameraViewR.onResume();
        if (mUVCCameraViewL != null)
            mUVCCameraViewL.onResume();
    }

    private void starPView(){
        if (mUSBMonitor==null)
            return;
        final List<UsbDevice> list = mUSBMonitor.getDeviceList();
        if (list.size() > 0) {
            if (!hasOpenedL)
                mUSBMonitor.requestPermission(list.get(0));
            if (list.size() == 2&&!hasOpenedR)
                mUSBMonitor.requestPermission(list.get(1));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onPause() {
        if (DEBUG) Log.v(TAG, "onPause:");
        setPause();
        super.onPause();
    }

    private synchronized void setPause(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hasOpenedR=false;
                hasOpenedL=false;
                setCameraButton();
                if (mHandlerR != null) {
                    mHandlerR.close();
                    mHandlerR = null;
                }
                if (mHandlerL != null) {
                    mHandlerL.close();
                    mHandlerL = null;
                }
                if (mUVCCameraViewR != null) {
                    mUVCCameraViewR.onPause();
                    mUVCCameraViewR = null;
                }
                if (mUVCCameraViewL != null) {
                    mUVCCameraViewL.onPause();
                    mUVCCameraViewL = null;
                }
                if (mUSBMonitor!=null) {
                    mUSBMonitor.unregister();
                    mUSBMonitor=null;
                }
                if (texture1!=null)
                    texture1.release();
                if (texture2!=null)
                    texture2.release();
            }
        }, 0);
    }


    @Override
    protected void onDestroy() {
        if (mHandlerR != null) {
            mHandlerR = null;
        }
        if (mHandlerL != null) {
            mHandlerL = null;
        }
        if (mUSBMonitor != null) {
            mUSBMonitor.unregister();
            mUSBMonitor.destroy();
            mUSBMonitor = null;
        }
        mUVCCameraViewL = null;
        mUVCCameraViewR = null;
        super.onDestroy();
    }

    SurfaceTexture texture1;
    SurfaceTexture texture2;
    public static String getVideoTwoPath(String pathOne) {
        if (TextUtils.isEmpty(pathOne))
            return null;
        StringBuilder sb = new StringBuilder(pathOne);//构造一个StringBuilder对象
        return sb.insert(pathOne.length() - 4, "_1").toString();
    }
    public void ClearDraw(Surface surface){

        Canvas canvas = null;
        try{
            canvas = surface.lockCanvas(null);
            canvas.drawColor(Color.WHITE);
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC);

        }catch(Exception e){


        }finally{

            if(canvas != null){

                surface.unlockCanvasAndPost(canvas);

            }
        }
    }

    private final OnDeviceConnectListener mOnDeviceConnectListener = new OnDeviceConnectListener() {
        @Override
        public void onAttach(final UsbDevice device) {
            if (DEBUG) Log.v(TAG, "onAttach:" + device);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    countTime.setText(R.string.dvasd);
                    mUSBMonitor.requestPermission(device);
                }
            });
        }

        @Override
        public void onConnect(final UsbDevice device, final UsbControlBlock ctrlBlock, final boolean createNew) {
            if (DEBUG) Log.v(TAG, "onConnect:" + device);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    countTime.setText(R.string.vdaf);
                    recodBtn.setVisibility(View.VISIBLE);
                }
            });
            if (mUVCCameraViewL==null)
                return;
            if (!mUVCCameraViewL.hasSurface())
                return;
            if (mUVCCameraViewR==null)
                return;
            if (!mUVCCameraViewR.hasSurface())
                return;

            if (!hasOpenedL&&mHandlerL!=null&&!mHandlerL.isOpened()) {
                texture1=mUVCCameraViewL.getSurfaceTexture();
                mHandlerL.open(ctrlBlock);
                mHandlerL.startPreview(texture1);
                hasOpenedL = true;
            } else if (!hasOpenedR&&mHandlerR!=null&&!mHandlerR.isOpened()) {
                texture2=mUVCCameraViewR.getSurfaceTexture();
                mHandlerR.open(ctrlBlock);
                mHandlerR.startPreview(texture2);
                hasOpenedR = true;
            }
            //starPView();
        }

        @Override
        public void onDisconnect(final UsbDevice device, final UsbControlBlock ctrlBlock) {
            //Map<Integer,Map<UsbDevice,UsbControlBlock>>
            if (DEBUG) Log.v(TAG, "onDisconnect:" + device);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    countTime.setText(R.string.rtra);
                }
            });
            setPause();
        }

        @Override
        public void onDettach(final UsbDevice device) {
            if (DEBUG) Log.v(TAG, "onDettach:" + device);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    countTime.setText("USB_DEVICE_DETACHED");
                }
            });
            setPause();
        }

        @Override
        public void onCancel(final UsbDevice device) {
            if (DEBUG) Log.v(TAG, "onCancel:");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    countTime.setText("onCancel");
                }
            });
            setPause();
        }

    };
    private void requestPView(){
        if (mUSBMonitor==null)
            return;
        final List<UsbDevice> list = mUSBMonitor.getDeviceList();
        if (list.size() > 0) {
            for (UsbDevice device:list){
                if (mUSBMonitor.hasPermission(device)){
                    mUSBMonitor.requestPermission(device);
                }
            }
        }
    }

    private boolean checkPViewOK(){
        boolean isOk=false;
        if (mUSBMonitor==null)
            return isOk;
        final List<UsbDevice> list = mUSBMonitor.getDeviceList();
        if (list.size() > 0) {
            for (UsbDevice device:list){
                if (!mUSBMonitor.hasPermission(device)){
                    break;
                }
                isOk=true;
            }
        }
        return isOk;
    }

    private void setCameraButton() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if ((mHandlerL != null) && !mHandlerL.isOpened() && (recodBtn != null)) {
                    recodBtn.setVisibility(View.INVISIBLE);
                }
                if ((mHandlerR != null) && !mHandlerR.isOpened() && (recodBtn != null)) {
                    recodBtn.setVisibility(View.INVISIBLE);
                }
            }
        }, 0);
    }
}
