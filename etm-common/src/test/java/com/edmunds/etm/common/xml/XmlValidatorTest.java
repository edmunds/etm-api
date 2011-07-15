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

import java.io.IOException;
import org.testng.annotations.Test;

import static com.edmunds.etm.common.xml.TestDataProvider.*;
import static com.edmunds.etm.common.xml.XmlValidator.CLIENT_CONFIG_XSD;
import static com.edmunds.etm.common.xml.XmlValidator.URL_TOKENS_XSD;

/**
 * Test for configValidator. <p/> <p/> Copyright (C) 2010 Edmunds.com <p/> <p/> Date: Mar 3, 2010
 *
 * @author Aliaksandr Savin
 */
@Test
public class XmlValidatorTest {
    public void testAppConfigOk() throws XmlValidationException, IOException {
        byte[] bytes = loadConfigurationFromFile(DRR_CONFIG_FILE);
        XmlValidator xmlValidator = new XmlValidator();
        xmlValidator.validate(bytes, CLIENT_CONFIG_XSD);
        // Repeat validation. Checks whether schema adds into validation map.
        xmlValidator.validate(bytes, CLIENT_CONFIG_XSD);
    }

    public void testUrlTokensOk() throws XmlValidationException, IOException {
        byte[] bytes = loadConfigurationFromFile(URL_TOKENS_FILE);
        XmlValidator xmlValidator = new XmlValidator();
        xmlValidator.validate(bytes, URL_TOKENS_XSD);
    }

    @Test(expectedExceptions = XmlValidationException.class)
    public void testValidateFailure() throws XmlValidationException {
        XmlValidator xmlValidator = new XmlValidator();
        xmlValidator.validate("failure".getBytes(), CLIENT_CONFIG_XSD);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testValidateWithInvalidFileName() throws XmlValidationException {
        XmlValidator xmlValidator = new XmlValidator();
        xmlValidator.validate("failure".getBytes(), null);
    }
}
