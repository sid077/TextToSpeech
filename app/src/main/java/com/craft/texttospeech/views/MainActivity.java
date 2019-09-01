package com.craft.texttospeech.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;

import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.craft.texttospeech.R;
import com.craft.texttospeech.recievers.NetworkChangeReciever;
import com.craft.texttospeech.viewmodel.ViewModelMain;
import com.craft.texttospeech.views.services.TTSService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    CardView cardViewTts,cardViewLc,cardViewStt;
    ConstraintLayout constraintLayoutTts,constraintLayoutLc,constraintLayoutStt;
    ViewModelMain viewModel;
    BroadcastReceiver networkChangeReciever;
    ClipboardManager clipboardManager;
    static TextView textViewNoInternet;
  static  ImageView imageViewNoInternet;
  Switch switchTTSService;
    private FloatingActionButton fabDrawer;
    private boolean isDrawerOpen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        FirebaseApp.initializeApp(this);
        networkChangeReciever = new NetworkChangeReciever();
        registerReceiver(networkChangeReciever,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        constraintLayoutStt = findViewById(R.id.constrainLayoutSTT);
        constraintLayoutTts = findViewById(R.id.constrainLayoutTTS);
        constraintLayoutLc = findViewById(R.id.constrainLayoutLC);
        imageViewNoInternet = findViewById(R.id.imageViewNoInternet);
        textViewNoInternet = findViewById(R.id.textViewNoInternet);
        switchTTSService = findViewById(R.id.switchTTSService);
        fabDrawer = findViewById(R.id.floatingActionButtonDrawer);





        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);


        navigationView.setNavigationItemSelectedListener(this);


        fabDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDrawerOpen){
                    drawer.closeDrawer(GravityCompat.START);
                    isDrawerOpen = !isDrawerOpen;
                }
                else {
                    drawer.openDrawer(GravityCompat.START);
                    isDrawerOpen = !isDrawerOpen;
                }
            }
        });

        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {

            }
        });

        switchTTSService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              askForSystemOverlayPermission();
                Intent intent =new Intent();
                intent.setComponent(new ComponentName("com.craft.texttospeech","com.craft.texttospeech.views.services.TTSService"));

                if(isChecked)
                {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(intent);
                            return;
                        }

                    startService(intent);
                }
                else {
                    stopService(intent);
                }
            }
        });

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReciever);




    }
    public static void setVisibility(boolean b){
        if(b) {
            textViewNoInternet.setVisibility(View.VISIBLE);
            imageViewNoInternet.setVisibility(View.VISIBLE);
            textViewNoInternet.setText("Its seems,there's no Internet.");

        }
        else {
            textViewNoInternet.setVisibility(View.GONE);
            imageViewNoInternet.setVisibility(View.GONE);
        }
    }
    private void askForSystemOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:{

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(this)) {
                        //Permission is not available. Display error text.
                        finish();
                    }
                }

            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
