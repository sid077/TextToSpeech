package com.craft.texttospeech.views.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.craft.texttospeech.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecyclerViewTtsAdapter extends RecyclerView.Adapter<RecyclerViewTtsAdapter.ViewHolder> {

    Map<String ,String> data;
    Context context;
    private final ArrayList<String> fileNames;

    public RecyclerViewTtsAdapter(Map<String,String> data, Context context) {
        this.data = data;
        this.context = context;
        fileNames = new ArrayList<>();
        for (String s: data.keySet()){
            fileNames.add(s);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_stored_stt_data_raw,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.textView.setText(fileNames.get(position));
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(data.get(fileNames.get(position)));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
        TextView textView ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewSttStoredData);

        }
    }
}
