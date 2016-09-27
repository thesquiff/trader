package com.futurewebdynamics.trader.common;

import java.io.IOException;

/**
 * Created by Charlie on 24/08/2016.
 */

//http://stackoverflow.com/questions/13118798/deserializing-a-generic-type-with-jackson
public interface IJsonDeserializer<T> {

    T get(String content) throws IOException;

    //List<T> getList(String content) throws IOException;
}