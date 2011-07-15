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
 * This exception indicates that pool member was not found on a load balancer.
 * <p/>
 * It is typically thrown when attempting to update or delete a pool member.
 *
 * @author Ryan Holmes
 */
public class PoolMemberNotFoundException extends Exception {
    
    public PoolMemberNotFoundException(String message) {
        super(message);
    }

    public PoolMemberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PoolMemberNotFoundException(Throwable cause) {
        super(cause);
    }
}
