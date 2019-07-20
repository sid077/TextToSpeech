package com.craft.texttospeech.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.app.Dialog;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.craft.texttospeech.R;
import com.craft.texttospeech.models.LanguageStringFormat;
import com.craft.texttospeech.viewmodel.ViewModelMain;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;

import java.util.ArrayList;
import java.util.Locale;

public class TextToSpeechActivity extends AppCompatActivity {
        TextToSpeech textToSpeech;
        FloatingActionButton playFab,langFab;
        EditText editText;
        ViewModelMain viewModel;
    private ArrayList<String> languageNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_text_to_speech);
        playFab = findViewById(R.id.floatingActionButtonPlay);
        editText = findViewById(R.id.editTextTTS);
        langFab = findViewById(R.id.floatingActionButtonLanguage);


        viewModel = ViewModelProviders.of(this).get(ViewModelMain.class);
        final Observer<String> langObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                langFab.setImageResource(R.drawable.common_google_signin_btn_icon_dark);
                Toast.makeText(getApplicationContext(),"language changed",Toast.LENGTH_LONG).show();
            }
        } ;
        viewModel.getLanguageLiveData().observe(this,langObserver);
        viewModel.fetchLanguages();
        final Observer<ArrayList<LanguageStringFormat>> langAndCodeObserver = new Observer<ArrayList<LanguageStringFormat>>() {
            @Override
            public void onChanged(ArrayList<LanguageStringFormat> languageStringFormats) {
                languageNames = new ArrayList<>();
                for(int i=0; i<languageStringFormats.size();i++){
                    languageNames.add(languageStringFormats.get(i).getName());

                }


            }
        };
        viewModel.getLanguageAndCodeliveData().observe(this,langAndCodeObserver);

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
                                    viewModel.getLanguageLiveData().setValue(s);
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
