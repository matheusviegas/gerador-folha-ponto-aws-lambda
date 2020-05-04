package br.com.mvsouza.helpers;

import java.lang.reflect.Array;
import java.util.Optional;

public class Args {

    public static <T> void requireNonNull(T object, String message) throws IllegalArgumentException {
        Optional.ofNullable(object).orElseThrow(() -> new IllegalArgumentException(message));
    }

    public static void requireNonEmpty(String value, String message) throws IllegalArgumentException {
        Optional.ofNullable(value).filter(s -> !s.isEmpty()).orElseThrow(() -> new IllegalArgumentException(message));
    }

    public static void requireNonEmptyArray(Object[] array, String message) throws IllegalArgumentException {
        Optional.ofNullable(array.length).filter(length -> length > 0).orElseThrow(() -> new IllegalArgumentException(message));
    }

    public static void requireArraySize(Object[] array, Integer size, String message) throws IllegalArgumentException {
        Optional.ofNullable(array.length).filter(length -> length.equals(size)).orElseThrow(() -> new IllegalArgumentException(message));
    }

}
