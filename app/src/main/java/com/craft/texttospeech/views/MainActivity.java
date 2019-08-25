package com.craft.texttospeech.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
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
    public void checkNetworkConnection(){
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setTitle("No internet Connection");
        builder.setMessage("Please turn on internet connection to continue");
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public boolean isNetworkConnectionAvailable(){
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        if(isConnected) {
            Log.d("Network", "Connected");
            return true;
        }
        else{
            checkNetworkConnection();
            Log.d("Network","Not Connected");
            return false;
        }
}

    @Override
    protected void onStart() {
        super.onStart();

        isNetworkConnectionAvailable();

    }
}
