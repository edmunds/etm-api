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
package com.edmunds.etm.common.impl;

import com.edmunds.etm.common.api.ControllerPaths;
import com.edmunds.etm.common.api.UrlToken;
import com.edmunds.etm.common.thrift.UrlTokenCollectionDto;
import com.edmunds.etm.common.thrift.UrlTokenDto;
import com.edmunds.etm.common.xml.XmlMarshaller;
import com.edmunds.etm.common.xml.XmlValidationException;
import com.edmunds.etm.common.xml.XmlValidator;
import com.edmunds.zookeeper.connection.ZooKeeperConnection;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Provides direct access and allows modification of persisted {@link UrlToken} objects.
 *
 * @author Ryan Holmes
 */
@Component
public class UrlTokenRepository {

    private static final Logger logger = Logger.getLogger(UrlTokenRepository.class);

    private final ZooKeeperConnection connection;
    private final ControllerPaths controllerPaths;
    private final ObjectSerializer objectSerializer;

    @Autowired
    public UrlTokenRepository(ZooKeeperConnection connection,
                              ControllerPaths controllerPaths,
                              ObjectSerializer objectSerializer) {
        this.connection = connection;
        this.controllerPaths = controllerPaths;
        this.objectSerializer = objectSerializer;
    }

    /**
     * Gets the names of all defined tokens.
     *
     * @return list of token names
     */
    public List<String> getTokenNames() {
        List<String> tokenNames;
        try {
            tokenNames = connection.getChildren(controllerPaths.getUrlTokens(), null);
        } catch(KeeperException e) {
            logger.error("Error fetching URL token names", e);
            tokenNames = new ArrayList<String>();
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }

        return tokenNames;
    }

    /**
     * Gets the UrlToken with the specified name.
     *
     * @param name name of the token to fetch
     * @return the requested token or null if not found
     */
    public UrlToken getToken(String name) {
        byte[] data;
        try {
            data = connection.getData(controllerPaths.getUrlToken(name), null, null);
        } catch(KeeperException e) {
            if(e.code() == Code.NONODE) {
                return null;
            } else {
                logger.error(String.format("Error fetching URL token: %s", name), e);
                return null;
            }
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            UrlTokenDto dto = objectSerializer.readValue(data, UrlTokenDto.class);
            return UrlToken.readDto(dto);
        } catch(IOException e) {
            return null;
        }
    }

    /**
     * Creates the specified UrlToken.
     *
     * @param token the token to create
     * @throws TokenExistsException if the token already exists
     */
    public void createToken(UrlToken token) throws TokenExistsException {
        byte[] data;
        try {
            data = objectSerializer.writeValue(UrlToken.writeDto(token));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        String path = controllerPaths.getUrlToken(token.getName());
        try {
            connection.createPersistent(path, data);
        } catch(KeeperException e) {
            if(e.code() == Code.NODEEXISTS) {
                throw new TokenExistsException(e);
            } else {
                logger.error(String.format("Error creating URL token: %s", token.getName()), e);
                throw new RuntimeException(e);
            }
        } catch(InterruptedException e) {
            logger.error(String.format("Error creating URL token: %s", token.getName()), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the specified token.
     *
     * @param token the updated token
     * @throws TokenNotFoundException if the token was not found
     */
    public void updateToken(UrlToken token) throws TokenNotFoundException {
        byte[] data;
        try {
            data = objectSerializer.writeValue(UrlToken.writeDto(token));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        String path = controllerPaths.getUrlToken(token.getName());
        try {
            connection.setData(path, data, -1);
        } catch(KeeperException e) {
            if(e.code() == Code.NONODE) {
                throw new TokenNotFoundException(e);
            } else {
                logger.error(String.format("Error updating URL token: %s", token.getName()), e);
                throw new RuntimeException(e);
            }
        } catch(InterruptedException e) {
            logger.error(String.format("Error updating URL token: %s", token.getName()), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes the specified token.
     *
     * @param name name of the token to delete
     * @throws TokenNotFoundException if the token was not found
     */
    public void deleteToken(String name) throws TokenNotFoundException {
        String path = controllerPaths.getUrlToken(name);
        try {
            connection.delete(path, -1);
        } catch(KeeperException e) {
            if(e.code() == Code.NONODE) {
                throw new TokenNotFoundException(e);
            } else {
                logger.error(String.format("Error deleting URL token: %s", name), e);
                throw new RuntimeException(e);
            }
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes all persisted tokens.
     */
    public void deleteAllTokens() {
        List<String> tokenNames = getTokenNames();
        for(String name : tokenNames) {
            try {
                deleteToken(name);
            } catch(TokenNotFoundException e) {
                logger.warn(String.format("Token not found: %s", name), e);
            }
        }
    }

    /**
     * Loads URL tokens definitions from the specified XML file, optionally replacing the existing tokens.
     *
     * @param file an XML file containing token definitions
     * @param replace true to replace existing tokens, false to merge with existing
     * @thows IOException if an error occurred while reading the default tokens
     */
    public void loadTokensFromFile(File file, boolean replace) throws IOException {

        List<UrlToken> tokens = readTokensFromFile(file);

        // Delete existing tokens
        if(replace) {
            deleteAllTokens();
        }

        // Add new tokens
        for(UrlToken token : tokens) {
            try {
                createToken(token);
            } catch(TokenExistsException e) {
                logger.warn(String.format("Token already exists: %s", token.getName()), e);
            }
        }
    }

    /**
     * Reads a list of tokens from the specified XML file.
     *
     * @param file an XML file containing token definitions
     * @return list of URL tokens
     * @throws IOException if an error occurs while reading the XML
     */
    public List<UrlToken> readTokensFromFile(File file) throws IOException {
        byte[] xmlData = FileUtils.readFileToByteArray(file);

        List<UrlToken> tokens;
        try {
            tokens = unmarshalTokensFromXml(xmlData);
        } catch(XmlValidationException e) {
            String message = "Invalid default URL token XML file";
            logger.error(message, e);
            throw new IOException(message, e);
        } catch(RuntimeException e) {
            String message = "Error loading default tokens";
            logger.error(message, e);
            throw new IOException(message, e);
        }

        return tokens;
    }

    /**
     * Returns a list of tokens unmarshalled from the given XML data.
     *
     * @param xmlData an XML document containing UrlToken definitions
     * @return list of UrlToken objects
     * @throws XmlValidationException if the XML data is invalid
     */
    public List<UrlToken> unmarshalTokensFromXml(byte[] xmlData) throws XmlValidationException {

        // Validate XML
        XmlValidator xmlValidator = new XmlValidator();
        xmlValidator.validate(xmlData, XmlValidator.URL_TOKENS_XSD);

        // Unmarshall XML data and convert to UrlToken objects
        UrlTokenCollectionDto tokenCollection = XmlMarshaller.unmarshal(xmlData, UrlTokenCollectionDto.class);
        List<UrlTokenDto> tokenDtos = tokenCollection.getTokens();
        ArrayList<UrlToken> tokens = Lists.newArrayListWithCapacity(tokenDtos.size());
        for(UrlTokenDto dto : tokenCollection.getTokens()) {
            tokens.add(UrlToken.readDto(dto));
        }

        return tokens;
    }

}
