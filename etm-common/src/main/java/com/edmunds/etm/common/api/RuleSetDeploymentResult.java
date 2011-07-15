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

/**
 * Result of deploying a configuration to a web server.
 *
 * @author Ryan Holmes
 */
public enum RuleSetDeploymentResult {
    /**
     * Rule set was deployed successfully or did not change.
     */
    OK,

    /**
     * Syntax check failed, rule set rolled back.
     */
    SYNTAX_CHECK_FAILED,

    /**
     * Restart command failed, rule set rolled back.
     */
    RESTART_COMMAND_FAILED,

    /**
     * Health check failed, rule set rolled back.
     */
    HEALTH_CHECK_FAILED,

    /**
     * Rule set rollback failed, web server is unresponsive.
     */
    ROLLBACK_FAILED,

    /**
     * Deployment result unknown.
     */
    UNKNOWN
}
