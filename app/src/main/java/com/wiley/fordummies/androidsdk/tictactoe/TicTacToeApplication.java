package com.wiley.fordummies.androidsdk.tictactoe;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.startup.AppInitializer;
import androidx.work.Configuration;

import com.mapbox.maps.loader.MapboxMapsInitializer;
import com.wiley.fordummies.androidsdk.tictactoe.model.SettingsDataStore;

import kotlin.coroutines.CoroutineContext;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
import leakcanary.LeakCanary;
import timber.log.Timber;

@Keep
public class TicTacToeApplication extends Application implements Configuration.Provider {
	public static final String NOTIFICATION_CHANNEL_ID = "flickr_poll";

	@SuppressLint("StaticFieldLeak")
	private static Context mContext;
	private static final CoroutineScope mScope = new CoroutineScope() {
		@NonNull
		@Override
		public CoroutineContext getCoroutineContext() {
			return Dispatchers.getDefault();
		}
	};
	private static SettingsDataStore mDataStore;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		mDataStore = new SettingsDataStore(this, mScope);

        LeakCanary.Config config = LeakCanary.getConfig()
                .newBuilder()
                .retainedVisibleThreshold(5)
                .build();
        LeakCanary.setConfig(config);

		final boolean mIsDebuggable = ( 0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE ));

		if (mIsDebuggable) {
			Timber.DebugTree debugTree = new Timber.DebugTree();
			Timber.plant(debugTree);
			// LeakCanary.setConfig(LeakCanary.getConfig());
		}

		String name = getString(R.string.notification_channel_name);
		int importance = NotificationManager.IMPORTANCE_DEFAULT;
		NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
		NotificationManager notificationManager = getSystemService(NotificationManager.class);
		notificationManager.createNotificationChannel(channel);


		AppInitializer.getInstance(this)
				.initializeComponent(MapboxMapsInitializer.class);
	}

	@NonNull
	@Override
	public Configuration getWorkManagerConfiguration() {
		return new Configuration.Builder()
				.setMinimumLoggingLevel(android.util.Log.INFO)
				.build();
	}

	public static Context getContext() {
		return TicTacToeApplication.mContext;
	}

	public static SettingsDataStore getDataStore() { return mDataStore; }

	@Override
	public void onTerminate() {
		super.onTerminate();
		mContext = null;
		mDataStore = null;
	}
}
