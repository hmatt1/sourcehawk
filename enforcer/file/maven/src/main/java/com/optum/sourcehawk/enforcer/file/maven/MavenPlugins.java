package com.optum.sourcehawk.enforcer.file.maven;

import com.optum.sourcehawk.enforcer.EnforcerResult;
import com.optum.sourcehawk.enforcer.file.maven.utils.MavenPomParser;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;

/**
 * An enforcer which enforces that the coordinates of the maven plugins are as expected
 */
@AllArgsConstructor(staticName = "coordinates")
public class MavenPlugins extends AbstractMavenModelEnforcer {

    public static final String PLUGIN = "plugin";

    /**
     * The expected maven coordinates ID of expected plugins.  Maven groupId and artifactId are required
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
                .map(Model::getBuild)
                .map(Build::getPlugins)
                .map(this::enforcePlugins)
                .orElseGet(() -> EnforcerResult.failed(PARSE_ERROR));
    }

    private EnforcerResult enforcePlugins(List<Plugin> plugins) {
        if (expectedCoordinates.isEmpty()) {
            return EnforcerResult.passed();
        }
        if (plugins == null || plugins.isEmpty()) {
            return EnforcerResult.failed(String.format(MISSING_DECLARATION_ERROR, getMavenModelType()));
        }
        return expectedCoordinates.stream()
                .map(expectedCoordinate -> {
                    val expectedCoordinatesArray = expectedCoordinate.split(":");
                    if (expectedCoordinatesArray.length < 2) {
                        return EnforcerResult.failed(EXPECTED_FORMAT_ERROR);
                    }
                    return plugins
                            .stream()
                            .filter(matchCoordinates(expectedCoordinatesArray))
                            .findFirst()
                            .map(this::buildModelFromPlugin)
                            .map(s -> enforce(expectedCoordinatesArray, s))
                            .orElseGet(() -> EnforcerResult.failed(String.format(MISSING_DECLARATION_ERROR, expectedCoordinate)));

                })
                .reduce(EnforcerResult.passed(), EnforcerResult::reduce);
    }

    private Model buildModelFromPlugin(Plugin plugin) {
        Model model = new Model();
        model.setArtifactId(plugin.getArtifactId());
        model.setGroupId(plugin.getGroupId());
        model.setVersion(plugin.getVersion());
        return model;
    }

    private Predicate<Plugin> matchCoordinates(String[] expectedCoordinatesArray) {
        return (Plugin s) -> s.getArtifactId().equalsIgnoreCase(expectedCoordinatesArray[1])
                && s.getGroupId().equalsIgnoreCase(expectedCoordinatesArray[0]);
    }

    @Override
    protected String getMavenModelType() {
        return PLUGIN;
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
