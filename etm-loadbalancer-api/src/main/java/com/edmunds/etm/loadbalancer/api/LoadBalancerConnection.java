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

import com.edmunds.etm.management.api.HttpMonitor;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A LoadBalancerConnection provides a connection to a load balancer that allows a basic set of operations.
 * <p/>
 * LoadBalancerConnection implementations are typically stateful and should not be used concurrently by multiple
 * threads. As such, ETM requires that implementations be marked as Spring prototypes (rather than singletons) to ensure
 * that separate instances are created for each object that requires a connection. This can be acheived easily with the
 * Scope annotation:
 * <pre>
 * \@Scope("prototype")
 * </pre>
 *
 * @author Ryan Holmes
 */
public interface LoadBalancerConnection {

    public boolean connect();

    public boolean isActive();

    public Set<VirtualServer> getAllVirtualServers() throws RemoteException;

    public VirtualServer getVirtualServer(String serverName)
        throws VirtualServerNotFoundException, RemoteException;

    public boolean isVirtualServerDefined(String serverName) throws RemoteException;

    public Map<String, AvailabilityStatus> getAvailabilityStatus(List<String> serverNames)
        throws VirtualServerNotFoundException, RemoteException;

    public void createVirtualServer(VirtualServer server, HttpMonitor httpMonitor)
        throws VirtualServerExistsException, RemoteException;

    public void verifyVirtualServer(VirtualServer server, HttpMonitor httpMonitor);

    public void deleteVirtualServer(VirtualServer server)
        throws VirtualServerNotFoundException, RemoteException;

    public void addPoolMember(String serverName, PoolMember member)
        throws PoolMemberExistsException, RemoteException;

    public void removePoolMember(String serverName, PoolMember member)
        throws PoolMemberNotFoundException, RemoteException;

    public boolean saveConfiguration();
}
