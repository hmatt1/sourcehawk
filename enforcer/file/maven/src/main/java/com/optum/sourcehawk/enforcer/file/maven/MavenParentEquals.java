package com.optum.sourcehawk.enforcer.file.maven;

import com.optum.sourcehawk.enforcer.EnforcerResult;
import com.optum.sourcehawk.enforcer.file.maven.utils.MavenPomParser;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;

import java.io.InputStream;
import java.util.Optional;

/**
 * An enforcer which enforces that the coordinates of the maven parent are as expected
 */
@AllArgsConstructor(staticName = "coordinates")
public class MavenParentEquals extends AbstractMavenModelEnforcer {

    public static final String PARENT = "parent";

    /**
     * The expected maven coordinates ID of the parent.  Maven groupId and artifactId are required
     * and version is optional.  Coordinates should be separated by colon.
     * <p>
     * Examples:
     * <p>
     * - com.optum.sourcehawk:parent
     * - com.optum.sourcehawk:parent:1.0.0
     */
    @NonNull
    private final String expectedCoordinates;


    /**
     * {@inheritDoc}
     */
    @Override
    protected EnforcerResult enforceInternal(@NonNull final InputStream actualFileInputStream) {
        val expectedCoordinatesArray = expectedCoordinates.split(":");
        if (expectedCoordinatesArray.length < 2) {
            return EnforcerResult.failed(EXPECTED_FORMAT_ERROR);
        }
        return MavenPomParser.parse(actualFileInputStream)
                .map(model -> getEnforcerResult(expectedCoordinatesArray, model))
                .orElseGet(() -> EnforcerResult.failed(PARSE_ERROR));
    }

    /**
     * Get the enforcer results and return failed if the expected coordinates are empty.
     *
     * @param expectedCoordinatesArray coordinates to use
     * @param model                    the maven model
     * @return Enforcer result
     */
    private EnforcerResult getEnforcerResult(String[] expectedCoordinatesArray, Model model) {
        if (model.getParent() == null) {
            return EnforcerResult.failed(String.format(MISSING_DECLARATION_ERROR, getMavenModelType()));
        }
        return enforce(expectedCoordinatesArray, model);
    }

    @Override
    protected String getMavenModelType() {
        return PARENT;
    }

    @Override
    protected String getArtifactId(Model model) {
        if (model == null) {
            return null;
        }
        return Optional.ofNullable(model.getParent())
                .map(Parent::getArtifactId)
                .orElse(null);
    }

    @Override
    protected String getGroupId(Model model) {
        if (model == null || model.getParent() == null) {
            return null;
        }
        return Optional.ofNullable(model.getParent())
                .map(Parent::getGroupId)
                .orElse(null);
    }

    @Override
    protected String getVersion(Model model) {
        if (model == null) {
            return null;
        }
        return Optional.ofNullable(model.getParent())
                .map(Parent::getVersion)
                .orElse(null);
    }

    @Override
    protected String getId(Model model) {
        if (model == null) {
            return null;
        }
        return Optional.ofNullable(model.getParent())
                .map(Parent::getId)
                .orElse(null);
    }
}
