package com.craft.texttospeech.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
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
        holder.cardViewStt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inten= new Intent();
                inten.setAction(Intent.ACTION_SEND);
               // inten.putExtra("Contents:",holder.textView.getText());
                inten.putExtra(Intent.EXTRA_TEXT,holder.textView.getText().toString());
                inten.setType("text/plain");
                Intent.createChooser(inten,"Choose one...");
                context.startActivity(inten);
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
        CardView cardViewStt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewSttStoredData);
            imageButtonRemove = itemView.findViewById(R.id.imageButtonDeleteSttRaw);
            cardViewStt = itemView.findViewById(R.id.cardviewSttRaw);


        }
    }
}
