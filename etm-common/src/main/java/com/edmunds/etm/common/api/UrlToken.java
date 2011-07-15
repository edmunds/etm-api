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

import com.edmunds.etm.common.thrift.UrlTokenDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * @author Ryan Holmes
 */
public abstract class UrlToken {
    private static final Logger logger = Logger.getLogger(UrlToken.class);

    private String name;
    private List<String> values;


    public UrlToken(String name, List<String> values) {
        Validate.notEmpty(name, "UrlToken name is empty");
        this.name = name;
        this.values = values == null ? new ArrayList<String>() : values;
    }

    /**
     * Returns the unique name of this token.
     *
     * @return the token name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the list of values represented by this token.
     *
     * @return list of string values
     */
    public List<String> getValues() {
        return Collections.unmodifiableList(values);
    }

    /**
     * Sets the values represented by this token.
     *
     * @param values list of string values
     */
    public void setValues(List<String> values) {
        this.values = values == null ? new ArrayList<String>() : values;
    }

    /**
     * Adds a new value to this URL token.
     *
     * @param value the value to add
     */
    public void addValue(String value) {
        this.values.add(value);
    }

    /**
     * Returns the regular expression that corresponds to this token.
     *
     * @return
     */
    public abstract String toRegex();

    /**
     * Gets the type of this token as an enum.
     *
     * @return the token type
     */
    public abstract UrlTokenType getType();

    /**
     * Factory method to create a new UrlToken of the specified type.
     *
     * @param type type of token to create
     * @param name token name
     * @param values list of values
     * @return new UrlToken object
     */
    public static UrlToken newUrlToken(UrlTokenType type, String name, List<String> values) {
        UrlToken token;
        switch(type) {
            case FIXED:
                token = new FixedUrlToken(name, values);
                break;
            case REGEX:
                token = new RegexUrlToken(name, values.get(0));
                break;
            default:
                throw new IllegalStateException(String.format("Unrecognized UrlTokenType: %s", type));
        }

        return token;
    }


    /**
     * Reads the specified DTO and returns a UrlToken object.
     *
     * @param dto the DTO to read
     * @return a UrlToken object
     */
    public static UrlToken readDto(UrlTokenDto dto) {
        if(dto == null) {
            return null;
        }

        UrlTokenType type;

        try {
            type = UrlTokenType.valueOf(dto.getType());
        } catch(RuntimeException ex) {
            logger.warn(String.format("Invalid URL token type read from DTO: %s; defaulting to FIXED", dto.getType()));
            type = UrlTokenType.FIXED;
        }

        return newUrlToken(type, dto.getName(), dto.getValues());
    }

    /**
     * Writes the specified UrlToken object as a DTO.
     *
     * @param obj a UrlToken object
     * @return a DTO
     */
    public static UrlTokenDto writeDto(UrlToken obj) {
        if(obj == null) {
            return null;
        }

        UrlTokenDto dto = new UrlTokenDto();
        dto.setName(obj.getName());
        dto.setType(obj.getType().name());
        dto.setValues(obj.getValues());
        return dto;

    }
}
