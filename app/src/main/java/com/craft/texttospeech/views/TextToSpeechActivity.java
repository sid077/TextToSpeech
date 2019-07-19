package com.craft.texttospeech.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.craft.texttospeech.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;

import java.util.Locale;

public class TextToSpeechActivity extends AppCompatActivity {
        TextToSpeech textToSpeech;
        FloatingActionButton playFab,langFab;
        EditText editText;
        ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_text_to_speech);
        playFab = findViewById(R.id.floatingActionButtonPlay);
        editText = findViewById(R.id.editTextTTS);
        langFab = findViewById(R.id.floatingActionButtonLanguage);




//        FirebaseLanguageIdentification languageIdentification = FirebaseNaturalLanguage.getInstance().getLanguageIdentification();
//        languageIdentification.identifyLanguage(editText.getText().toString())
//                .addOnSuccessListener(new OnSuccessListener<String>() {
//                    @Override
//                    public void onSuccess(String s) {
//                        Log.i("language",s);
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.i("language","failed to identify");
//                    }
//                });


        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int tts = textToSpeech.setLanguage(Locale.US);
                    if(tts == TextToSpeech.LANG_MISSING_DATA||tts == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.i("intitialised","Lang error");
                    }
                    else {
                        Log.i("Lang","supported");
                    }
                }
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    FirebaseLanguageIdentification languageIdentification = FirebaseNaturalLanguage.getInstance().getLanguageIdentification();
                    languageIdentification.identifyLanguage(editText.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    Log.i("language",s);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i("language","failed to identify");
                                }
                            });
                }
            return false;
            }
        });

        playFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               int ttsSpeak = textToSpeech.speak(editText.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);

                if(ttsSpeak== TextToSpeech.ERROR)
                    Log.i("error","abort");
            }
        });

        langFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            new LanguageSelcetorBottomSheet().show(getSupportFragmentManager(),"LANG_SELECTOR");


            }
        });


    }
}
