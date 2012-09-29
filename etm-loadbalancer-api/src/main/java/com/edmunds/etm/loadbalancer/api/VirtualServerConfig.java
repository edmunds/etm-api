package com.edmunds.etm.loadbalancer.api;

/**
 * Configuration object used while constructing a new virtual server.
 */
public class VirtualServerConfig {
    private final int port;

    public VirtualServerConfig(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }
}
