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
package com.edmunds.etm.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class whose only purpose so far is to allow escaping regex metacharacters.
 *
 * @author Julian Cardona
 */
public abstract class RegexUtil {

    /**
     * escapable regular expression metacaracters
     */
    private static final Pattern REGEX_SPECIAL_CHARS_PATTERN = Pattern.compile("([+\\[\\](){}\\$.?\\^|])");

    /**
     * Escapes the given string, replacing occurrences of regular expression metacaracters with their escaped form.
     *
     * @param string The string to escape.
     * @return The escaped string, i.e., the supplied string with any of '+', '[', ']', '(', ')', '{', '}', '$', '.',
     *         '?', '^', '|', ']' replaced by their corresponding escaped form, "\+", "\[", "\]", etc.
     */
    public static String escapeRegex(String string) {
        Matcher matcher = REGEX_SPECIAL_CHARS_PATTERN.matcher(string);
        return matcher.replaceAll("\\\\$1");
    }

}
