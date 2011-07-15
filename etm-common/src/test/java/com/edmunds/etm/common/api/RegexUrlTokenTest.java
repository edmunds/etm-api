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

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Ryan Holmes
 */
@Test
public class RegexUrlTokenTest {

    public void testConstructOk() {
        UrlToken token = new RegexUrlToken("test", "v1");
        assertNotNull(token);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testConstructFailNullName() {
        UrlToken token = new RegexUrlToken(null, "v1");
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testConstructFailEmptyName() {
        UrlToken token = new RegexUrlToken("", "v1");
    }

    public void testToRegex() {
        UrlToken token = new RegexUrlToken("test", "v1");
        assertEquals(token.toRegex(), "v1");

        token = new RegexUrlToken("test", "^(1|2.*{3})$");
        assertEquals(token.toRegex(), "^(1|2.*{3})$");
    }
}
