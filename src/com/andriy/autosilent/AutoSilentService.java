package com.andriy.autosilent;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class AutoSilentService extends Service {

	private SensorManager sensorManager;
	private final SensorEventListener sensorListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
				float pitchAngle = event.values[SensorManager.DATA_Y];
				if (pitchAngle < -120) {
					toXXMode(AudioManager.RINGER_MODE_VIBRATE);
				}
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	private void toXXMode(int mode) {
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		if (audioManager.getRingerMode() != mode) {
			audioManager.setRingerMode(mode);
		}
	}
	
	private void registerListener() {
		List<Sensor> sensors = sensorManager
				.getSensorList(Sensor.TYPE_ORIENTATION);
		sensorManager.registerListener(sensorListener, sensors.get(0),
				SensorManager.SENSOR_DELAY_NORMAL);		
	}
	
	private void unregisterListener() {
		sensorManager.unregisterListener(sensorListener);		
	}
	
	public class LocalBinder extends Binder {
		AutoSilentService getService() {
			return AutoSilentService.this;
		}
	}
	private final IBinder binder = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onCreate() {
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Bundle bundle = intent.getExtras();
		this.unregisterListener();
		if (bundle.getBoolean("register")) {
			this.registerListener();			
		}
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		this.unregisterListener();
		super.onDestroy();
	}

}
