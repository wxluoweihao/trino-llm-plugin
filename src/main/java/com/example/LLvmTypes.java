package com.example;

import static java.util.Locale.ENGLISH;

public enum LLvmTypes {
    chatgpt4;

    @Override
    public String toString()
    {
        return name().toLowerCase(ENGLISH);
    }
}
