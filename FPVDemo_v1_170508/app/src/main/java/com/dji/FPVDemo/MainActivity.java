package com.dji.FPVDemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import dji.common.VideoDataChannel;
import dji.common.airlink.LightbridgeSecondaryVideoDisplayMode;
import dji.common.camera.SettingsDefinitions;
import dji.common.camera.SystemState;
import dji.common.error.DJIError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.product.Model;
import dji.common.remotecontroller.ConnectToMasterResult;
import dji.common.remotecontroller.Credentials;
import dji.common.remotecontroller.GPSData;
import dji.common.remotecontroller.HardwareState;
import dji.common.remotecontroller.Information;
import dji.common.remotecontroller.RequestGimbalControlResult;
import dji.common.util.CommonCallbacks;
import dji.sdk.airlink.LightbridgeLink;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;
import dji.sdk.remotecontroller.RemoteController;

import static dji.common.remotecontroller.RCMode.SLAVE;

public class MainActivity extends Activity implements SurfaceTextureListener {

	private static final String TAG = MainActivity.class.getName();
	protected VideoFeeder.VideoDataCallback mReceivedVideoDataCallBack = null;
	private BaseProduct product;
	private FlightController flightController;
	//private MediaRecorder recorder;
	private CommonCallbacks.CompletionCallback sendDataCallback;
	private NotificationManager mNotificationManager;
	private VideoDataChannel c;

	// 实时图传编解码器
	protected DJICodecManager mCodecManager = null;

	protected TextureView mVideoSurface = null;
	private Button mCaptureBtn;
	private Button mbtn_3;
	private Button mbtn_4;
	private Button mbtn_5;
	private Button mbtn_6;
	private Button mbtn_7;
	private Button mbtn_8;
	private Button mbtn_9;
	private Button mbtn_10;
	private Button mbtn_11;
	private Button mbtn_12;
	private Button mbtn_13;
	private ToggleButton mRecordBtn;
	private ToggleButton mSwitchModeBtn;
	private TextView recordingTime;

	private double longitude,latitude,altitude;


	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (null != flightController) {
				switch (msg.what) {
					case 1:
						Log.d(TAG, "C1,C2,ShutterButton");
						//给Onboard SDK发送4个字节
						flightController.sendDataToOnboardSDKDevice(new byte[]{0x6F,0x11,0x11,0x0D}, sendDataCallback);
						break;

					case 2:
						Log.d(TAG, "C1,C2,RecordButton");
						//给Onboard SDK发送4个字节
						flightController.sendDataToOnboardSDKDevice(new byte[]{0x6F,0x11,0x11,0x0F}, sendDataCallback);
						break;

					case 3:
						Log.d(TAG, "C1,C2,Shutter,RecordButton");
						//给Onboard SDK发送4个字节
						flightController.sendDataToOnboardSDKDevice(new byte[]{0x6F,0x11,0x11,0x0E}, sendDataCallback);
						break;

					case 4:
						Log.d(TAG, "C1,ShutterButton");
						//给Onboard SDK发送4个字节
						flightController.sendDataToOnboardSDKDevice(new byte[]{0x6F,0x11,0x11,0x09}, sendDataCallback);
						break;
					case 5:
						Log.d(TAG,"C1,RecordButton");
						//给Onboard SDK发送4个字节
						flightController.sendDataToOnboardSDKDevice(new byte[]{0x6F,0x11,0x11,0x0A}, sendDataCallback);
						break;
					case 6:
						Log.d(TAG,"ShutterButton");
						//showToast("拍照成功！");
						//给Onboard SDK发送4个字节
						flightController.sendDataToOnboardSDKDevice(new byte[]{0x6F,0x11,0x11,0x01}, sendDataCallback);
						break;
					case 7:
						Log.d(TAG,"Shutter,RecordButton");
						//给Onboard SDK发送4个字节
						flightController.sendDataToOnboardSDKDevice(new byte[]{0x6F,0x11,0x11,0x03}, sendDataCallback);
						break;
					case 8:
						Log.d(TAG,"RecordButton");
						if (mRecordBtn.isChecked()) {
							mRecordBtn.setChecked(false);
						} else {
							mRecordBtn.setChecked(true);
						}
						//给Onboard SDK发送4个字节
						flightController.sendDataToOnboardSDKDevice(new byte[]{0x6F,0x11,0x11,0x02}, sendDataCallback);
						break;
					case 9:
						Log.d(TAG,"Shutter,C2Button");
						//给Onboard SDK发送4个字节
						flightController.sendDataToOnboardSDKDevice(new byte[]{0x6F,0x11,0x11,0x05}, sendDataCallback);
						break;
					case 10:
						Log.d(TAG,"C2,RecordButton");
						//给Onboard SDK发送4个字节
						flightController.sendDataToOnboardSDKDevice(new byte[]{0x6F,0x11,0x11,0x07}, sendDataCallback);
						break;
					case 11:
						Log.d(TAG,"C2,Shutter,RecordButton");
						//给Onboard SDK发送4个字节
						flightController.sendDataToOnboardSDKDevice(new byte[]{0x6F,0x11,0x11,0x06}, sendDataCallback);
						break;
					default:

						break;
				}
			}
			else{showToast("No flightcontroller!");}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNotificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		initUI();
		sendDataCallback = new CommonCallbacks.CompletionCallback() {
			@Override
			public void onResult(DJIError djiError) {
				//showToast("数据透传" + (djiError == null ? "成功！" : djiError.getDescription()));
			}
		};

