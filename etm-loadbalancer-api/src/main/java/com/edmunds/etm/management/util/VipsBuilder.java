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

import com.edmunds.etm.common.impl.ObjectSerializer;
import com.edmunds.etm.common.thrift.ManagementVipDto;
import com.edmunds.etm.management.api.ManagementVip;
import com.edmunds.etm.management.api.ManagementVips;
import com.edmunds.zookeeper.treewatcher.ZooKeeperTreeNode;
import com.google.common.collect.Lists;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;

import static com.edmunds.etm.management.api.ManagementLoadBalancerState.ACTIVE;
import static com.edmunds.etm.management.api.ManagementVipType.COMPLETE;

/**
 * Converts a ZooKeeper node tree into a ManagementVips object.
 *
 * @author David Trott
 * @author Ryan Holmes
 */
public class VipsBuilder {

    private static final Logger logger = Logger.getLogger(VipsBuilder.class);

    private final ZooKeeperTreeNode rootNode;
    private final ObjectSerializer objectSerializer;

    /**
     * Constructor.
     *
     * @param rootNode         the root node of the ZooKeeper tree structure.
     * @param objectSerializer object serializer
     */
    public VipsBuilder(ZooKeeperTreeNode rootNode, ObjectSerializer objectSerializer) {
        Validate.notNull(rootNode, "rootNode is null");
        Validate.notNull(objectSerializer, "objectSerializer is null");
        this.rootNode = rootNode;
        this.objectSerializer = objectSerializer;
    }

    /**
     * Converts a ZooKeeper node tree into a ManagementVips object.
     *
     * @return the new ManagementVips object or null if the structure is not fully initialized.
     */
    public ManagementVips generateVips() {

        final List<ManagementVip> vips = Lists.newArrayList();

        for (ZooKeeperTreeNode vipNode : rootNode.getChildren().values()) {
            ManagementVipDto vipDto;
            try {
                vipDto = objectSerializer.readValue(vipNode.getData(), ManagementVipDto.class);
                ManagementVip vip = ManagementVip.readDto(vipDto, ACTIVE);
                vips.add(vip);
            } catch (IOException e) {
                logger.error(String.format("Unable to read vip node: %s", vipNode.getPath()), e);
            }
        }
        // Generate as complete since we have maven module and ip address.
        return new ManagementVips(COMPLETE, vips);
    }
}
