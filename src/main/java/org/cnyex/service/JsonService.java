package org.cnyex.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class JsonService<T> {
    private final ObjectMapper mapper = new ObjectMapper();

    public String listToJson(List<T> list){
        try(var out = new ByteArrayOutputStream()){
            mapper.writeValue(out, list);
            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String toJson(T entity){
        try {
            return mapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
