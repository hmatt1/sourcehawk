package com.optum.sourcehawk.enforcer.file.maven;

import com.optum.sourcehawk.enforcer.EnforcerResult;
import com.optum.sourcehawk.enforcer.file.maven.utils.MavenPomParser;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;

/**
 * An enforcer which enforces that the coordinates of the maven dependencies are as expected
 */
@AllArgsConstructor(staticName = "coordinates")
public class MavenDependencies extends AbstractMavenModelEnforcer {

    public static final String DEPENDENCY = "dependency";

    /**
     * The expected maven coordinates ID of expected dependencies.  Maven groupId and artifactId are required
     * and version is optional.  Coordinates should be separated by colon.
     * <p>
     * Examples:
     * <p>
     * - com.optum.sourcehawk:library
     * - com.optum.sourcehawk:library:1.0.0
     */
    @NonNull
    private final List<String> expectedCoordinates;

    /**
     * {@inheritDoc}
     */
    @Override
    protected EnforcerResult enforceInternal(@NonNull final InputStream actualFileInputStream) throws IOException {
        return MavenPomParser.parse(actualFileInputStream)
                .map(Model::getDependencies)
                .map(this::enforceDependencies)
                .orElseGet(() -> EnforcerResult.failed(PARSE_ERROR));
    }

    private EnforcerResult enforceDependencies(List<Dependency> dependencies) {
        if (expectedCoordinates.isEmpty()) {
            return EnforcerResult.passed();
        }
        if (dependencies == null || dependencies.isEmpty()) {
            return EnforcerResult.failed(String.format(MISSING_DECLARATION_ERROR, getMavenModelType()));
        }
        return expectedCoordinates.stream()
                .map(expectedCoordinate -> {
                    val expectedCoordinatesArray = expectedCoordinate.split(":");
                    if (expectedCoordinatesArray.length < 2) {
                        return EnforcerResult.failed(EXPECTED_FORMAT_ERROR);
                    }
                    return dependencies
                            .stream()
                            .filter(matchCoordinates(expectedCoordinatesArray))
                            .findFirst()
                            .map(this::buildModelFromDependency)
                            .map(s -> enforce(expectedCoordinatesArray, s))
                            .orElseGet(() -> EnforcerResult.failed(String.format(MISSING_DECLARATION_ERROR, expectedCoordinate)));

                })
                .reduce(EnforcerResult.passed(), EnforcerResult::reduce);
    }

    private Model buildModelFromDependency(Dependency dependency) {
        Model model = new Model();
        model.setArtifactId(dependency.getArtifactId());
        model.setGroupId(dependency.getGroupId());
        model.setVersion(dependency.getVersion());
        return model;
    }

    private Predicate<Dependency> matchCoordinates(String[] expectedCoordinatesArray) {
        return s -> s.getArtifactId().equalsIgnoreCase(expectedCoordinatesArray[1]) && s.getGroupId().equalsIgnoreCase(expectedCoordinatesArray[0]);
    }

    @Override
    protected String getMavenModelType() {
        return DEPENDENCY;
    }

    @Override
    protected String getArtifactId(Model model) {
        return model.getArtifactId();
    }

    @Override
    protected String getGroupId(Model model) {
        return model.getGroupId();
    }

    @Override
    protected String getVersion(Model model) {
        return model.getVersion();
    }

    @Override
    protected String getId(Model model) {
        return model.getId();
    }
}
