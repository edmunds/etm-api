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
package com.edmunds.etm.loadbalancer.api;

/**
 * Provides the default values needed by ETM when creating new VIPs.
 * <p/>
 * The implementation of this interface is left up to the load balancer provider.
 * Since it may be possible to retrieve this information from a running load balancer,
 * however it is also common to just store the parameters in a property file.
 *
 * @author David Trott
 */
public interface LoadBalancerConfig {
    /**
     * The first address that can be used when creating new vips.
     *
     * @return the first ip address in dot quad notation: 1.2.3.4
     */
    String getIpPoolStart();

    /**
     * The last address (inclusive) that can be used when creating new vips.
     *
     * @return the last ip address in dot quad notation: 1.2.3.4
     */

    String getIpPoolEnd();

    /**
     * The default port number that should be used when creating new vips.
     *
     * @return the port number.
     */
    int getDefaultVipPort();
}
