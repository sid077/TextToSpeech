package com.craft.texttospeech.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.SeekBar;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TextToSpeechActivity extends AppCompatActivity {
    String selectedLangCode;
    TextToSpeech textToSpeech;
        FloatingActionButton playFab,langFab, saveVoiceFab;
        EditText editText;
        MainActivity mainActivity;
        ViewModelMain viewModel;
         ArrayList<String> languageNames = new ArrayList<>();
    private Observer<ArrayList<LanguageStringFormat>> langAndCodeObserver;
    private ArrayList<LanguageStringFormat> languageAndCode;
    private SeekBar seekBarSpeed,seekBarPitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_text_to_speech);
        playFab = findViewById(R.id.floatingActionButtonPlay);
        editText = findViewById(R.id.editTextTTS);
        langFab = findViewById(R.id.floatingActionButtonLanguage);
        seekBarSpeed = findViewById(R.id.seekBarSpeed);
        seekBarPitch = findViewById(R.id.seekBarPitch);
        saveVoiceFab = findViewById(R.id.floatingActionButtonDownload);

        mainActivity = (MainActivity) this.getParent();



        viewModel =ViewModelProviders.of(this).get(ViewModelMain.class);
        final Observer<String> langObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                selectedLangCode = s;
                textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS){

                            int tts = textToSpeech.setLanguage(new Locale(selectedLangCode));
                            if(tts == TextToSpeech.LANG_MISSING_DATA||tts == TextToSpeech.LANG_NOT_SUPPORTED){
                                Log.i("intitialised","Lang error");
                            }
                            else {
                                Log.i("Lang","supported");
                            }
                        }
                    }
                });
                langFab.setImageResource(R.drawable.common_google_signin_btn_icon_dark);
                Toast.makeText(getApplicationContext(),"language changed",Toast.LENGTH_LONG).show();
            }
        } ;
        viewModel.getLanguageLiveData().observe(this,langObserver);
        viewModel.fetchLanguages();
        langAndCodeObserver = new Observer<ArrayList<LanguageStringFormat>>() {
            @Override
            public void onChanged(ArrayList<LanguageStringFormat> languageStringFormats) {
                languageAndCode = languageStringFormats;
                languageNames = new ArrayList<>();
                Map<String,String> map = new HashMap<>();

                for(int i=0; i<languageStringFormats.size();i++){
                    languageNames.add(languageStringFormats.get(i).getName());
                    map.put(languageStringFormats.get(i).getName(),languageStringFormats.get(i).getCode());


                }
                viewModel.setMap(map);


            }
        };
        viewModel.getLanguageAndCodeliveData().observe(this, langAndCodeObserver);




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
            public void onClick(View v) { ;


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
        seekBarSpeed.setMax(100);
        seekBarSpeed.setProgress(50);

        seekBarSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress==0)
                    textToSpeech.setSpeechRate(0.5f);
                textToSpeech.setSpeechRate((float)progress/50);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarPitch.setProgress(50);
        seekBarPitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textToSpeech.setPitch((float)progress/50);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        saveVoiceFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                File root = android.os.Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsolutePath()+"/downloads/TTS/text to speech");
                if(!dir.exists()){
                    dir.mkdir();
                }
                File file = new File(dir, System.currentTimeMillis()+".mp3");
                int test = textToSpeech.synthesizeToFile( editText.getText().toString(),null,file,"tts");
                Log.i("voice saving",String.valueOf(test));
                Toast.makeText(getApplicationContext(),"Audio saved!",Toast.LENGTH_SHORT).show();
            }
        });


    }
}
