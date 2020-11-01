package com.optum.sourcehawk.enforcer.file.maven;

import com.optum.sourcehawk.enforcer.EnforcerResult;
import com.optum.sourcehawk.enforcer.file.AbstractFileEnforcer;
import lombok.val;
import org.apache.maven.model.Model;

import java.util.ArrayList;
import java.util.Optional;

public abstract class AbstractMavenModelEnforcer extends AbstractFileEnforcer {

    protected static final String WILDCARD = "*";
    protected static final String EXPECTED_FORMAT_ERROR = "The expectedCoordinates is improperly formatted, should be in format groupId:artifactId[:version]";
    protected static final String PARSE_ERROR = "Maven pom.xml parsing resulted in error";
    protected static final String MISSING_DECLARATION_ERROR = "Maven pom.xml is missing <%s> declaration";
    protected static final String INCORRECT_GROUP_ID_ERROR = "Maven <%s> groupId [%s] does not equal [%s]";
    protected static final String INCORRECT_ARTIFACT_ID_ERROR = "Maven <%s> artifactId [%s] does not equal [%s]";
    protected static final String INCORRECT_VERSION_ERROR = "Maven <%s> [%s] version [%s] does not equal or match [%s]";

    /**
     * Get the model type to validation; parent, dependency, etc
     *
     * @return Name of the model type
     */
    protected abstract String getMavenModelType();

    /**
     * Get the artifact id for the model.
     *
     * @param model The maven model
     * @return Name of the artifact
     */
    protected String getArtifactId(final Model model) {
        return Optional.ofNullable(model)
                .map(Model::getArtifactId)
                .orElse(null);
    }

    /**
     * Get the group id for the model.
     *
     * @param model The maven model
     * @return Name of the group
     */
    protected String getGroupId(final Model model) {
        return Optional.ofNullable(model)
                .map(Model::getGroupId)
                .orElse(null);
    }

    /**
     * Get the version for the model.
     *
     * @param model The maven model
     * @return Version of the artifact
     */
    protected String getVersion(final Model model) {
        return Optional.ofNullable(model)
                .map(Model::getVersion)
                .orElse(null);
    }

    /**
     * Get the ID of the model
     *
     * @param model the model to calculate the id for
     * @return the model id as <code>groupId:artifactId:packaging:version</code>
     */
    protected String getId(final Model model) {
        return Optional.ofNullable(model)
                .map(Model::getId)
                .orElse(null);
    }

    /**
     * Enforce the model is as expected
     *
     * @param expectedCoordinatesArray the array of expected coordinates
     * @param model the maven model
     * @return the enforcer result
     */
    protected EnforcerResult enforce(final String[] expectedCoordinatesArray, final Model model) {
        val expectedGroupId = expectedCoordinatesArray[0];
        val expectedArtifactId = expectedCoordinatesArray[1];
        Optional<String> expectedVersionOptional = Optional.of(expectedCoordinatesArray)
                .filter(coordinates -> coordinates.length == 3)
                .map(coordinates -> coordinates[2]);
        val failedMessages = new ArrayList<String>();
        if (!WILDCARD.equals(expectedGroupId) && !expectedGroupId.equals(getGroupId(model))) {
            failedMessages.add(String.format(INCORRECT_GROUP_ID_ERROR, getMavenModelType(), getGroupId(model), expectedGroupId));
        }
        if (!WILDCARD.equals(expectedArtifactId) && !expectedArtifactId.equals(getArtifactId(model))) {
            failedMessages.add(String.format(INCORRECT_ARTIFACT_ID_ERROR, getMavenModelType(), getArtifactId(model), expectedArtifactId));
        }
        if (expectedVersionOptional.isPresent()) {
            val expectedVersion = expectedVersionOptional.get();
            if (!WILDCARD.equals(expectedVersion) && !(expectedVersion.equals(getVersion(model)) || getVersion(model).matches(expectedVersion))) {
                failedMessages.add(String.format(INCORRECT_VERSION_ERROR, getMavenModelType(), getId(model), getVersion(model), expectedVersionOptional.get()));
            }
        }
        if (failedMessages.isEmpty()) {
            return EnforcerResult.passed();
        }
        return EnforcerResult.builder()
                .passed(false)
                .messages(failedMessages)
                .build();
    }

}