		// 回调接口接收H264视频数据，发送给mCodecManager解码
		mReceivedVideoDataCallBack = new VideoFeeder.VideoDataCallback() {

			@Override
			public void onReceive(byte[] videoBuffer, int size) {
				if (mCodecManager != null) {
					mCodecManager.sendDataToDecoder(videoBuffer, size);
				}
			}
		};

		Camera camera = FPVDemoApplication.getCameraInstance();

		if (camera != null) {

			camera.setSystemStateCallback(new SystemState.Callback() {
				@Override
				public void onUpdate(SystemState cameraSystemState) {
					if (null != cameraSystemState) {

						int recordTime = cameraSystemState.getCurrentVideoRecordingTimeInSeconds();
						int minutes = (recordTime % 3600) / 60;
						int seconds = recordTime % 60;

						final String timeString = String.format("%02d:%02d", minutes, seconds);
						final boolean isVideoRecording = cameraSystemState.isRecording();

						MainActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {

								recordingTime.setText(timeString);

                                /*
                                 * 更新录制时间和按钮状态
                                 */
								if (isVideoRecording) {
									recordingTime.setVisibility(View.VISIBLE);
								} else {
									recordingTime.setVisibility(View.INVISIBLE);
								}
							}
						});
					}
				}
			});

		}

	}

	protected void onProductChange() {
		initPreviewer();
	}

	@Override
	public void onResume() {
		Log.e(TAG, "onResume");
		super.onResume();
		onProductChange();

		if (mVideoSurface == null) {
			Log.e(TAG, "mVideoSurface is null");
		}
	}

	@Override
	public void onPause() {
		Log.e(TAG, "onPause");
		uninitPreviewer();
		super.onPause();
	}

	@Override
	public void onStop() {
		Log.e(TAG, "onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.e(TAG, "onDestroy");
		uninitPreviewer();
		super.onDestroy();
	}

	private void initUI() {
		// init mVideoSurface
		mVideoSurface = (TextureView) findViewById(R.id.video_previewer_surface);

		mSwitchModeBtn = (ToggleButton) findViewById(R.id.btn_switch_mode);
		mCaptureBtn = (Button) findViewById(R.id.btn_capture);
		mRecordBtn = (ToggleButton) findViewById(R.id.btn_record);

		recordingTime = (TextView) findViewById(R.id.timer);
		recordingTime.setVisibility(View.INVISIBLE);

		ToggleButton mbtn_PWM1 = (ToggleButton) findViewById(R.id.btn_PWM1);
		ToggleButton mbtn_PWM2 = (ToggleButton) findViewById(R.id.btn_PWM2);
		ToggleButton mbtn_PWM3 = (ToggleButton) findViewById(R.id.btn_PWM3);
		ToggleButton mbtn_PWM4 = (ToggleButton) findViewById(R.id.btn_PWM4);
		mbtn_3=(Button)findViewById(R.id.btn_3);
		mbtn_4=(Button)findViewById(R.id.btn_4);
		mbtn_5=(Button)findViewById(R.id.btn_5);
		mbtn_6=(Button)findViewById(R.id.btn_6);
		mbtn_7=(Button)findViewById(R.id.btn_7);
		mbtn_8=(Button)findViewById(R.id.btn_8);
		mbtn_9=(Button)findViewById(R.id.btn_9);
		mbtn_10=(Button)findViewById(R.id.btn_10);
		mbtn_11=(Button)findViewById(R.id.btn_11);
		mbtn_12=(Button)findViewById(R.id.btn_12);
		mbtn_13=(Button)findViewById(R.id.btn_13);
		Button slavebutton = (Button) findViewById(R.id.slavebutton);
		Button gimbal = (Button) findViewById(R.id.gimbal);
		Button display=(Button)findViewById(R.id.displaymode);
		Button back=(Button)findViewById(R.id.btn_back);

		if (null != mVideoSurface) {
			mVideoSurface.setSurfaceTextureListener(this);
		}

		mSwitchModeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					mRecordBtn.setVisibility(View.VISIBLE);
					mCaptureBtn.setVisibility(View.GONE);
					switchCameraMode(SettingsDefinitions.CameraMode.RECORD_VIDEO);
				} else {
					mCaptureBtn.setVisibility(View.VISIBLE);
					mRecordBtn.setVisibility(View.GONE);
					switchCameraMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO);
				}
			}
		});

		mCaptureBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.this.StoreInApp(mVideoSurface);
				captureAction();
			}
		});

		mRecordBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					startRecord();
					//StartRecordInApp();
					mSwitchModeBtn.setEnabled(false);
				} else {
					stopRecord();
					//StopRecordInApp();
					mSwitchModeBtn.setEnabled(true);
				}
			}
		});

		mbtn_PWM1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					mbtn_3.setVisibility(View.VISIBLE);
					mbtn_4.setVisibility(View.VISIBLE);
					mbtn_5.setVisibility(View.VISIBLE);
				}else{
					mbtn_3.setVisibility(View.GONE);
					mbtn_4.setVisibility(View.GONE);
					mbtn_5.setVisibility(View.GONE);
				}
			}
		});

		mbtn_PWM2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					mbtn_6.setVisibility(View.VISIBLE);
					mbtn_7.setVisibility(View.VISIBLE);
				}else{
					mbtn_6.setVisibility(View.GONE);
					mbtn_7.setVisibility(View.GONE);
				}
			}
		});

		mbtn_PWM3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					mbtn_8.setVisibility(View.VISIBLE);
					mbtn_9.setVisibility(View.VISIBLE);
					mbtn_10.setVisibility(View.VISIBLE);
				}else{
					mbtn_8.setVisibility(View.GONE);
					mbtn_9.setVisibility(View.GONE);
					mbtn_10.setVisibility(View.GONE);
				}
			}
		});

		mbtn_PWM4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					mbtn_11.setVisibility(View.VISIBLE);
					mbtn_12.setVisibility(View.VISIBLE);
					mbtn_13.setVisibility(View.VISIBLE);
				}else{
					mbtn_11.setVisibility(View.GONE);
					mbtn_12.setVisibility(View.GONE);
					mbtn_13.setVisibility(View.GONE);
				}
			}
		});

		mbtn_3.setOnClickListener(new NoDoubleClickListener() {
			@Override
			public void onNoDoubleClick(View v){
				handler.sendEmptyMessage(1);
			}
		});

		mbtn_4.setOnClickListener(new NoDoubleClickListener(){
			@Override
			public void onNoDoubleClick(View v){
				handler.sendEmptyMessage(2);
			}
		});


		mbtn_5.setOnClickListener(new NoDoubleClickListener(){
			@Override
			public void onNoDoubleClick(View v){
				handler.sendEmptyMessage(3);
			}
		});

		mbtn_6.setOnClickListener(new NoDoubleClickListener(){
			@Override
			public void onNoDoubleClick(View v){
				handler.sendEmptyMessage(4);
			}
		});

		mbtn_7.setOnClickListener(new NoDoubleClickListener(){
			@Override
			public void onNoDoubleClick(View v){
				handler.sendEmptyMessage(5);
			}
		});

		mbtn_8.setOnClickListener(new NoDoubleClickListener(){
			@Override
			public void onNoDoubleClick(View v){
				handler.sendEmptyMessage(6);
			}
		});

		mbtn_9.setOnClickListener(new NoDoubleClickListener(){
			@Override
			public void onNoDoubleClick(View v){
				handler.sendEmptyMessage(7);
			}
		});

		mbtn_10.setOnClickListener(new NoDoubleClickListener(){
			@Override
			public void onNoDoubleClick(View v){
				handler.sendEmptyMessage(8);
			}
		});

		mbtn_11.setOnClickListener(new NoDoubleClickListener(){
			@Override
			public void onNoDoubleClick(View v){
				handler.sendEmptyMessage(9);
			}
		});

		mbtn_12.setOnClickListener(new NoDoubleClickListener(){
			@Override
			public void onNoDoubleClick(View v){
				handler.sendEmptyMessage(10);
			}
		});

		mbtn_13.setOnClickListener(new NoDoubleClickListener(){
			@Override
			public void onNoDoubleClick(View v){
				handler.sendEmptyMessage(11);
			}
		});

		slavebutton.setOnClickListener(new NoDoubleClickListener() {
			@Override
			public void onNoDoubleClick(View view) {
				ConnectDialog();
			}
		});

		gimbal.setOnClickListener(new NoDoubleClickListener() {
			@Override
			public void onNoDoubleClick(View view) {
				requestGimbalControl();
			}
		});

		display.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				setDisplayMode();
			}
		});

		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}

	/**
	 * 显示实时视频
	 */
	private void initPreviewer() {

		product = FPVDemoApplication.getProductInstance();

		if (product == null || !product.isConnected()) {
			showToast(getString(R.string.disconnected));
		} else {
			if (null != mVideoSurface) {
				mVideoSurface.setSurfaceTextureListener(this);
			}
			if (!product.getModel().equals(Model.UNKNOWN_AIRCRAFT)) {
				initFlightController();
				initRemoteController();
				if (VideoFeeder.getInstance().getVideoFeeds() != null && VideoFeeder.getInstance().getVideoFeeds()
						.size() > 0) {
					VideoFeeder.getInstance().getVideoFeeds().get(0).setCallback(mReceivedVideoDataCallBack);
				}
			}
		}
	}

	//初始化飞行控制器
	private void initFlightController() {
		flightController = ((Aircraft) product).getFlightController();
		flightController.setStateCallback(new FlightControllerState.Callback() {
			@Override
			public void onUpdate(@NonNull FlightControllerState flightControllerState) {
				longitude=flightControllerState.getAircraftLocation().getLongitude();
				latitude=flightControllerState.getAircraftLocation().getLatitude();
				altitude=flightControllerState.getAircraftLocation().getAltitude();
			}
		});
		if (null != flightController) {
			flightController.setOnboardSDKDeviceDataCallback(new FlightController.OnboardSDKDeviceDataCallback() {
				@Override
				public void onReceive(byte[] bytes) {
					//showToast(new String(bytes));
					if(bytes[0]=='3'&&bytes[1]=='F'){
						//showToast("3F");
						MainActivity.this.StoreInApp(mVideoSurface);
						warning();
						showToast("警报！最高温度超过50摄氏度"+"\n"+"经度:"+String.valueOf(longitude)+",纬度:"+String.valueOf(latitude)+"高度:"+String.valueOf(altitude));
					}
					if(bytes[0]=='4'&&bytes[1]=='F'){
						showToast("Raspberry Pi output PWM wave!");
					}
					if(bytes[0]=='5'&&bytes[1]=='F'){
						writetext("温度降低到50℃以下");
						showToast("最高温度降低到50摄氏度以下"+"\n"+"经度:"+String.valueOf(longitude)+",纬度:"+String.valueOf(latitude)+"高度:"+String.valueOf(altitude));
					}
				}
			});
		}
	}

	/**
	 * 初始化遥控器组件
	 */
	private void initRemoteController() {
		final RemoteController remoteController = ((Aircraft) product).getRemoteController();
		if (null != remoteController) {
			remoteController.setHardwareStateCallback(new HardwareState.HardwareStateCallback() {
				@Override
				public void onUpdate(@NonNull HardwareState hardwareState) {
					//监听遥控器硬件状态的更新，哪几个按键被点击，就发送空消息通知Handler进行相应处理
					if (hardwareState.getShutterButton().isClicked()) {
						handler.sendEmptyMessage(6);
					} else if (hardwareState.getRecordButton().isClicked()) {
						handler.sendEmptyMessage(8);
					} //else if (hardwareState.getC1Button().isClicked()) {
						//handler.sendEmptyMessage(3);
					//}
//					  else if (hardwareState.getC2Button().isClicked()) {
//						ConnectDialog();
//					}
				}
			});
		}
	}

	/**
	 * 重置
	 */
	private void uninitPreviewer() {
		Camera camera = FPVDemoApplication.getCameraInstance();
		if (camera != null) {
			// 重置回调
			VideoFeeder.getInstance().getVideoFeeds().get(0).setCallback(null);
		}
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
		Log.e(TAG, "onSurfaceTextureAvailable");
		if (mCodecManager == null) {
			mCodecManager = new DJICodecManager(this, surface, width, height);
		}
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
		Log.e(TAG, "onSurfaceTextureSizeChanged");
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
		Log.e(TAG, "onSurfaceTextureDestroyed");
		if (mCodecManager != null) {
			mCodecManager.cleanSurface();
			mCodecManager = null;
		}

		return false;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {
	}

	public void showToast(final String msg) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void switchCameraMode(SettingsDefinitions.CameraMode cameraMode) {

		Camera camera = FPVDemoApplication.getCameraInstance();
		if (camera != null) {
			camera.setMode(cameraMode, new CommonCallbacks.CompletionCallback() {
				@Override
				public void onResult(DJIError error) {

					if (error == null) {
						showToast("模式切换成功！");
					} else {
						showToast(error.getDescription());
					}
				}
			});
		}
	}

	/**
	 * 拍照控制
	 */
	private void captureAction() {

		final Camera camera = FPVDemoApplication.getCameraInstance();
		final FlightController flightController = ((Aircraft) (FPVDemoApplication.getProductInstance()))
				.getFlightController();
		if (null != camera) {

			//拍照模式设置为SINGLE模式，连拍功能受限制
			SettingsDefinitions.ShootPhotoMode photoMode = SettingsDefinitions.ShootPhotoMode.SINGLE;
			//保存为JPEG：对于XT相机，两张照片拍照时间之间最快也要间隔1秒；对于其他相机，2s
			//保存为RAW：两张照片之间的拍照时间间隔最少10s
			camera.setPhotoFileFormat(SettingsDefinitions.PhotoFileFormat.JPEG, null);
			camera.setShootPhotoMode(photoMode, new CommonCallbacks.CompletionCallback() {
				@Override
				public void onResult(DJIError djiError) {
					if (null == djiError) {
						//拍照动作，教程里面说要延时2000ms，这里去掉了延时
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								camera.startShootPhoto(new CommonCallbacks.CompletionCallback() {
									@Override
									public void onResult(DJIError djiError) {
										if (djiError == null) {
											showToast("拍照成功！");
										} else {
											showToast(djiError.getDescription());
										}
									}
								});
								//延时2000ms，相机硬件决定单拍模式间隔不得少于2s
								try {
									Thread.sleep(2000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						});
					}
				}
			});

		}
	}

//	/**
//	 * 照片、视频下载
//	 *
//	private void mediaDownload() {
//		final Camera camera = FPVDemoApplication.getCameraInstance();
//		if(null!=camera) {
//			switchCameraMode(SettingsDefinitions.CameraMode.MEDIA_DOWNLOAD);
//			runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					camera.getMediaManager().fetchMediaList(new MediaManager.DownloadListener<List<MediaFile>>() {
//						@Override
//						public void onStart() {
//						}
//
//						@Override
//						public void onRateUpdate(long total, long current, long persize) {
//						}
//
//						@Override
//						public void onProgress(long total, long current) {
//						}
//
//						@Override
//						public void onSuccess(List<MediaFile> M) {
//							mediafile = M.get(M.size() - 1);
//						}
//
//						@Override
//						public void onFailure(DJIError error) {
//							showToast("Fetch media file failed!");
//						}
//					});
//					File destDir =
//							new File(Environment.getExternalStorageDirectory().getPath() + "/DJI_Download/");
//					if(!destDir.exists()){destDir.mkdir();}
//					camera.getMediaManager().fetchMediaData(mediafile, destDir, null, new MediaManager.DownloadListener<String>() {
//						@Override
//						public void onStart() {
//							showToast("开始下载");
//						}
//
//						@Override
//						public void onRateUpdate(long total, long current, long persize) {
//							showToast("总共大小为" + total + "kb,已经下载了" + current + "kb,刚才1s下载了" + persize + "kb");
//						}
//
//						@Override
//						public void onProgress(long total, long current) {
//						}
//
//						@Override
//						public void onSuccess(String data) {
//							showToast("下载成功！");
//						}
//
//						@Override
//						public void onFailure(DJIError error) {
//							showToast("下载失败！");
//						}
//					});
//				}
//			});
//			switchCameraMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO);
//		}
//	}/

	/**
	 * 开始摄像
	 */
	private void startRecord() {

		final Camera camera = FPVDemoApplication.getCameraInstance();
		if (camera != null) {
			camera.startRecordVideo(new CommonCallbacks.CompletionCallback() {
				@Override
				public void onResult(DJIError djiError) {
					if (djiError == null) {
						showToast("开始录像成功！");
					} else {
						showToast(djiError.getDescription());
					}
				}
			});
		}
	}
//
//	private void StartRecordInApp(){
//		SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日  HH:mm:ss");
//		Date curDate =  new Date(System.currentTimeMillis());
//		String str = formatter.format(curDate);
//		File destDir =
//				new File(Environment.getExternalStorageDirectory().getPath() + "/DJI_Download/");
//		if(!destDir.exists()){destDir.mkdir();}
//		File recordfile = new File(Environment.getExternalStorageDirectory().getPath() + "/DJI_Download/"+ str + ".mp4");
//		recorder = new MediaRecorder();
//		recorder.reset();
//		SurfaceTexture surfacetexture=mVideoSurface.getSurfaceTexture();
//		Surface surface=new Surface(surfacetexture);
//		recorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
//		recorder.setPreviewDisplay(surface);
//		recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//		recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//		recorder.setVideoSize(480,640);
//		recorder.setVideoEncodingBitRate(5*1024*1024);
//		//recorder.setVideoFrameRate(30);
//		recorder.setOutputFile(recordfile.getAbsolutePath());
//		try{
//			recorder.prepare();
//		}catch(IOException e){
//			Log.e(TAG,"prepare failed!");
//		}
//		recorder.start();
//		showToast("开始录制！");
//	}
//
//	private void StopRecordInApp(){
//		if(recorder!=null){
//			try {
//				recorder.setOnErrorListener(null);
//				recorder.setOnInfoListener(null);
//				recorder.setPreviewDisplay(null);
//				recorder.stop();
//				Log.e(TAG,"Stop success");
//				showToast("结束录制！");
//			}catch(IllegalStateException e){
//				Log.e(TAG,"IllegalState");
//				showToast("Illegal State");
//			}catch(RuntimeException e){
//				Log.e(TAG,"Runtime");
//				showToast("无视频源！");
//			}
//				recorder.release();
//				recorder = null;
//
//		}
//	}
//


	/**
	 * 停止摄像
	 */
	private void stopRecord() {

		Camera camera = FPVDemoApplication.getCameraInstance();
		if (camera != null) {
			camera.stopRecordVideo(new CommonCallbacks.CompletionCallback() {

				@Override
				public void onResult(DJIError djiError) {
					if (djiError == null) {
						showToast("结束录像成功！");
					} else {
						showToast(djiError.getDescription());
					}
				}
			});

		}

	}

	private void StoreInApp(TextureView vv){
		SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日  HH:mm:ss");
		Date curDate =  new Date(System.currentTimeMillis());
		String str = formatter.format(curDate);
		File destDir =
				new File(Environment.getExternalStorageDirectory().getPath() + "/DJI_Download/");
		if(!destDir.exists()){destDir.mkdir();}
		File imagefile =
				new File(Environment.getExternalStorageDirectory().getPath() + "/DJI_Download/"+ str + ".jpg");
		Bitmap bm=vv.getBitmap();
		try{
			OutputStream fout=new FileOutputStream(imagefile);
			bm.compress(Bitmap.CompressFormat.JPEG, 90, fout);
			fout.flush();
			fout.close();
		}catch(FileNotFoundException e){
			Log.e(TAG,"FileNotFoundException");
			e.printStackTrace();
		}catch(IOException e){
			Log.e(TAG,"IOException");
			e.printStackTrace();
		}
	}

	/**
	 * 状态栏警告
	 */
	private void warning(){
		PendingIntent pi = PendingIntent.getActivity(
				MainActivity.this,
				100,
				new Intent(MainActivity.this, MainActivity.class),
				PendingIntent.FLAG_CANCEL_CURRENT
		);
		Bitmap largeBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
		NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(MainActivity.this);
		mBuilder.setContentTitle("警告：")
				.setContentText("最高温度超过50℃")
				.setTicker("温度警告！")
				.setSmallIcon(R.mipmap.ic_launcher)
				.setLargeIcon(largeBitmap)
				.setWhen(System.currentTimeMillis())
				.setPriority(Notification.PRIORITY_HIGH)
				.setOngoing(false)
				.setAutoCancel(true)
				.setVisibility(Notification.VISIBILITY_PUBLIC)
				.setDefaults(Notification.DEFAULT_VIBRATE)
				.setContentIntent(pi);
		Notification mNotification = mBuilder.build();
		int notifyId = 123;
		mNotificationManager.notify(notifyId, mNotification);
		writetext("温度超过50℃!");
	}

	/**
	 * 温度超过50℃时写入DJI_Download的Warning Log文件中
	 */
	private void writetext(String s){
		File destDir =
				new File(Environment.getExternalStorageDirectory().getPath() + "/DJI_Download/");
		if(!destDir.exists()){destDir.mkdir();}
		SimpleDateFormat formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日  HH:mm:ss");
		Date curDate =  new Date(System.currentTimeMillis());
		String str = formatter.format(curDate);
		String str2=s+str+"\n"+"经度:"+String.valueOf(longitude)+",纬度:"+String.valueOf(latitude)+"高度:"+String.valueOf(altitude)+"\n";
		str=Environment.getExternalStorageDirectory().getPath() + "/DJI_Download/Warning Log.txt";
		File txtfile =
				new File(str);
		try {
			if (!txtfile.exists()){txtfile.createNewFile();}
			RandomAccessFile raf = new RandomAccessFile(txtfile, "rwd");
			raf.seek(txtfile.length());
			raf.write(str2.getBytes());  //将String字符串以字节流的形式写入到输出流中
			raf.close();         //关闭输出流
		}catch (Exception e){
			Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
		}

		String str1=Environment.getExternalStorageDirectory().getPath() + "/DJI_Download/Warning Log.db";
		SQLiteDatabase db= SQLiteDatabase.openOrCreateDatabase(str1,null);
		String TemperatureLog="create table if not exists TemperatureLog(_id integer primary key autoincrement,time text,longitude double,latitude double,height double,temperature text)";
		db.execSQL(TemperatureLog);
		String insertStr = "insert into TemperatureLog(time,longitude,latitude,height,temperature) values(?,?,?,?,?)";
		Object[] value=new Object[]{formatter.format(curDate),longitude,latitude,altitude,s};
		db.execSQL(insertStr,value);
		db.close();
	}


	/**
	 * Dialog for connect to master
	 */
	private void ConnectDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setIcon(R.mipmap.ic_launcher);
		builder.setTitle("请输入主机名称和密码");
		//    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
		View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog, null);
		//    设置我们自己定义的布局文件作为弹出框的Content
		builder.setView(view);

		final EditText username = (EditText)view.findViewById(R.id.username);
		final EditText password = (EditText)view.findViewById(R.id.password);

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				String a = username.getText().toString().trim();
				String b = password.getText().toString().trim();

				SwitchToSlaveRemoteController(a,b);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{

			}
		});
		builder.show();
	}


	/**
	 * Connect to master and display HD gimbal image
	 * Add in 2017.7.17
	 */
	private void SwitchToSlaveRemoteController(final String hostname, final String password) {
		product = FPVDemoApplication.getProductInstance();
		if (product != null) {
			final RemoteController remoteController = ((Aircraft) product).getRemoteController();
			//final LightbridgeLink lightbridge = product.getAirLink().getLightbridgeLink();
			if (remoteController.isMasterSlaveModeSupported()) {
				remoteController.setMode(SLAVE, new CommonCallbacks.CompletionCallback() {
					@Override
					public void onResult(DJIError djiError) {
						if (djiError == null) {
							showToast("模式切换成功！");
							remoteController.connectToMaster(new Credentials(1, hostname, password), new CommonCallbacks.CompletionCallbackWith<ConnectToMasterResult>() {
								@Override
								public void onSuccess(ConnectToMasterResult connectToMasterResult) {
									if (connectToMasterResult.value() == 0) {
										showToast("连接主机成功！");
									} else {
										showToast(connectToMasterResult.toString());
									}
								}

								@Override
								public void onFailure(DJIError djiError) {
									showToast("连接主机失败！");
								}
							});
						} else {
							showToast(djiError.getDescription());
						}
					}
				});
			}
		}
	}

	public void requestGimbalControl(){
		product = FPVDemoApplication.getProductInstance();
		if(product!=null) {
			final RemoteController remoteController = ((Aircraft) product).getRemoteController();
			remoteController.requestGimbalControl(new CommonCallbacks.CompletionCallbackWith<RequestGimbalControlResult>() {
				@Override
				public void onSuccess(RequestGimbalControlResult requestGimbalControlResult) {
					if (requestGimbalControlResult.value() == 0) showToast("获取云台控制成功！");
					if (requestGimbalControlResult.value() == 1) showToast("获取云台控制被拒绝！");
				}

				@Override
				public void onFailure(DJIError djiError) {
					showToast("获取云台控制失败！");
				}
			});
			showToast("light");
		}
	}

	/**
	 * Setup Display Mode
	 * */
	public void setDisplayMode(){
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setIcon(R.mipmap.ic_launcher);
		builder.setTitle("选择视频输源：");
		final String[] choices={"显示FPV视频图像","显示云台视频图像"};
		builder.setSingleChoiceItems(choices,0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int which) {
				if(which==0) c=VideoDataChannel.FPV_CAMERA;
				if(which==1) c=VideoDataChannel.HD_GIMBAL;
			}
		});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				showToast("you choose "+ c);
				product = FPVDemoApplication.getProductInstance();
				if(product!=null) {
					final LightbridgeLink lightbridge = product.getAirLink().getLightbridgeLink();
					lightbridge.setEXTVideoInputPortEnabled(true, new CommonCallbacks.CompletionCallback() {
						@Override
						public void onResult(DJIError djiError) {
							if(djiError!=null) showToast(djiError.getDescription());
						}
					});
					lightbridge.setVideoDataChannel(c, new CommonCallbacks.CompletionCallback() {
						@Override
						public void onResult(DJIError djiError) {
							if(djiError==null){
								showToast("设置视频源成功");
							}else{
								showToast(djiError.getDescription());
							}
						}
					});
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{

			}
		});
		builder.show();
	}

	/**
	 * No-Double-Click Button
	 * Add in 2017.7.21
	 * */

	private abstract class NoDoubleClickListener implements View.OnClickListener{
		private static final int MIN_CLICK_DELAY_TIME=500;
		private long lastClickTime=0;
		@Override
		public void onClick(View v){
			long currentTime= Calendar.getInstance().getTimeInMillis();
			if(currentTime-lastClickTime>MIN_CLICK_DELAY_TIME){
				onNoDoubleClick(v);
			}
			lastClickTime=currentTime;
		}
		public void onNoDoubleClick(View v){
		}
	}
}
