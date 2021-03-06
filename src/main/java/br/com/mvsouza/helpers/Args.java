package br.com.mvsouza.helpers;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Args {

    public <T> void requireNonNull(T object, String message) throws IllegalArgumentException {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public void requireNonEmpty(String value, String message) throws IllegalArgumentException {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public void requireNonEmptyArray(Object[] array, String message) throws IllegalArgumentException {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public void requireArraySize(Object[] array, Integer size, String message) throws IllegalArgumentException {
        if (array == null || array.length != size) {
            throw new IllegalArgumentException(message);
        }
    }

}
