package com.craft.texttospeech.views.adapters;

import android.content.Context;
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
import com.craft.texttospeech.views.SpeechToTextActivity;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewSttAdapter extends RecyclerView.Adapter<RecyclerViewSttAdapter.ViewHolder> {
    List<String> data ;
   Context context;
   SpeechToTextActivity activity;


    public RecyclerViewSttAdapter(List<String> data, SpeechToTextActivity activity) {
        this.data = data;
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_stored_stt_data_raw,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.textView.setText(data.get(position));
        holder.imageButtonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getViewModel().setFileContentsStt((ArrayList<String>) data);
                activity.getViewModel().deleteSTTFile(data.get(position));
                data.remove(data.get(position));
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
        TextView textView ;
        ImageButton imageButtonRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewSttStoredData);
            imageButtonRemove = itemView.findViewById(R.id.imageButtonDeleteSttRaw);


        }
    }
}
