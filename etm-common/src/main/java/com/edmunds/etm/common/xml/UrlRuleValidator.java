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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import static com.edmunds.etm.common.xml.UrlRuleValidator.State.ASTERISK;
import static com.edmunds.etm.common.xml.UrlRuleValidator.State.START;
import static com.edmunds.etm.common.xml.UrlRuleValidator.State.SYMBOL;

/**
 * Parses the ETM client configuration file.
 */
public class UrlRuleValidator {

    private static final String SLASH = "/";

    /**
     * Precompiled pattern for keywords 'make', 'year', 'zipcode' and 'state'.
     */
    private static final Pattern KEYWORDS_PATTERN = Pattern.compile("^[(make|model|year|zipcode|state)]$",
            Pattern.CASE_INSENSITIVE);

    /**
     * Precompiled pattern for escaped words 'make', 'year', 'zipcode' and 'state'.
     */
    private static final Pattern ESCAPED_KEYWORDS_PATTERN = Pattern.compile("^\\\\[(make|model|year|zipcode|state)]$",
            Pattern.CASE_INSENSITIVE);

    /**
     * Precompiled pattern for zero or more directories.
     */
    private static final Pattern DIRECTORIES_PATTERN = Pattern.compile("^\\*{2}$");

    private final Collection<String> xmlRules;

    public UrlRuleValidator(final Collection<String> xmlRules) {
        this.xmlRules = xmlRules;
    }

    public List<UrlRuleValidationError> validate() {
        List<UrlRuleValidationError> errors = new ArrayList<UrlRuleValidationError>();

        for (String rule : xmlRules) {
            errors.addAll(validateRule(rule));
        }

        return errors;
    }

    /**
     * Validate single URL rule.
     *
     * @param rule the URL rule to validate
     * @return list of validation errors
     */
    private List<UrlRuleValidationError> validateRule(String rule) {
        List<UrlRuleValidationError> errors = new ArrayList<UrlRuleValidationError>();

        if (rule.startsWith(SLASH)) {
            int charPos = 0;
            for (StringTokenizer stz = new StringTokenizer(rule, SLASH, true); stz.hasMoreTokens();) {
                String s = stz.nextToken();
                if (!SLASH.equals(s) && !isTokenValid(s)) {
                    errors.add(new UrlRuleValidationError(rule, s, charPos, null));
                }
                charPos += s.length();
            }
        } else {
            errors.add(new UrlRuleValidationError(rule, rule, 0, SLASH));
        }
        return errors;
    }

    /**
     * Check syntax of single token.
     *
     * @param token token.
     * @return true if sintax is valid, otherwise - false.
     */
    private boolean isTokenValid(String token) {
        if (KEYWORDS_PATTERN.matcher(token).matches()
                || ESCAPED_KEYWORDS_PATTERN.matcher(token).matches()
                || DIRECTORIES_PATTERN.matcher(token).matches()) {
            return true;
        }

        State state = START;
        for (int i = 0; i < token.length(); i++) {
            char ch = token.charAt(i);
            if (ch == '*') {
                if (state.equals(ASTERISK)) {
                    return false;
                } else {
                    state = ASTERISK;
                }
            } else if (ch == '\\' && token.length() == i + 1) {
                return false;
            } else if (ch == '.' && token.length() == 1) {
                return false;
            } else {
                state = SYMBOL;
            }
        }
        return true;
    }

    /**
     * Finite machine state.
     */
    protected enum State {
        /**
         * Last symbol is any symbol except asterisk.
         */
        SYMBOL,
        /**
         * Last symbol is Asterisk.
         */
        ASTERISK,
        /**
         * Start state.
         */
        START
    }
}
