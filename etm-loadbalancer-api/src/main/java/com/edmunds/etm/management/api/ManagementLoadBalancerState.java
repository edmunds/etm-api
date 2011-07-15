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

/**
 * Defines the state of the load balancer.
 */
public enum ManagementLoadBalancerState {
    /**
     * The state of the load balancer is not known.
     */
    UNKNOWN,

    /**
     * The ZooKeeper code wants the executor to create this item.
     */
    CREATE_REQUEST,

    /**
     * The item is active no change necessary.
     */
    ACTIVE,

    /**
     * The ZooKeeper code wants the executor to delete this item.
     */
    DELETE_REQUEST
}
