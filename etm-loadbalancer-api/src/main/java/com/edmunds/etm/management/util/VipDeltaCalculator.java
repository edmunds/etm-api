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

import com.edmunds.etm.management.api.ManagementVips;
import org.apache.commons.lang.Validate;
import org.springframework.stereotype.Component;

import static com.edmunds.etm.management.api.ManagementVipType.COMPLETE;

/**
 * Provides the logic to delta to vips.
 */
@Component
public class VipDeltaCalculator {

    /**
     * Performs a delta operation on vips managed at the load balancer.
     *
     * @param loadBalancerVips the previous list of load balancer vips.
     * @param clientVips       the current list of client vips.
     * @return the merged tree with flags to indicate where changes have been made
     */
    public ManagementVips deltaConnections(ManagementVips loadBalancerVips, ManagementVips clientVips) {
        Validate.notNull(loadBalancerVips, "loadBalancerVips is null.");
        Validate.isTrue(loadBalancerVips.getVipType() == COMPLETE);

        // Always copy the load balancer vips since they have the valid IP's
        return new VipDeltaLogic(loadBalancerVips, clientVips, false).delta();
    }

    /**
     * Performs a delta operation on vips managed at the web tier.
     *
     * @param oldVips the previous list of vips
     * @param newVips the current list of vips
     * @return the merged tree with flags to indicate where changes have been made
     */
    public ManagementVips deltaWebTier(ManagementVips oldVips, ManagementVips newVips) {
        Validate.isTrue(newVips.getVipType() == COMPLETE);

        // Always copy the new vips since they have the most up to date info.
        return new VipDeltaLogic(oldVips, newVips, true).delta();
    }

    /**
     * Perform a delta of between the load balancer and our local state.
     * <p/>
     * Goal: Update the local state to match the load balancer.
     *
     * @param localVips        the local state.
     * @param loadBalancerVips the state current active on the load balancer.
     * @return the merged tree with flags to indicate where changes have been made.
     */
    public ManagementVips deltaLoadBalancer(ManagementVips localVips, ManagementVips loadBalancerVips) {
        if (localVips != null) {
            Validate.isTrue(localVips.getVipType() == COMPLETE);
        }
        Validate.isTrue(loadBalancerVips.getVipType() == COMPLETE);

        // Use the most current info.
        return new VipDeltaLogic(localVips, loadBalancerVips, true).delta();
    }
}
