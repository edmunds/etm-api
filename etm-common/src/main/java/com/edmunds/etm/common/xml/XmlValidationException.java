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
 * Validation exception occurs when xml file does not correspond to xsd schema.
 * <p/>
 * <p/> Copyright (C) 2010 Edmunds.com
 * <p/>
 * <p/> Date: Mar 3, 2010
 *
 * @author Aliaksandr Savin
 */
public class XmlValidationException extends Exception {

    /**
     * Constructor with message and cause.
     *
     * @param cause cause.
     */
    public XmlValidationException(Throwable cause) {
        super(cause);
    }
}
