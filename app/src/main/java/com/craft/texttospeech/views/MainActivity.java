package com.craft.texttospeech.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
    ViewModelMain viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);


        cardViewLc = findViewById(R.id.cardViewLc);
        cardViewTts = findViewById(R.id.cardViewTts);
        cardViewStt = findViewById(R.id.cardviewStt);

        viewModel =  ViewModelProviders.of(this).get(ViewModelMain.class);

        cardViewTts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),TextToSpeechActivity.class));
            }
        });
        cardViewLc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LanguageConverterActivity.class));
            }
        });
        cardViewStt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SpeechToTextActivity.class));
            }
        });
    }
}
