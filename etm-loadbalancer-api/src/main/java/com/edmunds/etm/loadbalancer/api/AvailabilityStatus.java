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

/**
 * Enumerates the possible values of a virtual server's availability.
 *
 * @author Ryan Holmes
 */
public enum AvailabilityStatus {

    /**
     * No available status, indicates an error.
     */
    NONE,

    /**
     * The virtual server is currently available.
     */
    AVAILABLE,

    /**
     * The virtual server is currently unavailable.
     */
    UNAVAILABLE,

    /**
     * The virtual server is disabled.
     */
    DISABLED,

    /**
     * Status currently unknown, typically a temporary state.
     */
    UNKNOWN
}
