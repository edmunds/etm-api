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

import com.edmunds.etm.common.thrift.AgentInstanceDto;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * An AgentInstance represents a running ETM Agent process.
 *
 * @author Ryan Holmes
 */
public class AgentInstance {

    private static final Logger logger = Logger.getLogger(AgentInstance.class);

    private final UUID id;
    private final String ipAddress;
    private final String version;
    private String activeRuleSetDigest;
    private RuleSetDeploymentEvent lastDeploymentEvent;
    private RuleSetDeploymentEvent lastFailedDeploymentEvent;
    private String hostName;

    /**
     * Constructs a new AgentInstance with the specified parameters.
     *
     * @param id unique agent identifier
     * @param version application version
     * @param ipAddress ip address of the agent host
     */
    public AgentInstance(UUID id, String ipAddress, String version) {
        Validate.notNull(id, "Unique ID is null");
        Validate.notEmpty(ipAddress, "IP address is empty");
        Validate.notEmpty(version, "Agent version is empty");
        this.id = id;
        this.ipAddress = ipAddress;
        this.version = version;
    }

    /**
     * Gets the unique agent identifier.
     *
     * @return unique agent identifier
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the agent host ip address.
     *
     * @return agent host ip address
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Gets the host name.
     *
     * @return host name
     */
    public String getHostName() {
        if(hostName == null) {
            try {
                InetAddress addr = InetAddress.getByName(ipAddress);
                hostName = addr.getHostName();
            } catch(UnknownHostException e) {
                return "Unknown";
            }
        }
        return hostName;
    }


    /**
     * Gets the application version.
     *
     * @return application version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets the MD5 digest of the currently deployed rule set.
     *
     * @return digest of the current rule set
     */
    public String getActiveRuleSetDigest() {
        return activeRuleSetDigest;
    }

    /**
     * Sets the MD5 digest of the currently deployed rule set.
     *
     * @param activeRuleSetDigest web server rule set MD5 digest
     */
    public void setActiveRuleSetDigest(String activeRuleSetDigest) {
        this.activeRuleSetDigest = activeRuleSetDigest;
    }

    /**
     * Gets the most recent rule set deployment event.
     *
     * @return last deployment event
     */
    public RuleSetDeploymentEvent getLastDeploymentEvent() {
        return lastDeploymentEvent;
    }

    /**
     * Sets the most recent rule set deployment event.
     *
     * @param lastDeploymentEvent last deployment event
     */
    public void setLastDeploymentEvent(RuleSetDeploymentEvent lastDeploymentEvent) {
        this.lastDeploymentEvent = lastDeploymentEvent;
    }

    /**
     * Gets the most recent failed rule set deployment event. <p/> This method returns the most recent deployment event
     * with a result other than {@link RuleSetDeploymentResult#OK}.
     *
     * @return last failed deployment event
     */
    public RuleSetDeploymentEvent getLastFailedDeploymentEvent() {
        return lastFailedDeploymentEvent;
    }

    /**
     * Sets the most recent failed rule set deployment event.
     *
     * @param lastFailedDeploymentEvent last failed deployment event
     */
    public void setLastFailedDeploymentEvent(RuleSetDeploymentEvent lastFailedDeploymentEvent) {
        this.lastFailedDeploymentEvent = lastFailedDeploymentEvent;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof AgentInstance)) {
            return false;
        }

        AgentInstance that = (AgentInstance) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("AgentInstance");
        sb.append("{id=").append(id);
        sb.append(", ipAddress='").append(ipAddress).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", activeRuleSetDigest='").append(activeRuleSetDigest).append('\'');
        sb.append(", lastDeploymentEvent=").append(lastDeploymentEvent);
        sb.append(", lastFailedDeploymentEvent=").append(lastFailedDeploymentEvent);
        sb.append('}');
        return sb.toString();
    }

    public static AgentInstance readDto(AgentInstanceDto dto) {
        if(dto == null) {
            return null;
        }

        UUID id = null;
        try {
            id = UUID.fromString(dto.getId());
        } catch(IllegalArgumentException e) {
            logger.error(String.format("Cannot parse UUID from dto: %s", dto), e);
            return null;
        }

        AgentInstance obj = null;
        try {
            obj = new AgentInstance(id, dto.getIpAddress(), dto.getVersion());
            obj.setActiveRuleSetDigest(dto.getActiveRuleSetDigest());
            obj.setLastDeploymentEvent(RuleSetDeploymentEvent.readDto(dto.getLastDeploymentEvent()));
            obj.setLastFailedDeploymentEvent(RuleSetDeploymentEvent.readDto(dto.getLastFailedDeploymentEvent()));
        } catch(IllegalArgumentException e) {
            logger.error("Invalid agent instance DTO", e);
        }

        return obj;
    }

    public static AgentInstanceDto writeDto(AgentInstance obj) {
        if(obj == null) {
            return null;
        }

        AgentInstanceDto dto = new AgentInstanceDto();
        dto.setId(obj.getId().toString());
        dto.setIpAddress(obj.getIpAddress());
        dto.setVersion(obj.getVersion());
        dto.setActiveRuleSetDigest(obj.getActiveRuleSetDigest());
        dto.setLastDeploymentEvent(RuleSetDeploymentEvent.writeDto(obj.getLastDeploymentEvent()));
        dto.setLastFailedDeploymentEvent(RuleSetDeploymentEvent.writeDto(obj.getLastFailedDeploymentEvent()));
        return dto;
    }
}
