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

import com.edmunds.common.configuration.api.EnvironmentConfiguration;
import com.edmunds.etm.management.api.HostAddress;
import com.edmunds.etm.management.api.MavenModule;
import org.apache.commons.lang.Validate;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A VirtualServer represents a load balancer virtual server object.
 *
 * @author Ryan Holmes
 */
public class VirtualServer implements Serializable {

    public static String createServerEnvironmentPrefix(String prefix,
                                                       EnvironmentConfiguration environment) {
        StringBuilder sb = new StringBuilder(64);
        sb.append(prefix);
        sb.append('_');
        sb.append(environment.getSite());
        sb.append('_');
        sb.append(environment.getEnvironmentName());
        sb.append('_');
        return sb.toString().replaceAll("[^a-zA-Z0-9_.-]", "-");
    }

    public static String createServerName(String prefix,
                                          MavenModule mavenModule,
                                          EnvironmentConfiguration environment) {
        StringBuilder sb = new StringBuilder(64);
        sb.append(prefix);
        sb.append('_');
        sb.append(environment.getSite());
        sb.append('_');
        sb.append(environment.getEnvironmentName());
        sb.append('_');
        sb.append(mavenModule.getGroupId());
        sb.append('_');
        sb.append(mavenModule.getArtifactId());
        sb.append('_');
        sb.append(mavenModule.getVersion());
        return sb.toString().replaceAll("[^a-zA-Z0-9_.-]", "-");
    }

    private final String name;
    private final HostAddress hostAddress;
    private final Set<PoolMember> poolMembers;

    public VirtualServer(String name, HostAddress hostAddress) {
        this(name, hostAddress, null);
    }

    public VirtualServer(String name, HostAddress hostAddress, Set<PoolMember> poolMembers) {
        Validate.notEmpty(name, "Name is empty");
        this.name = name;
        this.hostAddress = hostAddress;
        this.poolMembers = poolMembers != null ? poolMembers : new HashSet<PoolMember>();
    }

    public String getName() {
        return name;
    }

    public HostAddress getHostAddress() {
        return hostAddress;
    }

    public Set<PoolMember> getPoolMembers() {
        return Collections.unmodifiableSet(poolMembers);
    }

    public int getPoolSize() {
        return poolMembers.size();
    }

    public void addPoolMember(PoolMember member) {
        this.poolMembers.add(member);
    }

    public void addAllPoolMembers(Collection<PoolMember> members) {
        this.poolMembers.addAll(members);
    }

    public void removePoolMember(PoolMember member) {
        this.poolMembers.remove(member);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VirtualServer)) {
            return false;
        }

        VirtualServer that = (VirtualServer) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("VirtualServer");
        sb.append("{name='").append(name).append('\'');
        sb.append(", hostAddress=").append(hostAddress);
        sb.append(", poolMembers=").append(poolMembers);
        sb.append('}');
        return sb.toString();
    }
}
