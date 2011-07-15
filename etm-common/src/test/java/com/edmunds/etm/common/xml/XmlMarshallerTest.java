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

import com.edmunds.etm.common.thrift.ClientConfigDto;
import com.edmunds.etm.common.thrift.UrlTokenCollectionDto;
import org.testng.annotations.Test;

import static com.edmunds.etm.common.xml.TestDataProvider.*;
import static org.testng.Assert.assertNotNull;

/**
 * Test for marshalling/unmarshalling Etm configuration file. <p/> <p/> Copyright (C) 2010 Edmunds.com <p/> <p/> Date:
 * Mar 4, 2010
 *
 * @author Aliaksandr Savin
 */
@Test
public class XmlMarshallerTest {

    public void testUnmarshallClientConfigOk() {
        byte[] bytes = loadConfigurationFromFile(DRR_CONFIG_FILE);
        ClientConfigDto config = XmlMarshaller.unmarshal(bytes, ClientConfigDto.class);
        assertNotNull(config);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testUnmarshallClientConfigFromXMLFailure() throws Exception {
        XmlMarshaller.unmarshal("failure".getBytes(), ClientConfigDto.class);
    }

    public void testMarshallClientConfigToByteArray() {
        ClientConfigDto config = TestDataProvider.createNewClientConfig();
        byte[] bytes = XmlMarshaller.marshalToByteArray(config);
        assertNotNull(bytes);
    }

    public void testMarshallClientConfigToXMLAndSave() {
        ClientConfigDto config = TestDataProvider.createNewClientConfig();
        XmlMarshaller.marshalToXMLAndSave(config, "target/test.xml");
    }

    public void testUnmarshallUrlTokensOk() {
        byte[] bytes = loadConfigurationFromFile(URL_TOKENS_FILE);
        UrlTokenCollectionDto dto = XmlMarshaller.unmarshal(bytes, UrlTokenCollectionDto.class);
        assertNotNull(dto);
    }

    public void testMarshallUrlTokensToByteArray() {
        UrlTokenCollectionDto coll = TestDataProvider.createNewUrlTokenCollection();
        byte[] bytes = XmlMarshaller.marshalToByteArray(coll);
        assertNotNull(bytes);
    }


    @Test(expectedExceptions = RuntimeException.class)
    public void testInvalidFilenameMarshallToXMLAndSave() {
        ClientConfigDto config = TestDataProvider.createNewClientConfig();
        XmlMarshaller.marshalToXMLAndSave(config, ".");
    }
}
