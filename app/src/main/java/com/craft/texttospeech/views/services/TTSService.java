package com.craft.texttospeech.views.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.craft.texttospeech.R;
import com.craft.texttospeech.views.MainActivity;

import java.util.Locale;

public class TTSService extends Service {
    private WindowManager windowManager;
    private View overlayView;
    int mWidth;
    private ImageView imageBTNTTS;
    boolean activity_background;
    private EditText editText;
    private TextToSpeech textToSpeech;
    private ClipboardManager clipboardManager;
    Intent mIntent;
    int mstartId;
    int mflags;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mflags = flags;
        mIntent = intent;
        mstartId = startId;

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("service","created");

        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    startForegroundService(new Intent(getApplicationContext(),TTSService2.class));
//                    return;
//                }
                Intent intent = new Intent(getApplicationContext(),TTSService2.class);
                ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);
                intent.putExtra("text",clipboardManager.getPrimaryClip().getItemAt(0).getText().toString());
                startService(intent);
            }
        });


        String name = getPackageName() ;
        String CHANNEL_ID = getPackageName();
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,name,importance);
            channel.setDescription("hello");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)

                .setContentText("hey there!")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setColor(Color.BLACK)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                .build();
        notification.flags = notification.flags| Notification.FLAG_NO_CLEAR;
        startForeground(1, notification);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(overlayView!=null)
            windowManager.removeView(overlayView);
        Log.i("service","Destroyed");
    }
}
