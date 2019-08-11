package com.craft.texttospeech.views.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.craft.texttospeech.R;
import com.craft.texttospeech.views.TextToSpeechActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecyclerViewTtsAdapter extends RecyclerView.Adapter<RecyclerViewTtsAdapter.ViewHolder> {

    Map<String ,String> data;
    Context context;
    private final ArrayList<String> fileNames;
    private MediaPlayer mediaPlayer ;
    TextToSpeechActivity activity;

    public RecyclerViewTtsAdapter(Map<String,String> data, TextToSpeechActivity activity) {
        this.data = data;
        this.context = activity.getApplicationContext();
        this.activity = activity;

        fileNames = new ArrayList<>();
        for (String s: data.keySet()){
            fileNames.add(s);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_stored_tts_data_raw,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.textView.setText(fileNames.get(position));

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        holder.checkBoxPlayPause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mediaPlayer = new MediaPlayer();

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        holder.checkBoxPlayPause.setChecked(false);
                        mp.release();
                    }
                });

                if(isChecked){
                    try {
                        mediaPlayer.setDataSource(data.get(fileNames.get(position)));
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {

                    if(mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.release();

                    }
                }
            }
        });
        holder.imageButtonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.remove(fileNames.get(position));
                activity.getViewModel().setFileContentsTts(data);
                activity.getViewModel().deleteTTSFile(fileNames.get(position));
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        if(data!=null)
        return data.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox checkBoxPlayPause;
        private final ImageButton imageButtonRemove;
        TextView textView ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewTtsStoredData);
            checkBoxPlayPause = itemView.findViewById(R.id.checkBoxPlayPauseTTSRaw);
            imageButtonRemove = itemView.findViewById(R.id.imageButtonDeleteTTSRaw);

        }
    }
}
