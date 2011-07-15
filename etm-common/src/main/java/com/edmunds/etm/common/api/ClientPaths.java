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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

/**
 * Provides ZooKeeper paths used by the client to write its state.
 * <p/>
 * The client tree has a single main node, {@code connected}, which has one child node per connected client. Each client
 * node stores a serialized {@link com.edmunds.etm.common.thrift.ClientConfigDto} object.
 * <pre>
 * /client/[VERSION]/[ENVIRONMENT]/connected
 * /client/[VERSION]/[ENVIRONMENT]/connected/[IP_ADDRESS:PORT]
 * </pre>
 */
@Component
public class ClientPaths {

    /**
     * The name of the hosts node.
     */
    public static final String NODE_NAME_CONNECTED = "connected";

    /**
     * The environment.
     */
    private final EnvironmentConfiguration environment;

    /**
     * Auto-wire constructor.
     *
     * @param environment the environment.
     */
    @Autowired
    public ClientPaths(EnvironmentConfiguration environment) {
        this.environment = environment;
    }

    /**
     * Returns the client root.
     *
     * @return /client/1.1/ENVIRONMENT_KEY
     */
    public String getRoot() {
        StringBuilder sb = new StringBuilder(64);
        sb.append("/client/1.1/");
        sb.append(environment.getDataCenter());
        sb.append(':');
        sb.append(environment.getSite());
        sb.append(':');
        sb.append(environment.getEnvironmentName());
        return sb.toString();
    }

    /**
     * Gets the parent path for all connected clients.
     *
     * @return the ZooKeeper connected node path
     */
    public String getConnected() {
        return getRoot() + "/" + NODE_NAME_CONNECTED;
    }

    /**
     * Gets the path for a given connected client.
     *
     * @param host client host name (must be a valid ZooKeeper identifier)
     * @return the path to the connected client node
     */
    public String getConnectedHost(String host) {
        return getConnected() + "/" + host;
    }

    /**
     * Returns the paths to nodes that are necessary to run ETM.
     *
     * @return an list containing required node paths
     */
    public Set<String> getStructuralPaths() {
        return Collections.singleton(getConnected());
    }
}
