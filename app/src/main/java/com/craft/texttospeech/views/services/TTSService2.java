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

//            editText.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
//
//
//        }
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
