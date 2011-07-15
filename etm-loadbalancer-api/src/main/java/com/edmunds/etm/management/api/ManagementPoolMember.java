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
import com.edmunds.etm.common.thrift.ManagementPoolMemberDto;
import org.apache.commons.lang.Validate;

/**
 * Member of a pool attached to a VIP.
 */
public class ManagementPoolMember implements Comparable<ManagementPoolMember> {
    private final ManagementLoadBalancerState loadBalancerState;
    private final HostAddress hostAddress;

    /**
     * Standard constructor.
     *
     * @param loadBalancerState the current load balancer state of this entry.
     * @param hostAddress       the address of this entry.
     */
    public ManagementPoolMember(ManagementLoadBalancerState loadBalancerState, HostAddress hostAddress) {
        Validate.notNull(loadBalancerState);

        this.loadBalancerState = loadBalancerState;
        this.hostAddress = hostAddress;
    }

    public ManagementLoadBalancerState getLoadBalancerState() {
        return loadBalancerState;
    }

    public HostAddress getHostAddress() {
        return hostAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ManagementPoolMember that = (ManagementPoolMember) o;

        if (hostAddress != null ? !hostAddress.equals(that.hostAddress) : that.hostAddress != null) {
            return false;
        }
        return loadBalancerState == that.loadBalancerState;
    }

    @Override
    public int hashCode() {
        int result = loadBalancerState.hashCode();
        result = 31 * result + (hostAddress != null ? hostAddress.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ManagementPoolMember{" +
            "loadBalancerState=" + loadBalancerState +
            ", hostAddress=" + hostAddress +
            '}';
    }

    @Override
    public int compareTo(ManagementPoolMember other) {
        if (other == null) {
            return 1;
        }

        if (hostAddress == other.hostAddress || (hostAddress != null && hostAddress.equals(other.hostAddress))) {
            return loadBalancerState.compareTo(other.loadBalancerState);
        }

        return (hostAddress == null) ? -1 : hostAddress.compareTo(other.hostAddress);
    }

    /**
     * Creates a ManagementPoolMember from the given DTO.
     *
     * @param dto   the DTO to read
     * @param state load balancer state
     * @return a ManagementPoolMember object
     */
    public static ManagementPoolMember readDto(ManagementPoolMemberDto dto, ManagementLoadBalancerState state) {
        if (dto == null) {
            return null;
        }
        HostAddress address = HostAddress.readDto(dto.getHostAddress());
        return new ManagementPoolMember(state, address);
    }

    /**
     * Creates a DTO from the given ManagementPoolMember.
     *
     * @param value a ManagementPoolMember object
     * @return a data transfer object
     */
    public static ManagementPoolMemberDto writeDto(ManagementPoolMember value) {
        if (value == null) {
            return null;
        }
        HostAddressDto address = HostAddress.writeDto(value.getHostAddress());
        return new ManagementPoolMemberDto(address);
    }
}
