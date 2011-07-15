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

import com.edmunds.etm.common.thrift.ManagementPoolMemberDto;
import com.edmunds.etm.common.thrift.ManagementVipDto;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.edmunds.etm.management.api.ManagementLoadBalancerState.ACTIVE;
import static com.edmunds.etm.management.api.ManagementLoadBalancerState.CREATE_REQUEST;
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.notNull;

/**
 * Represents a vip.
 */
public class ManagementVip implements Comparable<ManagementVip> {

    /**
     * Key function to extract the MavenModule.
     */
    public static final Function<ManagementVip, MavenModule> MAVEN_MODULE_FUNCTION =
        new Function<ManagementVip, MavenModule>() {
            @Override
            public MavenModule apply(ManagementVip vip) {
                return vip.getMavenModule();
            }
        };

    public static final Function<ManagementPoolMember, HostAddress> MEMBER_TO_HOST_ADDRESS =
        new Function<ManagementPoolMember, HostAddress>() {
            @Override
            public HostAddress apply(ManagementPoolMember poolMember) {
                return poolMember.getHostAddress();
            }
        };

    /**
     * Predicate to test if the MavenModule is non-null.
     */
    public static final Predicate<ManagementVip> MAVEN_MODULE_VALID = compose(notNull(), MAVEN_MODULE_FUNCTION);

    public static ManagementVipType getVipType(HostAddress hostAddress, MavenModule mavenModule) {
        Validate.notNull(mavenModule, "mavenModule is null.");

        if (hostAddress == null) {
            return ManagementVipType.MAVEN_ONLY;
        }

        return ManagementVipType.COMPLETE;
    }

    public static <K, V extends Comparable> ImmutableMap<K, V> createSortedMap(
        Collection<V> poolMembers, Function<V, K> indexFunction) {

        final List<V> members = Lists.newArrayList(poolMembers);
        Collections.sort(members);

        return Maps.uniqueIndex(members, indexFunction);
    }

    private final ManagementVipType vipType;
    private final ManagementLoadBalancerState loadBalancerState;
    private final MavenModule mavenModule;
    private final HostAddress hostAddress;
    private final Map<HostAddress, ManagementPoolMember> poolMembers;
    private final String rootContext;
    private final List<String> rules;
    private final HttpMonitor httpMonitor;

    private final int hashCode;

    /**
     * Standard constructor validates that rootContext and rules are in sync with the maven module parameter.
     *
     * @param loadBalancerState the state of the load balancer.
     * @param mavenModule       the maven module.
     * @param hostAddress       the host address.
     * @param poolMembers       is internally copied into an unmodifiable set by the constructor.
     * @param rootContext       the root context.
     * @param rules             the rules.
     * @param httpMonitor       the health monitor.
     */
    public ManagementVip(
        ManagementLoadBalancerState loadBalancerState, MavenModule mavenModule,
        HostAddress hostAddress, Collection<ManagementPoolMember> poolMembers,
        String rootContext, List<String> rules, HttpMonitor httpMonitor) {

        Validate.notNull(loadBalancerState, "loadBalancerState is null");
        Validate.notNull(mavenModule, "mavenModule is null");
        Validate.notNull(rootContext, "rootContext is null");
        Validate.notNull(rules, "rules are null");

        this.vipType = getVipType(hostAddress, mavenModule);
        this.loadBalancerState = loadBalancerState;
        this.mavenModule = mavenModule;
        this.hostAddress = hostAddress;

        this.poolMembers = createSortedMap(poolMembers, MEMBER_TO_HOST_ADDRESS);
        this.rootContext = rootContext;
        this.rules = rules;
        this.httpMonitor = httpMonitor;

        // Not using poolMembers in hashCode calculation.
        this.hashCode = new HashCodeBuilder()
            .append(mavenModule)
            .append(hostAddress)
            .append(rootContext)
            .append(rules)
            .append(vipType)
            .append(loadBalancerState)
            .append(httpMonitor)
            .toHashCode();

        // Ensure data is consistent.
        if (loadBalancerState == CREATE_REQUEST) {
            // However we can assert if we know this is a create request.
            Validate.notNull(rules, "rules cannot be null if mavenModule is valid");
        }
    }

    public ManagementVipType getVipType() {
        return vipType;
    }

    public ManagementLoadBalancerState getLoadBalancerState() {
        return loadBalancerState;
    }

    public MavenModule getMavenModule() {
        return mavenModule;
    }

