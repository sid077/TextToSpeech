package com.craft.texttospeech.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.craft.texttospeech.R;
import com.craft.texttospeech.models.LanguageStringFormat;
import com.craft.texttospeech.viewmodel.ViewModelMain;
import com.craft.texttospeech.views.adapters.RecyclerViewSttAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SpeechToTextActivity extends AppCompatActivity {
    List<String> languageNames;

    public ViewModelMain getViewModel() {
        return viewModel;
    }

    ViewModelMain viewModel;
    RecyclerView recyclerViewStoredSTTData;
    EditText editTextStt;
        FloatingActionButton fabStart,fabSelectLang,fabPlay,fabSave;
        String selectedLangCode ="en";
    private ArrayList<LanguageStringFormat> languageAndCode;
    private TextToSpeech textToSpeech;
    private boolean isLangFetched;
    private SpeechToTextActivity activity = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_to_text);
        intialiseView();

       // editTextStt.clearFocus();
        editTextStt.requestFocus(View.FOCUS_DOWN);
        viewModel = ViewModelProviders.of(this).get(ViewModelMain.class);
        viewModel.fetchLanguages();
        viewModel.getSTTStoredFile(this);
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


//                langFab.setImageResource(R.drawable.common_google_signin_btn_icon_dark);
//                Toast.makeText(getApplicationContext(),"language changed",Toast.LENGTH_LONG).show();
            }
        } ;
        final Observer<List<String>> storedSTTDataObserver = new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
            isLangFetched=true;
            recyclerViewStoredSTTData.setAdapter(new RecyclerViewSttAdapter(strings,activity));
            recyclerViewStoredSTTData.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));

            }
        };
        viewModel.getStoredSTTData().observe(this,storedSTTDataObserver);
        viewModel.getLanguageLiveData().observe(this,langObserver);
       Observer langAndCodeObserver = new Observer<ArrayList<LanguageStringFormat>>() {
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


        fabStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, new Locale(selectedLangCode));
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"com.craft.texttospeech");
                SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
                RecognitionListener listener = new RecognitionListener() {
                    @Override
                    public void onReadyForSpeech(Bundle params) {
                        System.out.println("ready for speech");
                    }

                    @Override
                    public void onBeginningOfSpeech() {
                        System.out.println("speech starting");
                    }

                    @Override
                    public void onRmsChanged(float rmsdB) {

                    }

                    @Override
                    public void onBufferReceived(byte[] buffer) {
                        System.out.println(String.valueOf(buffer));
                    }

                    @Override
                    public void onEndOfSpeech() {
                        System.out.println("speech ended");
                    }

                    @Override
                    public void onError(int error) {
                        System.out.println("error occured");
                    }

                    @Override
                    public void onResults(Bundle results) {
                        ArrayList<String> voiceResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                        if(voiceResults == null)
                            System.out.println("no voice results");
                        else {
                            System.out.println("Printing matches: ");
                            for (String match : voiceResults) {
                                System.out.println(match);
                            }
                            editTextStt.setText(voiceResults.get(0));

                        }
                    }

                    @Override
                    public void onPartialResults(Bundle partialResults) {

                    }

                    @Override
                    public void onEvent(int eventType, Bundle params) {

                    }
                };
                speechRecognizer.setRecognitionListener(listener);
                speechRecognizer.startListening(intent);
            }
        });
        fabSelectLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isLangFetched){
                    Toast.makeText(getApplicationContext(),"Please wait while we fetch available languages!",Toast.LENGTH_SHORT).show();
                    return;
                }
                new LanguageSelectorForSttBottomSheet().show(getSupportFragmentManager(),"STT");
            }
        });
        fabPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextStt.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Oops,It seems like you spoke nothing",Toast.LENGTH_SHORT).show();
                    return;
                }
                textToSpeech.speak(editTextStt.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);

            }
        });
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextStt.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Oops,It seems like you spoke nothing",Toast.LENGTH_SHORT).show();
                    return;
                }
                File root = android.os.Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsoluteFile()+"/downloads/TTS/speech to text");
                if(!dir.exists()){
                    dir.mkdirs();
                }
                File file = new File(dir,System.currentTimeMillis()+".txt");
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(file);
                    outputStream.write(editTextStt.getText().toString().getBytes());
                    Toast.makeText(getApplicationContext(),"File saved",Toast.LENGTH_SHORT).show();
                    outputStream.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                viewModel.getSTTStoredFile(activity);


            }
        });
    }
    void intialiseView(){
        editTextStt = findViewById(R.id.editTextStt);
        fabSelectLang = findViewById(R.id.floatingActionButtonSelectLangStt);
        fabStart = findViewById(R.id.floatingActionButtonStart);
        fabPlay = findViewById(R.id.floatingActionButtonPlayStt);
        fabSave = findViewById(R.id.floatingActionButtonSaveStt);
        recyclerViewStoredSTTData = findViewById(R.id.recyclerViewStoredSttData);

    }

}
