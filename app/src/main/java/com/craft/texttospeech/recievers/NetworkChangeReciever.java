package com.craft.texttospeech.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.craft.texttospeech.views.MainActivity;

public class NetworkChangeReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(!isOnline(context)){
            MainActivity.setVisibility(true);
            Toast.makeText(context,"Network disconnected.",Toast.LENGTH_LONG).show();
        }
        else {
            MainActivity.setVisibility(false);
            Toast.makeText(context,"You are good to go.",Toast.LENGTH_LONG).show();


        }

    }
    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}
