package com.optum.sourcehawk.core.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Global constants for all Sourcehawk modules
 *
 * @author Brian Wyka
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SourcehawkConstants {

    /**
     * The name
     */
    public static final String NAME = "Sourcehawk";

    /**
     * The name (lowercase)
     */
    public static final String NAME_LOWERCASE = "sourcehawk";

    /**
     * The default name of the configuration file in the repository
     */
    public static final String DEFAULT_CONFIG_FILE_NAME = "sourcehawk.yml";

}
