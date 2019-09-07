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
import android.widget.LinearLayout;
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
    private String text="";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mflags = flags;
        mIntent = intent;
        mstartId = startId;

        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        try {
            text = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){

                    int tts = textToSpeech.setLanguage(new Locale("en"));
                    if(tts == TextToSpeech.LANG_MISSING_DATA||tts == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.i("intitialised","Lang error");
                    }
                    else {
                        Log.i("Lang","supported");
                    }
                }
            }
        });
        if(intent  !=null){
            activity_background = intent.getBooleanExtra("activity_background",false);

        }
        if(overlayView == null) {
            overlayView = LayoutInflater.from(this).inflate(R.layout.custom_tts_service, null);

            editText = overlayView.findViewById(R.id.textViewTTSService);
            clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                    if(clipboardManager.hasPrimaryClip())
                        try {
                            editText.setText(clipboardManager.getPrimaryClip().getItemAt(0).getText().toString());
                        }
                    catch (NullPointerException e){
                            e.printStackTrace();
                    }
                }
            });

            WindowManager.LayoutParams params;
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
                params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            }
            else {
                params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE
                        ,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            }
            params.gravity = Gravity.BOTTOM | Gravity.END;
            params.x = 0;
            params.y = 0;
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            windowManager.addView(overlayView, params);
            Display display = windowManager.getDefaultDisplay();
            final Point size = new Point();
            display.getSize(size);
            imageBTNTTS = overlayView.findViewById(R.id.imageViewTTSService);
            if (text != null)
                editText.setText(text);

            final LinearLayout layout  = (LinearLayout) overlayView.findViewById(R.id.relativeTTS);
            ViewTreeObserver vto = layout.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int width = layout.getMeasuredWidth();

                    //To get the accurate middle of the screen we subtract the width of the floating widget.
                    mWidth = size.x - width;

                }
            });
            imageBTNTTS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (editText.getVisibility() == View.VISIBLE) {
                        if(editText.getText()!=null)
                        textToSpeech.speak(editText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                        editText.setVisibility(View.GONE);

                    } else {
                        editText.setVisibility(View.VISIBLE);
                    }
                    Log.i("btnttsservice", "pressed");
                }
            });
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("service","created");


//        clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
//            @Override
//            public void onPrimaryClipChanged() {
////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                    startForegroundService(new Intent(getApplicationContext(),TTSService2.class));
////                    return;
////                }
//                Intent intent = new Intent(getApplicationContext(),TTSService2.class);
//                ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);
//                intent.putExtra("text",clipboardManager.getPrimaryClip().getItemAt(0).getText().toString());
//                startService(intent);
//            }
//        });


        String name = getPackageName() ;
        String CHANNEL_ID = getPackageName();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,name,importance);
            channel.setDescription("hello");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)

                .setContentText("Smart text to speech service is running...")
                .setSmallIcon(R.drawable.ic_text_fields_black_24dp)
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
