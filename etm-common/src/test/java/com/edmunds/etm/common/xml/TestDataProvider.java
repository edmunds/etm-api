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
package com.edmunds.etm.common.xml;

import com.edmunds.etm.common.api.UrlTokenType;
import com.edmunds.etm.common.thrift.ClientConfigDto;
import com.edmunds.etm.common.thrift.MavenModuleDto;
import com.edmunds.etm.common.thrift.UrlTokenCollectionDto;
import com.edmunds.etm.common.thrift.UrlTokenDto;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.commons.io.IOUtils;

/**
 * Etm configuration provider. Provides different variants of configuration. <p/> <p/> Copyright (C) 2010 Edmunds.com
 * <p/> <p/> Date: Mar 11, 2010
 *
 * @author Aliaksandr Savin
 */
public final class TestDataProvider {

    public static final MavenModuleDto DRR_APP = new MavenModuleDto("com.edmunds.drr", "drr", "1.1");
    public static final MavenModuleDto DRR_APP_NEW_VER = new MavenModuleDto("com.edmunds.drr", "drr", "1.2");
    public static final MavenModuleDto CRR_APP = new MavenModuleDto("com.edmunds.crr", "crr", "1.1");

    public static final String DRR_CONFIG_FILE = "/config/drr-config.xml";
    public static final String URL_TOKENS_FILE = "/config/url-tokens.xml";

    public static ClientConfigDto createNewClientConfig() {
        ConfigBuilder builder = new ConfigBuilder();
        builder.createMavenModule(DRR_APP).createUrlRule(DRR_APP, "/rule2/**");
        return builder.getConfig();
    }

    public static ClientConfigDto createNewConflictedClientConfig() {
        ConfigBuilder builder = new ConfigBuilder();
        builder.createMavenModule(DRR_APP).createUrlRule(DRR_APP, "rule");
        return builder.getConfig();
    }

    public static byte[] loadConfigurationFromFile(String fileName) {
        InputStream stream = TestDataProvider.class.getResourceAsStream(fileName);
        try {
            return IOUtils.toByteArray(stream);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ClientConfigDto createNewClientConfigWithSubsets() {
        ConfigBuilder builder = new ConfigBuilder();
        builder.createMavenModule(DRR_APP)
            .createUrlRule(DRR_APP, "rule*").createUrlRule(DRR_APP, "*ule").createUrlRule(DRR_APP, "*ul*")
            .createUrlRule(DRR_APP, "/alpha/*").createUrlRule(DRR_APP, "/alpha/*.txt");
        return builder.getConfig();
    }

    public static UrlTokenCollectionDto createNewUrlTokenCollection() {

        UrlTokenCollectionDto coll = new UrlTokenCollectionDto();
        UrlTokenDto token = new UrlTokenDto();
        token.setName("make");
        token.setType(UrlTokenType.FIXED.name());
        token.addToValues("ford");
        token.addToValues("honda");
        token.addToValues("toyota");
        coll.addToTokens(token);

        token = new UrlTokenDto();
        token.setName("year");
        token.setType("regex");
        token.addToValues("19|20\\d{2}");
        coll.addToTokens(token);

        return coll;
    }

    static class ConfigBuilder {
        private ClientConfigDto clientConfiguration;

        ConfigBuilder() {
            clientConfiguration = new ClientConfigDto();
            clientConfiguration.setUrlRules(new ArrayList<String>());
        }

        public ConfigBuilder createMavenModule(MavenModuleDto mavenModule) {
            clientConfiguration.setMavenModule(mavenModule);
            return this;
        }

        public ConfigBuilder createUrlRule(MavenModuleDto module, String rule) {
            if(clientConfiguration.getMavenModule().equals(module)) {

                clientConfiguration.getUrlRules().add(rule);
            }
            return this;
        }

        public ClientConfigDto getConfig() {
            return clientConfiguration;
        }
    }
}
