package com.craft.texttospeech.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.craft.texttospeech.models.LanguageStringFormat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewModelMain extends ViewModel {

    public Map<String, String> getMap() {
        return map;
    }

    private Map<String, String> map;

    public MutableLiveData<String> getLanguageLiveData() {
        return languageLiveData;
    }

    MutableLiveData<String> languageLiveData = new MutableLiveData<>();
    MutableLiveData<ArrayList<LanguageStringFormat>> languageAndCodeliveData = new MutableLiveData<>();

    public MutableLiveData<ArrayList<LanguageStringFormat>> getLanguageAndCodeliveData() {
        return languageAndCodeliveData;
    }

    public void fetchLanguages() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final ArrayList<LanguageStringFormat> arrayListLangFormat = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()){

                    arrayListLangFormat.add(d.getValue(LanguageStringFormat.class));

                }
                languageAndCodeliveData.setValue(arrayListLangFormat);
                map = new HashMap<>();
                for(int i=0;i<arrayListLangFormat.size();i++){
                    map.put(arrayListLangFormat.get(i).getName(),arrayListLangFormat.get(i).getCode());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
