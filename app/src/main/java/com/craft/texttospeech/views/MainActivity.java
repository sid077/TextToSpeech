package com.craft.texttospeech.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchUIUtil;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.Keyboard;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.craft.texttospeech.R;
import com.craft.texttospeech.recievers.NetworkChangeReciever;
import com.craft.texttospeech.viewmodel.ViewModelMain;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;

import java.security.Permissions;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    CardView cardViewTts,cardViewLc,cardViewStt;
    ConstraintLayout constraintLayoutTts,constraintLayoutLc,constraintLayoutStt;
    ViewModelMain viewModel;
    BroadcastReceiver networkChangeReciever;
    ClipboardManager clipboardManager;
    static TextView textViewNoInternet;
  static  ImageView imageViewNoInternet;

    private FloatingActionButton fabDrawer;
    private boolean isDrawerOpen;
    private DrawerLayout drawer;
    private View.OnClickListener listenerStt, listenerTts,listenerTranslate;
    private CompoundButton.OnCheckedChangeListener checkedChangeListener;


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

        fabDrawer = findViewById(R.id.floatingActionButtonDrawer);

        registerListener();
        drawer = findViewById(R.id.drawer_layout);
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



        viewModel =  ViewModelProviders.of(this).get(ViewModelMain.class);




         constraintLayoutTts.setOnClickListener(listenerTts);
        constraintLayoutLc.setOnClickListener(listenerTranslate);
        constraintLayoutStt.setOnClickListener(listenerStt);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                Switch smartTts = findViewById(R.id.switcht);
                smartTts.setOnCheckedChangeListener(checkedChangeListener);

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

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


            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 2:
                if(grantResults.length>0&&grantResults[0]==PermissionChecker.PERMISSION_GRANTED)
                    viewModel.isReadWritePermissionGranted = true;
                else
                   viewModel.isReadWritePermissionGranted = false;

                break;
            case 3:
                if(grantResults.length>0&&grantResults[0]==PermissionChecker.PERMISSION_GRANTED)
                   viewModel.isRecordAudioPermissionGranted = true;
                else
                    viewModel.isRecordAudioPermissionGranted = false;

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
        int id = menuItem.getItemId();
        switch (id){
            case R.id.nav_home:
                break;
            case R.id.nav_speech_to_text:
                listenerStt.onClick(cardViewStt);
                break;
            case R.id.nav_text_to_speech:
                listenerTts.onClick(cardViewTts);
                break;
            case R.id.nav_translate:
                listenerTranslate.onClick(cardViewLc);
            case R.id.nav_share:
                break;
            case R.id.nav_rate_us:
                break;
            case R.id.nav_other_apps:

                Uri uri = Uri.parse("market://details?id="+"com.siddhant.craftifywallpapers");
                Intent i = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(i);
                break;
            case R.id.nav_smartTts:

//                if(smartTts.isChecked())
//
//                checkedChangeListener.onCheckedChanged(smartTts,true);
//                else
//                    checkedChangeListener.onCheckedChanged(smartTts,false);



        }
        drawer.closeDrawer(GravityCompat.START);


        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    private void registerListener(){
        listenerTts = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),TextToSpeechActivity.class));

            }
        };
        listenerTranslate = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LanguageConverterActivity.class));

            }
        };
        listenerStt = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SpeechToTextActivity.class));

            }
        };
        checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                startTttsService(isChecked);
            }
        };

        }
        private void startTttsService(boolean isChecked){
            askForSystemOverlayPermission();
            final Intent intent =new Intent();
            intent.setComponent(new ComponentName("com.craft.texttospeech","com.craft.texttospeech.views.services.TTSService"));

            if(intent.getComponent() == null){
                AlertDialog.Builder dialog = new AlertDialog.Builder(getApplicationContext());
                dialog.setTitle("Tap ok to download the service...");
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent1 = new Intent();

                    }
                });
            }

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


    }


