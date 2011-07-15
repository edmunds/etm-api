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

import com.edmunds.common.configuration.api.EnvironmentConfiguration;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Defines the ZooKeeper paths used by the agent to write its state.
 * <p/>
 * Each agent writes its state to a child node under {@code /agent/[version]/[environment]/connected}.
 *
 * @author Ryan Holmes
 */
@Component
public class AgentPaths {

    /**
     * The environment.
     */
    private final EnvironmentConfiguration environment;

    @Autowired
    public AgentPaths(EnvironmentConfiguration environment) {
        this.environment = environment;
    }

    /**
     * Gets the parent path for all connected agents.
     *
     * @return agent hosts path
     */
    public String getConnected() {
        return getRoot() + "/connected";
    }

    /**
     * Gets the path for an individual connected agent.
     *
     * @param host host name or ip address (must be a valid ZooKeeper identifier)
     * @return path for the given host
     */
    public String getConnectedHost(String host) {
        return getConnected() + "/" + host;
    }

    /**
     * Gets the parent path for restart master election.
     *
     * @return restart election path
     */
    public String getRestartElection() {
        return getRoot() + "/restart";
    }

    /**
     * Returns the paths to nodes that are necessary to run ETM.
     *
     * @return an list containing required node paths
     */
    public Set<String> getStructuralPaths() {
        return Sets.newHashSet(getConnected(), getRestartElection());
    }

    /**
     * Returns the client root.
     *
     * @return /client/1.0/ENVIRONMENT_KEY
     */
    private String getRoot() {
        StringBuilder sb = new StringBuilder(64);
        sb.append("/agent/1.1/");
        sb.append(environment.getDataCenter());
        sb.append(':');
        sb.append(environment.getSite());
        sb.append(':');
        sb.append(environment.getEnvironmentName());
        return sb.toString();
    }
}
