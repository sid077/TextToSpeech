package com.craft.texttospeech.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.craft.texttospeech.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.SQLOutput;
import java.util.ArrayList;

public class SpeechToTextActivity extends AppCompatActivity {
        EditText editTextStt;
        FloatingActionButton fabStart,fabSelectLang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_to_text);
        intialiseView();
        fabStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
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
    }
    void intialiseView(){
        editTextStt = findViewById(R.id.editTextStt);
        fabSelectLang = findViewById(R.id.floatingActionButtonSelectLangStt);
        fabStart = findViewById(R.id.floatingActionButtonStart);

    }
}
