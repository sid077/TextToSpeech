package com.craft.texttospeech.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.craft.texttospeech.R;
import com.craft.texttospeech.viewmodel.ViewModelMain;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;

import java.util.ArrayList;
import java.util.List;

public class LanguageSelcetorBottomSheet extends BottomSheetDialogFragment {
    ListView listView ;
    ArrayAdapter<String> adapter;
    TextToSpeechActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.language_dialog,container,false);
        listView = view.findViewById(R.id.listViewLanguageName);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        activity = (TextToSpeechActivity) getActivity();
        final List<String> langNames = activity.languageNames;

        adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),R.layout.simple_list_item,langNames);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {



            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                activity.viewModel.getLanguageLiveData().setValue(langNames.get(position));
                dismiss();
            }
        });


    }
}
