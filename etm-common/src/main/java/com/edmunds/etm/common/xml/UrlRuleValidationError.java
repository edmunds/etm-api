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

/**
 * Validation error holder object.
 * <p/>
 * <p/> Copyright (C) 2010 Edmunds.com
 * <p/>
 * <p/> Date: Mar 15, 2010
 *
 * @author Aliaksandr Savin
 */
public class UrlRuleValidationError {

    /**
     * Rule, that contains syntax error.
     */
    private String rule;

    /**
     * Rule part that contains syntax error.
     */
    private String invalidRulePart;

    /**
     * Character position to the right of which the syntax error occurs.
     */
    private int characterPosition;

    /**
     * Expected expression.
     */
    private String expectedExpression;

    /**
     * Constructor with parameters.
     *
     * @param rule               rule, that contains syntax error.
     * @param invalidRulePart    rule part that contains syntax error.
     * @param characterPosition  character position to the right of which the syntax error occurs.
     * @param expectedExpression expected expression.
     */
    public UrlRuleValidationError(String rule,
                                  String invalidRulePart,
                                  int characterPosition,
                                  String expectedExpression) {
        this.rule = rule;
        this.invalidRulePart = invalidRulePart;
        this.characterPosition = characterPosition;
        this.expectedExpression = expectedExpression;
    }

    /**
     * Returns rule, that contains syntax error.
     *
     * @return rule, that contains syntax error.
     */
    public String getRule() {
        return rule;
    }

    /**
     * Returns rule part that contains syntax error.
     *
     * @return rule part that contains syntax error.
     */
    public String getInvalidRulePart() {
        return invalidRulePart;
    }

    /**
     * Returns character position to the right of which the syntax error occurs.
     *
     * @return character position to the right of which the syntax error occurs.
     */
    public int getCharacterPosition() {
        return characterPosition;
    }

    /**
     * Returns expected expression.
     *
     * @return expected expression.
     */
    public String getExpectedExpression() {
        return expectedExpression;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Syntax error in rule: ");
        sb.append(rule);
        sb.append(" At character position: ");
        sb.append(characterPosition);
        sb.append(" Invalid part: ");
        sb.append(invalidRulePart);
        sb.append(" Expected: ");
        sb.append(expectedExpression);

        return sb.toString();
    }
}
