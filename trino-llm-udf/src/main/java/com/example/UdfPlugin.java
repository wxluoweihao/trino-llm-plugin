package com.example;

import com.google.common.collect.ImmutableSet;
import io.trino.spi.Plugin;

import java.util.Set;

public final class UdfPlugin implements Plugin
{
    @Override
    public Set<Class<?>> getFunctions()
    {
        return ImmutableSet.<Class<?>>builder()
                .add(LLmChat.class)
                .build();
    }
}
