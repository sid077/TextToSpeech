package com.craft.texttospeech.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
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
    String selectedLangCode = "en";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_converter);
        initialiseView();
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


            }
        };
        viewModel.getLanguageAndCodeliveData().observe(this, langAndCodeObserver);
        editTextTranslateFrom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){

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
                                            Toast.makeText(getApplicationContext(),"model downloaded",Toast.LENGTH_SHORT).show();
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
            }
        });
        Observer<String> langObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                selectedLangCode = s;
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
                new LanguageSelcetorBottomSheet().show(getSupportFragmentManager(),"TO_LANG_SELECTOR");
            }
        });
        fabDoTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
    void initialiseView(){
        editTextTranslateFrom = findViewById(R.id.editTextTranslateFrom);
        editTextTranslatedText = findViewById(R.id.editTextTranslatedText);
        fabTranslateFrom = findViewById(R.id.floatingActionButtonTranslateFrom);
        fabTranslateTo = findViewById(R.id.floatingActionButtonTranslateTo);
        fabDoTranslate = findViewById(R.id.floatingActionButtonDoTranslate);


    }
}
