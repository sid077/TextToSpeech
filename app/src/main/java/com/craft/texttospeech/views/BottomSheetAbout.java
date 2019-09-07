package com.craft.texttospeech.views;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.craft.texttospeech.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class BottomSheetAbout extends BottomSheetDialogFragment {
    TextView textView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_bottomsheet,container,false);
        textView = view.findViewById(R.id.textViewIconDetails);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        textView.setText(Html.fromHtml("Icons made by <a href=\"http://www.freepik.com\">Freepik (www.freepik.com)</a> from <a href=\"https://www.flaticon.com/\" " +
                "title=\"Flaticon\">Flaticon(www.flaticon.com)</a> "));



    }
}
