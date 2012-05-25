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

import com.edmunds.etm.common.thrift.HostAddressDto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Container for a host and address. <p/> The host may be a host name or a dotted quad (a.b.c.d) ip address.
 */
public class HostAddress implements Comparable<HostAddress>, Serializable {

    private final String host;
    private final int port;

    private String ipAddress;
    private String hostName;

    /**
     * Initializes the HostAddress object using a Properties object (loaded from the ZooKeeper node).
     *
     * @param properties the properties to use to initialize the object.
     */
    public HostAddress(Properties properties) {
        this(properties.getProperty("address"), extractPort(properties));
    }

    /**
     * Constructor.
     *
     * @param host the host name or ip address.
     * @param port the port number.
     */
    public HostAddress(String host, int port) {
        Validate.notEmpty(host, "host cannot be empty");

        this.host = host;
        this.port = port;
    }

    /**
     * Gets the host as originally defined. This may be a host name or an ip address.
     *
     * @return host name or ip address
     */
    public String getHost() {
        return host;
    }

    /**
     * Gets the port number.
     *
     * @return port number
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets the host ip address.
     *
     * @return host ip address
     */
    public String getIpAddress() {
        if (ipAddress == null) {
            try {
                InetAddress addr = InetAddress.getByName(host);
                ipAddress = addr.getHostAddress();
            } catch (UnknownHostException e) {
                ipAddress = null;
            }
        }

        return ipAddress;
    }

    /**
     * Gets the host name.
     *
     * @return host name
     */
    public String getHostName() {
        if (hostName == null) {
            try {
                InetAddress addr = InetAddress.getByName(host);
                hostName = addr.getHostName();
            } catch (UnknownHostException e) {
                hostName = null;
            }
        }
        return hostName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HostAddress that = (HostAddress) o;

        return host.equals(that.host) && port == that.port;
    }

    @Override
    public int hashCode() {
        int result = host.hashCode();
        result = 31 * result + port;
        return result;
    }

    @Override
    public String toString() {
        return host + ':' + port;
    }

    @Override
    public int compareTo(HostAddress other) {
        if (other == null) {
            return 1;
        }

        // Host cannot be null (see constructor).
        if (host.equals(other.host)) {
            return port - other.port;
        }

        return host.compareTo(other.host);
    }

    /**
     * Creates a HostAddress from the given DTO.
     *
     * @param dto the DTO to read
     * @return a HostAddress object
     */
    public static HostAddress readDto(HostAddressDto dto) {
        if (dto == null) {
            return null;
        }
        return new HostAddress(dto.getHost(), dto.getPort());
    }

    /**
     * Creates a DTO from the given HostAddress.
     *
     * @param value a HostAddress object
     * @return a data transfer object
     */
    public static HostAddressDto writeDto(HostAddress value) {
        if (value == null) {
            return null;
        }
        return new HostAddressDto(value.host, value.port);
    }

    private static int extractPort(Properties properties) {
        final String portText = properties.getProperty("port");
        Validate.notEmpty(portText, "port cannot be empty");

        try {
            if (StringUtils.isNumeric(portText)) {
                return Integer.parseInt(portText);
            } else {
                throw new IllegalStateException("Port must be numeric: " + portText);
            }
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Port must be numeric: " + portText, e);
        }
    }
}
