package com.futurewebdynamics.trader.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.TypeLiteral;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Created by Charlie on 23/08/2016.
 */
public class JsonDeserializer<T> implements IJsonDeserializer<T> {

    private ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T> klass;

    @Inject
    public JsonDeserializer(TypeLiteral<T> type){
        this.klass = (Class<T>) type.getRawType();
    }

    @Override
    public T get(String content) throws IOException {
        return objectMapper.readValue(content, klass);
    }

    /*@Override
    public List<T> getList(String content) throws IOException {
        return objectMapper.readValue(content, TypeFactory.collectionType(ArrayList.class, klass));
    }*/

}