    public HostAddress getHostAddress() {
        return hostAddress;
    }

    public Map<HostAddress, ManagementPoolMember> getPoolMembers() {
        return poolMembers;
    }

    public String getRootContext() {
        return rootContext;
    }

    public List<String> getRules() {
        return rules;
    }

    public HttpMonitor getHttpMonitor() {
        return httpMonitor;
    }

    /**
     * Indicates whether this vip has any changed data.
     *
     * @return true if the vip has changes, false otherwise
     */
    public boolean hasChanges() {
        if (loadBalancerState != ACTIVE) {
            return true;
        }

        for (ManagementPoolMember pm : getPoolMembers().values()) {
            if (pm.getLoadBalancerState() != ACTIVE) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ManagementVip other = (ManagementVip) o;

        // Probably only the first to will be used.
        return new EqualsBuilder()
            .append(mavenModule, other.mavenModule)
            .append(hostAddress, other.hostAddress)
            .append(rootContext, other.rootContext)
            .append(poolMembers, other.poolMembers)
            .append(rules, other.rules)
            .append(vipType, other.getVipType())
            .append(loadBalancerState, other.loadBalancerState)
            .append(httpMonitor, other.httpMonitor)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public int compareTo(ManagementVip other) {

        // CAUTION: ImmutableMap's don't implement compareTo() so I have excluded poolMembers.
        //          this decision may need to be revisited.

        // Probably only the first 2 will be used.
        return new CompareToBuilder()
            .append(mavenModule, other.mavenModule)
            .append(hostAddress, other.hostAddress)
            .append(rootContext, other.rootContext)
            .append(rules, other.rules)
            .append(vipType, other.getVipType())
            .append(loadBalancerState, other.loadBalancerState)
            .append(httpMonitor, other.httpMonitor)
            .toComparison();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ManagementVip");
        sb.append("{vipType=").append(vipType);
        sb.append(", loadBalancerState=").append(loadBalancerState);
        sb.append(", mavenModule=").append(mavenModule);
        sb.append(", hostAddress=").append(hostAddress);
        sb.append(", poolMembers=").append(poolMembers);
        sb.append(", rootContext='").append(rootContext).append('\'');
        sb.append(", rules=").append(rules);
        sb.append(", httpMonitor=").append(httpMonitor);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Creates a ManagementVip from the given DTO.
     *
     * @param dto   the DTO to read
     * @param state load balancer state of the new vip
     * @return a ManagementVip object
     */
    public static ManagementVip readDto(ManagementVipDto dto, ManagementLoadBalancerState state) {
        if (dto == null) {
            return null;
        }
        MavenModule mavenModule = MavenModule.readDto(dto.getMavenModule());
        HostAddress vipAddress = HostAddress.readDto(dto.getHostAddress());
        HttpMonitor httpMonitor = HttpMonitor.readDto(dto.getHttpMonitor());
        String rootContext = dto.getContextPath();
        List<String> rules = dto.getUrlRules();

        Set<ManagementPoolMember> members = Sets.newHashSetWithExpectedSize(dto.getPoolMembersSize());
        for (ManagementPoolMemberDto memberDto : dto.getPoolMembers()) {
            members.add(ManagementPoolMember.readDto(memberDto, ACTIVE));
        }
        return new ManagementVip(state, mavenModule, vipAddress, members, rootContext, rules, httpMonitor);
    }

    /**
     * Creates a DTO from the given ManagementVip.
     *
     * @param vip a ManagementVip object
     * @return a data transfer object
     */
    public static ManagementVipDto writeDto(ManagementVip vip) {
        if (vip == null) {
            return null;
        }
        ManagementVipDto dto = new ManagementVipDto();
        dto.setMavenModule(MavenModule.writeDto(vip.getMavenModule()));
        dto.setHostAddress(HostAddress.writeDto(vip.getHostAddress()));
        dto.setContextPath(vip.getRootContext());
        dto.setUrlRules(vip.getRules());
        dto.setHttpMonitor(HttpMonitor.writeDto(vip.getHttpMonitor()));

        Collection<ManagementPoolMember> poolMembers = vip.getPoolMembers().values();
        Set<ManagementPoolMemberDto> memberDtos = new HashSet<ManagementPoolMemberDto>(poolMembers.size());
        for (ManagementPoolMember pm : poolMembers) {
            memberDtos.add(ManagementPoolMember.writeDto(pm));
        }

        dto.setPoolMembers(memberDtos);
        return dto;
    }
}
