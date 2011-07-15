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

import com.edmunds.etm.common.thrift.MavenModuleDto;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Bean containing maven module properties.
 */
public class MavenModule implements Comparable<MavenModule>, Serializable {
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final int hashCode;

    /**
     * Explicit constructor calling out each of the properties.
     *
     * @param groupId    the group id
     * @param artifactId the artifact id
     * @param version    the maven version
     */
    public MavenModule(String groupId, String artifactId, String version) {
        Validate.notEmpty(groupId);
        Validate.notEmpty(artifactId);
        Validate.notEmpty(version);

        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;

        // Since all fields are final immutable calculate the hashCode now.
        this.hashCode = new HashCodeBuilder()
            .append(groupId)
            .append(artifactId)
            .append(version)
            .toHashCode();
    }

    /**
     * Returns the group id.
     *
     * @return the group id
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Returns the artifact id.
     *
     * @return the artifact id
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * Returns the version.
     *
     * @return the version.
     */
    public String getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MavenModule other = (MavenModule) o;

        return new EqualsBuilder()
            .append(groupId, other.groupId)
            .append(artifactId, other.artifactId)
            .append(version, other.version)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public int compareTo(MavenModule other) {
        if (other == null) {
            return 1;
        }

        return new CompareToBuilder()
            .append(groupId, other.groupId)
            .append(artifactId, other.artifactId)
            .append(version, other.version)
            .toComparison();
    }

    /**
     * Returns a string representation of the module.
     *
     * @return groupId + ":" + artifactId + ":" + version
     */
    @Override
    public String toString() {
        return groupId + ":" + artifactId + ":" + version;
    }

    /**
     * Creates a MavenModule from the given DTO.
     *
     * @param dto the DTO to read
     * @return a MavenModule object
     */
    public static MavenModule readDto(MavenModuleDto dto) {
        if (dto == null) {
            return null;
        }
        return new MavenModule(dto.getGroupId(), dto.getArtifactId(), dto.getVersion());
    }

    /**
     * Creates a DTO from the given MavenModule.
     *
     * @param value a MavenModule object
     * @return a data transfer object
     */
    public static MavenModuleDto writeDto(MavenModule value) {
        if (value == null) {
            return null;
        }
        return new MavenModuleDto(value.groupId, value.artifactId, value.version);
    }
}
