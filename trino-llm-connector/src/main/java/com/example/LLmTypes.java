package com.example;

import static java.util.Locale.ENGLISH;

public enum LLmTypes {
    openai;

    @Override
    public String toString()
    {
        return name().toLowerCase(ENGLISH);
    }
}
