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
package com.edmunds.etm.common.xml;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.StringReader;

import static java.text.MessageFormat.format;

/**
 * XML marshaller/unmarshaller.
 * <p/>
 * This utility class provides static methods for marshalling/unmarshalling xml documents from/to data model objects.
 * <p/>
 * <p/> Copyright (C) 2010 Edmunds.com
 * <p/>
 * <p/> Date: Mar 4, 2010
 *
 * @author Aliaksandr Savin, Dzianis Krauchanka
 */
public final class XmlMarshaller {

    /**
     * Indent in XML file.
     */
    private static final int INDENT = 4;

    /**
     * The message used when file not found exception occurs.
     */
    private static final String FILE_NOT_FOUND_MESSAGE = "Can''t write file \"{0}\".";

    /**
     * UTF-8 encoding constant.
     */
    private static final String UTF_8_ENCODING = "UTF-8";

    /**
     * Private constructor.
     */
    private XmlMarshaller() {
    }

    /**
     * Unmarshalls data model object from XML document and returns it.
     *
     * @param xml   xml document as byte array
     * @param clazz target class for binding
     * @return data model object.
     */
    @SuppressWarnings("unchecked")
    public static <T> T unmarshal(byte[] xml, Class<T> clazz) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(xml);
            IBindingFactory bindingFactory = BindingDirectory.getFactory(clazz);
            IUnmarshallingContext unmarshallCtx = bindingFactory.createUnmarshallingContext();
            return (T) unmarshallCtx.unmarshalDocument(bais, UTF_8_ENCODING);
        } catch (JiBXException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Unmarshalls data model object from XML document and returns it.
     *
     * @param xml   xml document as a string
     * @param clazz target class for binding
     * @return data model object.
     */
    @SuppressWarnings("unchecked")
    public static <T> T unmarshal(String xml, Class<T> clazz) {
        try {
            StringReader reader = new StringReader(xml);
            IBindingFactory bindingFactory = BindingDirectory.getFactory(clazz);
            IUnmarshallingContext unmarshallCtx = bindingFactory.createUnmarshallingContext();
            return (T) unmarshallCtx.unmarshalDocument(reader, UTF_8_ENCODING);
        } catch (JiBXException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Marshalls data model object to XML document. Returns document as byte array.
     *
     * @param <T>    object type
     * @param object data model object
     * @return xml document as byte array
     */
    public static <T> byte[] marshalToByteArray(T object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IBindingFactory bindingFactory = BindingDirectory.getFactory(object.getClass());
            IMarshallingContext marshallCtx = bindingFactory.createMarshallingContext();
            marshallCtx.setIndent(INDENT);
            marshallCtx.marshalDocument(object, UTF_8_ENCODING, null, baos);
            return baos.toByteArray();
        } catch (JiBXException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Marshalls object and save XML document into file.
     *
     * @param <T>      object type
     * @param object   data model object
     * @param fileName full path to file
     */
    public static <T> void marshalToXMLAndSave(T object, String fileName) {
        try {
            IBindingFactory bindingFactory = BindingDirectory.getFactory(object.getClass());
            IMarshallingContext marshallCtx = bindingFactory.createMarshallingContext();
            marshallCtx.setIndent(INDENT);
            marshallCtx.marshalDocument(object, UTF_8_ENCODING, null, new FileOutputStream(fileName));
        } catch (JiBXException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(format(FILE_NOT_FOUND_MESSAGE, fileName), e);
        }
    }
}
