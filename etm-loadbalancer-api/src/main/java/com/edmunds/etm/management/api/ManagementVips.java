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

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static com.edmunds.etm.management.api.ManagementVip.MAVEN_MODULE_FUNCTION;
import static com.edmunds.etm.management.api.ManagementVip.MAVEN_MODULE_VALID;
import static com.edmunds.etm.management.api.ManagementVip.createSortedMap;
import static com.google.common.collect.Collections2.filter;

/**
 * Wrapper that contains the vips.
 *
 * @author David Trott
 * @author Ryan Holmes
 */
public class ManagementVips {
    public static final ManagementVips EMPTY_INSTANCE =
        new ManagementVips(ManagementVipType.COMPLETE, Collections.<ManagementVip>emptyList());

    private final Map<MavenModule, ManagementVip> vipsByMavenModule;
    private final int hashCode;
    private final ManagementVipType vipType;

    public ManagementVips(ManagementVipType vipType, Collection<ManagementVip> vips) {
        Validate.isTrue(filter(vips, vipType.getCannotContainVipPredicate()).isEmpty());

        this.vipType = vipType;
        vipsByMavenModule = createSortedMap(filter(vips, MAVEN_MODULE_VALID), MAVEN_MODULE_FUNCTION);
        hashCode = new HashCodeBuilder()
            .append(vipsByMavenModule)
            .toHashCode();
    }

    public ManagementVipType getVipType() {
        return vipType;
    }

    public ManagementVip getVip(MavenModule mavenModule) {
        return vipsByMavenModule.get(mavenModule);
    }

    public Collection<ManagementVip> getMavenModuleVips() {
        return vipsByMavenModule.values();
    }

    /**
     * Fetches the collection of vips.
     *
     * @return the collection of vips
     */
    public Collection<ManagementVip> getVips() {
        return vipsByMavenModule.values();
    }

    public boolean containsChanges() {
        for (ManagementVip vip : getVips()) {
            if (vip.hasChanges()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        // Optimize using the hashCode.
        if (o == null || getClass() != o.getClass() || hashCode != o.hashCode()) {
            return false;
        }
        ManagementVips other = (ManagementVips) o;

        return new EqualsBuilder()
            .append(vipsByMavenModule, other.vipsByMavenModule)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
