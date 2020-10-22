package com.optum.sourcehawk.enforcer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Enforcer constants
 *
 * @author Brian Wyka
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnforcerConstants {

    /**
     * The property name which type hints deserialization engine
     */
    public static final String DESERIALIZATION_TYPE_KEY = "enforcer";

}
