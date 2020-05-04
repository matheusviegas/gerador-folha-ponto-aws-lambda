package br.com.mvsouza.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JSON {

    public static <T> T fromJson(String json, Class clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return (T) mapper.readValue(json, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static String toJson(Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            return null;
        }
    }

}
