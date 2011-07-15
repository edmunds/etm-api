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
package com.edmunds.etm.common.api;

import com.edmunds.etm.common.thrift.RuleSetDeploymentEventDto;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * A RuleSetDeploymentEvent describes the deployment of configuration data to a web server.
 *
 * @author Ryan Holmes
 */
public class RuleSetDeploymentEvent {

    private static Logger logger = Logger.getLogger(RuleSetDeploymentEvent.class);

    private final Date eventDate;
    private final String ruleSetDigest;
    private final RuleSetDeploymentResult result;

    public RuleSetDeploymentEvent(Date eventDate,
                                  String ruleSetDigest,
                                  RuleSetDeploymentResult result) {
        Validate.notNull(eventDate, "Event date is null");
        Validate.notEmpty(ruleSetDigest, "Rule set digest is empty");
        Validate.notNull(result, "Result is null");
        this.eventDate = eventDate;
        this.ruleSetDigest = ruleSetDigest;
        this.result = result;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public String getRuleSetDigest() {
        return ruleSetDigest;
    }

    public RuleSetDeploymentResult getResult() {
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RuleSetDeploymentEvent)) {
            return false;
        }

        RuleSetDeploymentEvent that = (RuleSetDeploymentEvent) o;

        if (!eventDate.equals(that.eventDate)) {
            return false;
        }
        if (result != that.result) {
            return false;
        }
        if (!ruleSetDigest.equals(that.ruleSetDigest)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result1 = eventDate.hashCode();
        result1 = 31 * result1 + ruleSetDigest.hashCode();
        result1 = 31 * result1 + result.hashCode();
        return result1;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("RuleSetDeploymentEvent");
        sb.append("{eventDate=").append(eventDate);
        sb.append(", ruleSetDigest='").append(ruleSetDigest).append('\'');
        sb.append(", result=").append(result);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Creates a RuleSetDeploymentEvent object from the specified DTO.
     *
     * @param dto the DTO to read
     * @return a RuleSetDeploymentEvent object
     */
    public static RuleSetDeploymentEvent readDto(RuleSetDeploymentEventDto dto) {
        if (dto == null) {
            return null;
        }

        Date eventDate = new Date(dto.getEventDate());
        String ruleSetDigest = dto.getRuleSetDigest();
        RuleSetDeploymentResult result;

        try {
            result = RuleSetDeploymentResult.valueOf(dto.getResult());
        } catch (RuntimeException e) {
            logger.error("Invalid RuleSetDeploymentResult read from DTO", e);
            result = RuleSetDeploymentResult.UNKNOWN;
        }

        RuleSetDeploymentEvent obj = null;
        try {
            obj = new RuleSetDeploymentEvent(eventDate, ruleSetDigest, result);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid RuleSetDeploymentEventDto", e);
        }

        return obj;
    }

    /**
     * Creates a DTO from the specified RuleSetDeploymentEvent object.
     *
     * @param obj the object to write
     * @return a data transfer object
     */
    public static RuleSetDeploymentEventDto writeDto(RuleSetDeploymentEvent obj) {
        if (obj == null) {
            return null;
        }

        long eventDate = obj.getEventDate().getTime();
        String ruleSetDigest = obj.getRuleSetDigest();
        String result = obj.getResult().name();
        return new RuleSetDeploymentEventDto(eventDate, ruleSetDigest, result);
    }
}
