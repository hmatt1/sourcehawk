package com.optum.sourcehawk.enforcer.file.json;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.optum.sourcehawk.enforcer.EnforcerResult;
import com.optum.sourcehawk.enforcer.file.AbstractFileEnforcer;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * An enforcer implementation which enforces that the result of a JsonPath query equals a specific value
 *
 * @see JsonPath
 *
 * @author Brian Wyka
 */
@AllArgsConstructor(staticName = "equals")
public class JsonPathEquals extends AbstractFileEnforcer {

    private static final String PARSE_ERROR_TEMPLATE = "Query parsing resulted in error [%s]";
    private static final String QUERY_ERROR_TEMPLATE = "Execution of query [%s] yielded error [%s]";
    private static final String MISSING_MESSAGE_TEMPLATE = "Execution of query [%s] yielded no result";
    private static final String NOT_EQUAL_MESSAGE_TEMPLATE = "Execution of query [%s] yielded result [%s] which is not equal to [%s]";

    /**
     * Key: The JsonPath query to retrieve the value
     * @see JsonPath
     *
     * Value: The expected value which the query should evaluate to
     */
    private final Map<String, Object> expectations;

    /**
     * Create with a single path query and expected value
     *
     * @param jsonPathQuery the json path query
     * @param expectedValue the expected value
     * @return the enforcer
     */
    public static JsonPathEquals equals(final String jsonPathQuery, final Object expectedValue) {
        return JsonPathEquals.equals(Collections.singletonMap(jsonPathQuery, expectedValue));
    }

    /** {@inheritDoc} */
    @Override
    public EnforcerResult enforceInternal(@NonNull final InputStream actualFileInputStream) {
        final DocumentContext documentContext;
        try {
            documentContext = JsonPath.parse(actualFileInputStream);
        } catch (final Exception e) {
            return EnforcerResult.failed(String.format(PARSE_ERROR_TEMPLATE, e.getMessage()));
        }
        val messages = expectations.entrySet()
                .stream()
                .map(entry -> enforce(documentContext, entry.getKey(), entry.getValue()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        return EnforcerResult.create(messages);
    }

    /**
     * Enforce individual json path queries with expected value
     *
     * @param documentContext the documentation context
     * @param jsonPathQuery the json path query
     * @param expectedValue the expected value
     * @return The message to be added, otherwise {@link Optional#empty()}
     */
    private static Optional<String> enforce(final DocumentContext documentContext, final String jsonPathQuery, final Object expectedValue) {
        try {
            val actualValue = documentContext.read(JsonPath.compile(jsonPathQuery));
            if (Objects.equals(expectedValue, actualValue)) {
                return Optional.empty();
            }
            return Optional.of(String.format(NOT_EQUAL_MESSAGE_TEMPLATE, jsonPathQuery, actualValue, expectedValue));
        } catch (final PathNotFoundException e) {
            return Optional.of(String.format(MISSING_MESSAGE_TEMPLATE, jsonPathQuery));
        } catch (final Exception e) {
            return Optional.of(String.format(QUERY_ERROR_TEMPLATE, jsonPathQuery, e.getMessage()));
        }
    }

}
