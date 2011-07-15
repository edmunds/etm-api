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
package com.edmunds.etm.management.api;

import com.edmunds.etm.common.thrift.HttpMonitorDto;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * An HttpMonitor describes how to check the health of an application via HTTP.
 *
 * @author Ryan Holmes
 */
public class HttpMonitor implements Serializable {

    private static final Logger logger = Logger.getLogger(HttpMonitor.class);

    private final String url;
    private final String content;

    /**
     * Creates a new HttpMonitor with the specified values.
     *
     * @param url     the URL to monitor
     * @param content content expected to appear in the response body
     */
    public HttpMonitor(String url, String content) {
        Validate.notEmpty(url);
        Validate.notEmpty(content);
        this.url = url;
        this.content = content;
    }

    /**
     * Gets the URL  to monitor.
     *
     * @return monitor URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets the content expected to appear in the response.
     * <p/>
     * The application will be considered active if the expected content appears anywhere within the body of the HTTP
     * response. If this value is blank or null, the monitor will expect an HTTP status code of 200 in the response
     * headers.
     *
     * @return expected content
     */
    public String getContent() {
        return content;
    }

    public String getHttpRequest() {
        StringBuilder sb = new StringBuilder(64);
        sb.append("GET ");
        if (!url.startsWith("/")) {
            sb.append("/");
        }
        sb.append(url.trim());
        sb.append(" HTTP/1.0\r\n\r\n");
        return sb.toString();
    }

    public String getHttpResponse() {
        return content.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HttpMonitor)) {
            return false;
        }

        HttpMonitor that = (HttpMonitor) o;

        if (!content.equals(that.content)) {
            return false;
        }
        if (!url.equals(that.url)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + content.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("HttpMonitor");
        sb.append("{url='").append(url).append('\'');
        sb.append(", content='").append(content).append('\'');
        sb.append('}');
        return sb.toString();
    }

    /**
     * Creates an HttpMonitor from the given DTO.
     *
     * @param dto the DTO to read
     * @return an HttpMonitor object
     */
    public static HttpMonitor readDto(HttpMonitorDto dto) {
        if (dto == null) {
            return null;
        }
        try {
            return new HttpMonitor(dto.getUrl(), dto.getContent());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid HttpMonitor DTO", e);
            return null;
        }
    }

    /**
     * Creates a DTO from the given HttpMonitor.
     *
     * @param value an HttpMonitor object
     * @return a data transfer object
     */
    public static HttpMonitorDto writeDto(HttpMonitor value) {
        if (value == null) {
            return null;
        }
        return new HttpMonitorDto(value.url, value.content);
    }
}
