package com.craft.texttospeech.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class ViewModelMain extends ViewModel {
    public MutableLiveData<String> getLanguageLiveData() {
        return languageLiveData;
    }

    MutableLiveData<String> languageLiveData;


}
