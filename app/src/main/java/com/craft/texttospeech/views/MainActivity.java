package com.craft.texttospeech.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.craft.texttospeech.R;
import com.craft.texttospeech.viewmodel.ViewModelMain;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    CardView cardViewTts,cardViewLc,cardViewStt;
    ConstraintLayout constraintLayoutTts,constraintLayoutLc,constraintLayoutStt;
    ViewModelMain viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);


        constraintLayoutStt = findViewById(R.id.constrainLayoutSTT);
        constraintLayoutTts = findViewById(R.id.constrainLayoutTTS);
        constraintLayoutLc = findViewById(R.id.constrainLayoutLC);

        viewModel =  ViewModelProviders.of(this).get(ViewModelMain.class);

        constraintLayoutTts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),TextToSpeechActivity.class));
            }
        });
        constraintLayoutLc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LanguageConverterActivity.class));
            }
        });
        constraintLayoutStt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SpeechToTextActivity.class));
            }
        });
    }
}
