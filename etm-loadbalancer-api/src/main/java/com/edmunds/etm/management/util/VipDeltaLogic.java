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
package com.edmunds.etm.management.util;

import com.edmunds.etm.management.api.HostAddress;
import com.edmunds.etm.management.api.ManagementLoadBalancerState;
import com.edmunds.etm.management.api.ManagementPoolMember;
import com.edmunds.etm.management.api.ManagementVip;
import com.edmunds.etm.management.api.ManagementVipType;
import com.edmunds.etm.management.api.ManagementVips;
import com.google.common.collect.Lists;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.edmunds.etm.management.api.ManagementLoadBalancerState.ACTIVE;
import static com.edmunds.etm.management.api.ManagementLoadBalancerState.CREATE_REQUEST;
import static com.edmunds.etm.management.api.ManagementLoadBalancerState.DELETE_REQUEST;
import static com.edmunds.etm.management.api.ManagementVipType.COMPLETE;

/**
 * Provides the logic to delta to vips.
 */
public class VipDeltaLogic {
    /**
     * The logger.
     */
    private static final Logger logger = Logger.getLogger(VipDeltaLogic.class);

    private final ManagementVips oldVips;
    private final ManagementVips newVips;
    private final ManagementVipType vipsType;
    private final boolean copyNew;

    private static ManagementVipType calculateVipType(ManagementVips oldVips, ManagementVips newVips) {
        // No old vip so new vip type wins.
        if (oldVips == null) {
            return newVips.getVipType();
        }

        final ManagementVipType oldVipsType = oldVips.getVipType();
        final ManagementVipType newVipsType = newVips.getVipType();

        // Use new vip type if old is complete or they are the same.
        if (oldVipsType == COMPLETE || oldVipsType == newVipsType) {
            return newVipsType;
        }
        if (newVipsType == COMPLETE) {
            return oldVipsType;
        }

        final String msg = "Incompatible Management Vips for Delta: " + oldVips.getVipType() + " - " + newVipsType;
        logger.error(msg);
        throw new IllegalStateException(msg);
    }

    /**
     * Constructs a new delta logic.
     *
     * @param oldVips the old tree.
     * @param newVips the new tree.
     * @param copyNew should identical objects be copied for the old or new trees?
     */
    public VipDeltaLogic(ManagementVips oldVips, ManagementVips newVips,
                         boolean copyNew) {
        Validate.notNull(newVips, "newVips is null.");

        this.oldVips = oldVips;
        this.newVips = newVips;
        this.copyNew = copyNew;
        this.vipsType = calculateVipType(oldVips, newVips);
    }

    /**
     * Apply the delta operation.
     *
     * @return the delta result.
     */

    public ManagementVips delta() {

        // Result
        List<ManagementVip> deltaVips = Lists.newArrayList();

        if (oldVips == null) {
            for (ManagementVip newVip : newVips.getVips()) {
                deltaVips.add(updateState(CREATE_REQUEST, newVip));
            }
        } else {
            // Handle when the entire vip is deleted.
            for (ManagementVip oldVip : oldVips.getVips()) {
                if (newVips.getVip(oldVip.getMavenModule()) == null) {
                    deltaVips.add(updateState(DELETE_REQUEST, oldVip));
                }
            }

            // Handle all other cases
            for (ManagementVip newVip : newVips.getVips()) {
                final ManagementVip oldVip = oldVips.getVip(newVip.getMavenModule());

                if (oldVip == null) {
                    // Doesn't exist so create it.
                    deltaVips.add(updateState(CREATE_REQUEST, newVip));
                } else {
                    // Exists so check the members to see if they have changed.
                    deltaVips.add(deltaMembers(oldVip, newVip));
                }
            }
        }

        return new ManagementVips(vipsType, deltaVips);
    }

    /**
     * Performs a delta of each vip's pool members.
     *
     * @param oldVip the old vip.
     * @param newVip the new vip.
     * @return a new vip with the merge of the delta
     */
    private ManagementVip deltaMembers(ManagementVip oldVip, ManagementVip newVip) {
        final Map<HostAddress, ManagementPoolMember> oldMembers = oldVip.getPoolMembers();
        final Map<HostAddress, ManagementPoolMember> newMembers = newVip.getPoolMembers();

        // Result
        List<ManagementPoolMember> deltaMembers = Lists.newArrayList();

        // Handle Pool Member Deletions
        for (ManagementPoolMember oldMember : oldMembers.values()) {
            final HostAddress hostAddress = oldMember.getHostAddress();

            if (!newMembers.containsKey(hostAddress)) {
                deltaMembers.add(new ManagementPoolMember(DELETE_REQUEST, hostAddress));
            }
        }

        for (ManagementPoolMember newMember : newMembers.values()) {
            final HostAddress hostAddress = newMember.getHostAddress();

            if (oldMembers.containsKey(hostAddress)) {
                deltaMembers.add(new ManagementPoolMember(ACTIVE, hostAddress));
            } else {
                deltaMembers.add(new ManagementPoolMember(CREATE_REQUEST, hostAddress));
            }
        }

        return copyVip(ACTIVE, copyNew ? newVip : oldVip, deltaMembers);
    }

    private ManagementVip copyVip(
            ManagementLoadBalancerState state, ManagementVip vip, Collection<ManagementPoolMember> members) {

        return new ManagementVip(state, vip.getMavenModule(), vip.getHostAddress(),
                members, vip.getRootContext(), vip.getRules(), vip.getHttpMonitor());
    }

    private ManagementVip updateState(ManagementLoadBalancerState state, ManagementVip vip) {
        return copyVip(state, vip, updateState(state, vip.getPoolMembers().values()));
    }

    private List<ManagementPoolMember> updateState(
            ManagementLoadBalancerState state, Collection<ManagementPoolMember> poolMembers) {

        List<ManagementPoolMember> result = Lists.newArrayList();

        for (ManagementPoolMember poolMember : poolMembers) {
            result.add(new ManagementPoolMember(state, poolMember.getHostAddress()));
        }

        return result;
    }
}
