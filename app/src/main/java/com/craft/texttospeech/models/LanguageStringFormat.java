package com.craft.texttospeech.models;

import androidx.annotation.Nullable;

public class LanguageStringFormat {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private String code;

    @Override
    public boolean equals(@Nullable Object obj) {

        return super.equals(obj);
    }
}
