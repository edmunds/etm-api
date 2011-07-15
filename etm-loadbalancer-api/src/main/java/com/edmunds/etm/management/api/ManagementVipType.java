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

import com.google.common.base.Predicate;

/**
 * The type of VIP.
 */
public enum ManagementVipType {

    /**
     * The vip details have been populated from the active web applications so IP's may not have been assigned.
     */
    MAVEN_ONLY,

    /**
     * The vip details have been loaded from ZooKeeper load balancer tree as a result all details are populated.
     */
    COMPLETE;

    /**
     * Predicate which tests if the current type can *NOT* contain vips of the given type.
     */
    private final Predicate<ManagementVip> cannotContainVipPredicate = new Predicate<ManagementVip>() {
        @Override
        public boolean apply(ManagementVip item) {
            return !canContain(item.getVipType());
        }
    };

    /**
     * Test if a collection of this type "can contain" vips of the given type.
     *
     * @param other the vip type to test.
     * @return true if the collection "can contain" the other type.
     */
    public boolean canContain(ManagementVipType other) {
        return this == other || other == COMPLETE;
    }

    /**
     * Returns the Predicate which tests if the current type can *NOT* contain vips of the given type.
     *
     * @return the Predicate.
     */
    public Predicate<ManagementVip> getCannotContainVipPredicate() {
        return cannotContainVipPredicate;
    }
}
