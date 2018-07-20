package com.br.rhf.restretrofit.communication.dataserializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DataSerializer {


    ObjectMapper objectMapper = null;

    private static DataSerializer instance = null;

    public static final String DEFAULT_FORMAT_DATE = "yyyy-MM-dd HH:mm a z";

    private DataSerializer(DataSerializerMapperConfiguration dataSerializerMapperConfiguration) {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        DateFormat df = new SimpleDateFormat(dataSerializerMapperConfiguration.getFormatDate());
        objectMapper.setDateFormat(df);
    }


    public void setDateFormat(DateFormat df) {
        objectMapper.setDateFormat(df);
    }


    private DataSerializer() {
        this(new DataSerializerMapperConfiguration(DEFAULT_FORMAT_DATE));
    }

    public static DataSerializer getInstance(DataSerializerMapperConfiguration dataSerializerMapperConfiguration) {
        if (instance == null) {
            instance = new DataSerializer(dataSerializerMapperConfiguration);
        }

        return instance;
    }


    public static DataSerializer getInstance() {
        if (instance == null) {
            instance = new DataSerializer();
        }

        return instance;
    }

    public String toJson(Object content) {
        try {
            return objectMapper.writeValueAsString(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T toObject(String json, Class targetClass) {
        try {
            return (T) objectMapper.readValue(json, targetClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T toObject(String json, final TypeReference<T> type) {
        try {
            return (T) objectMapper.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> toObject(String jsonAnswer, CollectionType collectionType) {

        try {
            return (List<T>) objectMapper.readValue(jsonAnswer, collectionType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public <T> List<T> toObjectList(String jsonAnswer, Class<T> targetClass) {
        try {
            return (List<T>) objectMapper.readValue(jsonAnswer, TypeFactory.defaultInstance().constructCollectionType(List.class,
                    targetClass));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> ArrayList<T> toObjectArrayList(String jsonAnswer, Class<T> targetClass) {
        try {
            return (ArrayList<T>) objectMapper.readValue(jsonAnswer, TypeFactory.defaultInstance().constructCollectionType(List.class,
                    targetClass));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
