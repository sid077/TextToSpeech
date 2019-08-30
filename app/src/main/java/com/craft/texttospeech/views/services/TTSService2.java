package com.craft.texttospeech.views.services;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.inputmethodservice.Keyboard;
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
import androidx.recyclerview.widget.ItemTouchUIUtil;

import com.craft.texttospeech.R;

import java.util.Locale;

public class TTSService2 extends Service {
    private WindowManager windowManager;
    private View overlayView;
    int mWidth;
    private ImageView imageBTNTTS;
    boolean activity_background;
    private EditText editText;
    private TextToSpeech textToSpeech;
    private String text;
    private ClipboardManager clipboardManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        text =  clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
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
        if(overlayView == null){
            overlayView = LayoutInflater.from(this).inflate(R.layout.custom_tts_service,null);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.TOP|Gravity.LEFT;
            params.x= 0;
            params.y = 100;
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            windowManager.addView(overlayView,params);
            Display display = windowManager.getDefaultDisplay();
            final Point size = new Point();
            display.getSize(size);
            imageBTNTTS = overlayView.findViewById(R.id.imageViewTTSService);
            editText = overlayView.findViewById(R.id.textViewTTSService);
            if(text!=null)
                editText.setText(text);

            final RelativeLayout layout = (RelativeLayout) overlayView.findViewById(R.id.relativeTTS);
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

                    if(editText.getVisibility()==View.VISIBLE){

                        textToSpeech.speak(editText.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
                        editText.setVisibility(View.GONE);

                    }

                    else {
                        editText.setVisibility(View.VISIBLE);
                    }
                    Log.i("btnttsservice","pressed");
                }
            });
            editText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });


        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
