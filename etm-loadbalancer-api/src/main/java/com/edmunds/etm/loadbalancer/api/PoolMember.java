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

import com.edmunds.etm.management.api.HostAddress;
import org.apache.commons.lang.Validate;

import java.io.Serializable;

/**
 * A PoolMember associates a client node (indicated by the host address) to a virtual server.
 *
 * @author Ryan Holmes
 */
public class PoolMember implements Serializable {

    private final HostAddress hostAddress;

    public PoolMember(HostAddress hostAddress) {
        Validate.notNull(hostAddress);
        this.hostAddress = hostAddress;
    }

    public HostAddress getHostAddress() {
        return hostAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PoolMember)) {
            return false;
        }

        PoolMember that = (PoolMember) o;

        return hostAddress.equals(that.hostAddress);
    }

    @Override
    public int hashCode() {
        return hostAddress.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("PoolMember");
        sb.append("{hostAddress=").append(hostAddress);
        sb.append('}');
        return sb.toString();
    }
}
