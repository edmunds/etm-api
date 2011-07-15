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
package com.edmunds.etm.common.api;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Ryan Holmes
 */
@Test
public class FixedUrlTokenTest {

    public void testConstructOk() {
        UrlToken token = new FixedUrlToken("test", "v1", "v2", "v3");
        assertNotNull(token);

        token = new FixedUrlToken("test", Collections.singletonList("v1"));
        assertNotNull(token);

        token = new FixedUrlToken("test");
        assertNotNull(token);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testConstructFailNullName() {
        UrlToken token = new FixedUrlToken(null, "v1");
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testConstructFailEmptyName() {
        UrlToken token = new FixedUrlToken("", "v1");
    }

    public void testToRegex() {
        UrlToken token = new FixedUrlToken("test", "v1", "v2", "v3");
        assertEquals(token.toRegex(), "(v1|v2|v3)");

        List<String> values = Lists.newArrayList("v1", "v2", "v3");
        token = new FixedUrlToken("test", values);
        assertEquals(token.toRegex(), "(v1|v2|v3)");


    }

    public void testToRegexEscaping() {
        UrlToken token = new FixedUrlToken("test", "^[a-z]\\d{9}$");
        assertEquals(token.toRegex(), "(\\^\\[a-z\\]\\d\\{9\\}\\$)");
    }
}
