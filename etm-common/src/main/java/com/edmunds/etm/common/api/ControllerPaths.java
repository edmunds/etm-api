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
 * Provides ZooKeeper paths used by the controller to write its state.
 * <p/>
 * The controller node has the following child nodes:
 * <p/>
 * connected: This is the parent node for all connected ETM controllers. It has one child note per controller.
 * <pre>
 * /controller/[VERSION]/[ENVIRONMENT]/connected
 * /controller/[VERSION]/[ENVIRONMENT]/connected/[IP_ADDRESS:PORT]
 * </pre>
 * <p/>
 * vips: This is where ETM stores its persistent load balancer data. It has one child node per vip, with each node
 * containing a serialized {@link com.edmunds.etm.common.thrift.ManagementVipDto} object. The object contains all of the
 * vip's data, including its set of pool members.
 * <pre>
 * /controller/[VERSION]/[ENVIRONMENT]/vips
 * /controller/[VERSION]/[ENVIRONMENT]/vips/[GROUP_ID:ARTIFACT_ID:VERSION]
 * </pre>
 * <p/>
 * master: This node has one child node for each ETM controller process. It is used to determine the master ETM
 * controller via a leader election algorithm.
 * <p/>
 * <pre>
 * /controller/[VERSION]/[ENVIRONMENT]/master
 * </pre>
 * <p/>
 * urltokens: This node has one child node per UrlToken, with each node containing a serialized {@link
 * com.edmunds.etm.common.thrift.UrlTokenDto} object.
 * <pre>
 * /controller/[VERSION]/[ENVIRONMENT]/urltokens
 * /controller/[VERSION]/[ENVIRONMENT]/urltokens/[TOKEN_NAME]
 * </pre>
 * <p/>
 * webconf: This  node has child nodes with web server configuration data. Currently, only a single child node, {@code
 * apache} is defined. This node holds the current Apache configuration file and is used by Apache ETM agents.
 * <p/>
 * <pre>
 * /controller/[VERSION]/[ENVIRONMENT]/webconf
 * /controller/[VERSION]/[ENVIRONMENT]/webconf/apache
 * </pre>
 *
 * @author Ryan Holmes
 */
@Component
public class ControllerPaths {

    /**
     * The environment.
     */
    private final EnvironmentConfiguration environment;

    private String rootPath;

    @Autowired
    public ControllerPaths(EnvironmentConfiguration environment) {
        this.environment = environment;
    }

    /**
     * Gets the parent path for all connected controllers.
     *
     * @return agent hosts path
     */
    public String getConnected() {
        return getRoot() + "/connected";
    }

    /**
     * Gets the path for a connected controller.
     *
     * @param host host name or ip address (must be a valid ZooKeeper identifier)
     * @return path for the given host
     */
    public String getConnectedHost(String host) {
        return getConnected() + "/" + host;
    }

    /**
     * Gets the path to the vips parent node.
     *
     * @return vips node path
     */
    public String getVips() {
        return getRoot() + "/vips";
    }

    /**
     * Gets the path to a vip node with a given name.
     *
     * @param vipName name of the vip
     * @return vip node path
     */
    public String getVip(String vipName) {
        return getVips() + "/" + vipName;
    }

    /**
     * Gets the path to the master election node.
     *
     * @return master election node path
     */
    public String getMaster() {
        return getRoot() + "/master";
    }

    /**
     * Gets the path to the URL token definitions node.
     *
     * @return token definitions node path
     */
    public String getUrlTokens() {
        return getRoot() + "/urltokens";
    }

    /**
     * Gets the path to the URL token with the given name.
     *
     * @param name token name
     * @return token node path
     */
    public String getUrlToken(String name) {
        return getUrlTokens() + "/" + name;
    }

    /**
     * Gets the path to the web server configuration parent node.
     *
     * @return web server configuration node path
     */
    public String getWebConf() {
        return getRoot() + "/webconf";
    }

    /**
     * Gets the path to the Apache web server configuration node.
     *
     * @return apache server configuration node path
     */
    public String getApacheConf() {
        return getWebConf() + "/apache";
    }

    /**
     * Gets the path to the HA Proxy configuration node.
     *
     * @return HA Proxy server configuration node path
     */
    public String getHaProxyConf() {
        return getWebConf() + "/haproxy";
    }

    /**
     * Returns the leaf nodes of that are necessary to run etm.
     *
     * @return currently returns /controller/1.0/master
     */
    public Set<String> getStructuralPaths() {
        return Sets.newHashSet(getConnected(), getVips(), getMaster(), getUrlTokens(),
                getApacheConf(), getHaProxyConf());
    }

    private String getRoot() {
        if (rootPath == null) {
            StringBuilder sb = new StringBuilder(64);
            sb.append("/controller/1.1/");
            sb.append(environment.getDataCenter());
            sb.append(':');
            sb.append(environment.getSite());
            sb.append(':');
            sb.append(environment.getEnvironmentName());
            rootPath = sb.toString();
        }
        return rootPath;
    }
}
