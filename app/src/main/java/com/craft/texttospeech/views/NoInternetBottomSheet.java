package com.craft.texttospeech.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.craft.texttospeech.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class NoInternetBottomSheet extends BottomSheetDialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.no_internet_layout,container,false);
        return view;

    }

}
