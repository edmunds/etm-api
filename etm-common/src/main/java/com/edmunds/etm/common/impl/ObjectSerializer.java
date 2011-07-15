/*
 * Copyright 2011 Edmunds.com, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.edmunds.etm.common.impl;

import org.apache.commons.lang.Validate;
import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Provides serialization and deserialization of Thrift objects.
 *
 * @author Ryan Holmes
 */
@Component
public class ObjectSerializer {

    TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
    private TDeserializer deserializer = new TDeserializer(new TBinaryProtocol.Factory());

    /**
     * Reads a value object from a byte array.
     *
     * @param src       the byte array to read
     * @param valueType the value object's Java class
     * @param <T>       value type
     * @return deserialized object
     * @throws java.io.IOException if deserialization failed
     */
    public <T extends TBase> T readValue(byte[] src, Class<T> valueType) throws IOException {

        Validate.notNull(src, "Source byte array is null");
        Validate.notNull(valueType, "Value type is null");

        T value;
        try {
            value = valueType.newInstance();
        } catch (Exception e) {
            String message = String.format("Unable to instantiate object of type %s", valueType.toString());
            throw new IllegalArgumentException(message, e);
        }

        try {
            deserializer.deserialize(value, src);
        } catch (TException e) {
            throw new IOException(e);
        }
        return value;
    }

    /**
     * Writes a value object to a byte array.
     *
     * @param value the value object to write
     * @return serialized byte array
     * @throws java.io.IOException if serialization fails
     */
    public byte[] writeValue(TBase value) throws IOException {
        Validate.notNull(value, "Value object is null");

        byte[] bytes;
        try {
            bytes = serializer.serialize(value);
        } catch (TException e) {
            throw new IOException(e);
        }

        return bytes;
    }
}
