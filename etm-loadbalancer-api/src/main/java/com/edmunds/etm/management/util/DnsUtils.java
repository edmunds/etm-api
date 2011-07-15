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
package com.edmunds.etm.management.util;

import org.apache.commons.lang.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * DNS utilities.
 * <p/>
 * The contents of this class should be moved to com.edmunds.common:configuration-dns.
 *
 * @author Ryan Holmes
 */
public final class DnsUtils {

    private static final long CONNECT_RETRY_WAIT_MILLIS = 5000;

    private DnsUtils() {
        // This class should never be instantiated
    }

    /**
     * Returns a set of all IP addresses for the given host names.
     *
     * @param hosts      array of host names or IP addresses
     * @param retryCount maximum number of times to retry DNS lookups
     * @return set of all IP addresses (may be empty if no addresses were found)
     */
    public static Set<InetAddress> getAddressesByHost(String[] hosts, int retryCount) {
        return getAddressesByHost(Arrays.asList(hosts), retryCount);
    }

    /**
     * Returns a set of all IP addresses for the given host names.
     *
     * @param hosts      collection of host names or IP addresses
     * @param retryCount maximum number of times to retry DNS lookups
     * @return set of all IP addresses (may be empty if no addresses were found)
     */
    public static Set<InetAddress> getAddressesByHost(Collection<String> hosts, int retryCount) {

        Set<InetAddress> allAddresses = new HashSet<InetAddress>();

        for (String host : hosts) {
            if (StringUtils.isBlank(host)) {
                continue;
            }
            InetAddress[] hostAddresses = getAddressesByHost(host, retryCount);
            if (hostAddresses != null) {
                allAddresses.addAll(Arrays.asList(hostAddresses));
            }
        }
        return allAddresses;
    }

    /**
     * Returns an array of all IP addresses for the given host name.
     * <p/>
     * The host name can either be a machine name or a textual representation of an IP address. If a literal IP address
     * is supplied, only the validity of the address format is checked.
     *
     * @param host       host name or IP address
     * @param retryCount maximum number of times to retry DNS lookups
     * @return array of IP addresses, or {@code null} if no addresses were found
     */
    public static InetAddress[] getAddressesByHost(String host, int retryCount) {

        if (StringUtils.isBlank(host)) {
            throw new IllegalArgumentException("Host name is blank");
        }

        InetAddress[] addresses = null;
        for (int i = 0; i < retryCount; i++) {

            try {
                addresses = InetAddress.getAllByName(host.trim());
            } catch (UnknownHostException e) {
                try {
                    Thread.sleep(CONNECT_RETRY_WAIT_MILLIS);
                } catch (InterruptedException e1) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        return addresses;
    }
}
