package com.craft.texttospeech.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.craft.texttospeech.views.adapters.RecyclerViewTtsAdapter;
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
import java.util.Random;

public class TextToSpeechActivity extends AppCompatActivity {
    private static final String TAG = "TEXTTOSPEECHACT";
    String selectedLangCode;
    TextToSpeech textToSpeech;
        FloatingActionButton playFab,langFab, saveVoiceFab;
        EditText editText;


    public ViewModelMain getViewModel() {
        return viewModel;
    }

    ViewModelMain viewModel;
        RecyclerView recyclerView;
         ArrayList<String> languageNames = new ArrayList<>();
    private Observer<ArrayList<LanguageStringFormat>> langAndCodeObserver;
    private ArrayList<LanguageStringFormat> languageAndCode;
    private SeekBar seekBarSpeed,seekBarPitch;
    private TextToSpeechActivity activity = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_text_to_speech);
        intialiseView();

        seekBarPitch.setEnabled(false);
        seekBarSpeed.setEnabled(false);
        viewModel =ViewModelProviders.of(this).get(ViewModelMain.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
            editText.clearFocus();
            viewModel.fetchTTSStoredFile(this);

        }

        final Observer<String> langObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                selectedLangCode = s;
                textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS){
                            seekBarPitch.setEnabled(true);
                            seekBarSpeed.setEnabled(true);
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
                Toast.makeText(getApplicationContext(),"Language changed",Toast.LENGTH_LONG).show();
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
        Observer<Map<String,String>> storedFiles = new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String, String> stringStringMap) {
                recyclerView.setAdapter(new RecyclerViewTtsAdapter(stringStringMap,activity));
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.HORIZONTAL,false));
            }
        };
        viewModel.getStoredTTSData().observe(this,storedFiles);




        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(textToSpeech==null) {
                    FirebaseLanguageIdentification languageIdentification = FirebaseNaturalLanguage.getInstance().getLanguageIdentification();
                    languageIdentification.identifyLanguage(editText.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    Log.i("language", s);
                                    viewModel.getLanguageLiveData().setValue(s);

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i("language", "failed to identify");
                                }
                            });
                }
            }
        });

        playFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(editText.getText().toString().isEmpty()){
                   Toast.makeText(getApplicationContext(),"Please enter some text!",Toast.LENGTH_SHORT).show();
                   return;
               }

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
                if(editText.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please enter some text!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Build.VERSION.SDK_INT>=23){
                    viewModel.askForReadWritePermission(activity);
//                    if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
//                        saveVoiceFabCode();
//                    }
//                    else{
//                        Toast.makeText(getApplicationContext(),"Permission not granted...",Toast.LENGTH_LONG).show();
//                        viewModel.askForReadWritePermission(activity);
//
//                    }
//                    return;
                }
                saveVoiceFabCode();






            }
        });


    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: keyboard");
        super.onBackPressed();
    }

    private void intialiseView() {
        playFab = findViewById(R.id.floatingActionButtonPlay);
        editText = findViewById(R.id.editTextTTS);
        langFab = findViewById(R.id.floatingActionButtonLanguage);
        seekBarSpeed = findViewById(R.id.seekBarSpeed);
        seekBarPitch = findViewById(R.id.seekBarPitch);
        saveVoiceFab = findViewById(R.id.floatingActionButtonDownload);
        recyclerView = findViewById(R.id.recyclerViewStoredFilesTTS);


    }
    private void saveVoiceFabCode(){
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/downloads/TTS/text to speech");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(dir, new Random().nextInt() + ".mp3");
        int test = textToSpeech.synthesizeToFile(editText.getText().toString(), null, file, "tts");
        Log.i("voice saving", String.valueOf(test));
        Toast.makeText(getApplicationContext(), "Audio saved!", Toast.LENGTH_SHORT).show();
        viewModel.fetchTTSStoredFile(activity);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==2){
            if(grantResults.length>0&&grantResults[0]== PermissionChecker.PERMISSION_GRANTED){
                saveVoiceFabCode();
            }

        }
    }
}
