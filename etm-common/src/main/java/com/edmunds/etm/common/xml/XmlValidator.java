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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

/**
 * XML validator for Etm configuration files. <p/> <p/> Copyright (C) 2010 Edmunds.com <p/> <p/> Date: Mar 3, 2010
 *
 * @author Aliaksandr Savin
 * @author Ryan Holmes
 */
@Component
public class XmlValidator {

    /**
     * XSD schema file name of Etm rules configuration.
     */
    public static final String CLIENT_CONFIG_XSD = "/schemas/etm-client-config.xsd";

    public static final String URL_TOKENS_XSD = "/schemas/etm-url-tokens.xsd";

    /**
     * W3 schema.
     */
    public static final String W3_ORG_XMLSCHEMA = "http://www.w3.org/2001/XMLSchema";

    /**
     * Validators map. Key is a XSD schema file name, value is a validator instance for schema.
     */
    private Map<String, Validator> validatorsMap = new HashMap<String, Validator>();

    /**
     * Validate configuration against XSD schema.
     *
     * @param configuration XML configuration as a byte array
     * @param schemaFile XSD schema file name.
     * @throws XmlValidationException when validation fails
     */
    public void validate(byte[] configuration, String schemaFile) throws XmlValidationException {
        Source source = new StreamSource(new ByteArrayInputStream(configuration));
        validate(source, schemaFile);
    }

    /**
     * Validate configuration against XSD schema.
     *
     * @param configuration XML configuration as a string
     * @param schemaFile XSD schema file name.
     * @throws XmlValidationException when validation fails
     */
    public void validate(String configuration, String schemaFile) throws XmlValidationException {
        Source source = new StreamSource(new StringReader(configuration));
        validate(source, schemaFile);
    }

    /**
     * Validate configuration against XSD schema.
     *
     * @param source XML configuration source
     * @param schemaFile XSD schema file name
     * @throws XmlValidationException if validation fails
     */
    private void validate(Source source, String schemaFile) throws XmlValidationException {
        try {
            if(!validatorsMap.containsKey(schemaFile)) {
                validatorsMap.put(schemaFile, initValidator(schemaFile));
            }
            validatorsMap.get(schemaFile).validate(source);
        } catch(SAXException e) {
            throw new XmlValidationException(e);
        } catch(IOException e) {
            throw new XmlValidationException(e);
        }
    }

    /**
     * Creates schema factory, opens schema file and initializes schema rulesConfigValidator.
     *
     * @param schemaFileName XSD schema file name.
     * @return validator object.
     * @throws org.xml.sax.SAXException when parser error occurs.
     * @throws java.io.IOException when IO error occurs.
     */
    private Validator initValidator(String schemaFileName) throws SAXException, IOException {
        InputStream is = null;
        try {
            SchemaFactory factory = SchemaFactory.newInstance(W3_ORG_XMLSCHEMA);
            is = getClass().getResourceAsStream(schemaFileName);
            Source source = new StreamSource(is);
            Schema schema = factory.newSchema(source);
            return schema.newValidator();
        } finally {
            if(is != null) {
                is.close();
            }
        }
    }
}
