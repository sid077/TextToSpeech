package com.craft.texttospeech.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;
import java.util.List;

public class LanguageConverterActivity extends AppCompatActivity {
     List<String> languageNames;
    EditText editTextTranslateFrom,editTextTranslatedText;
    FloatingActionButton fabTranslateFrom,fabTranslateTo,fabDoTranslate;
    MainActivity mainActivity;
    ViewModelMain viewModel;
    Observer<ArrayList<LanguageStringFormat>> langAndCodeObserver;
    ArrayList<LanguageStringFormat> languageAndCode;
    private FirebaseTranslator translateLanguage;
    String selectedLangCode="en" ;
    private boolean isLangFetched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_converter);
        initialiseView();
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        fabDoTranslate.setEnabled(false);
        mainActivity = (MainActivity) this.getParent();
        viewModel = ViewModelProviders.of(this).get(ViewModelMain.class);

        viewModel.fetchLanguages();
        langAndCodeObserver = new Observer<ArrayList<LanguageStringFormat>>() {
            @Override
            public void onChanged(ArrayList<LanguageStringFormat> languageStringFormats) {
                languageAndCode = languageStringFormats;
                languageNames = new ArrayList<>();
                for(int i=0; i<languageStringFormats.size();i++){
                    languageNames.add(languageStringFormats.get(i).getName());

                }
                isLangFetched=true;



            }
        };
        viewModel.getLanguageAndCodeliveData().observe(this, langAndCodeObserver);
        editTextTranslateFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(editTextTranslateFrom.getText().toString().length()>5&&editTextTranslateFrom.getText().toString().length()<10){

                }

            }
        });

        Observer<String> langObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                selectedLangCode = s;
                detectLanguageAndDownloadModel();
            }
        };
        viewModel.getLanguageLiveData().observe(this,langObserver);
                editTextTranslateFrom.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                }
                return false;
            }
        });
        fabTranslateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isLangFetched){
                    Toast.makeText(getApplicationContext(),"Please wait until available languages are fetched!",Toast.LENGTH_LONG).show();
                    return;
                }
                new LanguageSelcetorBottomSheet().show(getSupportFragmentManager(),"TO_LANG_SELECTOR");
            }
        });
        fabTranslateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextTranslateFrom.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Oops,Nothing to detect!",Toast.LENGTH_SHORT).show();
                    return;
                }
                detectLanguageAndDownloadModel();

            }
        });
        fabDoTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextTranslateFrom.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please enter some text!",Toast.LENGTH_SHORT).show();
                }
                translateLanguage.translate(editTextTranslateFrom.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                editTextTranslatedText.setText(s);
                            }
                        });
            }
        });



    }

    public void detectLanguageAndDownloadModel() {
        if(editTextTranslateFrom.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),"It looks like you typed nothing,enter some text and select again!",Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseLanguageIdentification languageIdentification = FirebaseNaturalLanguage.getInstance().getLanguageIdentification();
        languageIdentification.identifyLanguage(editTextTranslateFrom.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {

                        Log.i("language",s);
                        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                                .setSourceLanguage(FirebaseTranslateLanguage.languageForLanguageCode(s))
                                .setTargetLanguage(FirebaseTranslateLanguage.languageForLanguageCode(selectedLangCode))
                                .build();
                        translateLanguage = FirebaseNaturalLanguage.getInstance().getTranslator(options);
                        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();

                        translateLanguage.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(),"Model downloaded,ready to Translate",Toast.LENGTH_SHORT).show();
                                fabDoTranslate.setEnabled(true);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Failed to download model,try changing language!",Toast.LENGTH_SHORT).show();

                            }
                        })
                        ;

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("language","failed to identify");
                    }
                });
    }

    void initialiseView(){
        editTextTranslateFrom = findViewById(R.id.editTextTranslateFrom);
        editTextTranslatedText = findViewById(R.id.editTextTranslatedText);
        fabTranslateFrom = findViewById(R.id.floatingActionButtonTranslateFrom);
        fabTranslateTo = findViewById(R.id.floatingActionButtonTranslateTo);
        fabDoTranslate = findViewById(R.id.floatingActionButtonDoTranslate);


    }
}
