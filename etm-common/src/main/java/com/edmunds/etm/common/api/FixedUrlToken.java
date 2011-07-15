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

import com.edmunds.etm.common.util.RegexUtil;
import java.util.Arrays;
import java.util.List;

/**
 * @author Ryan Holmes
 */
public class FixedUrlToken extends UrlToken {

    private String regex;

    public FixedUrlToken(String name, List<String> values) {
        super(name, values);
    }

    public FixedUrlToken(String name, String... values) {
        this(name, Arrays.asList(values));
    }

    @Override
    public String toRegex() {

        if(regex == null) {
            // list of explicit values; regular-expression-ize
            List<String> values = getValues();
            StringBuilder sb = new StringBuilder();
            sb.append('(');
            for(String value : values) {
                sb.append(RegexUtil.escapeRegex(value));
                sb.append('|');
            }

            // delete the trailing '|'
            if(!values.isEmpty()) {
                sb.deleteCharAt(sb.length() - 1);
            }

            sb.append(')');
            regex = sb.toString();
        }

        return regex;
    }

    @Override
    public UrlTokenType getType() {
        return UrlTokenType.FIXED;
    }

}
