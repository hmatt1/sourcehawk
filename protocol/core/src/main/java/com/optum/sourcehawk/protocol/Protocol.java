package com.optum.sourcehawk.protocol;


/**
 * Definition of a protocol
 *
 * @author Brian Wyka
 * @author Christian Oestreich
 */
@SuppressWarnings("unused")
public interface Protocol {

    /**
     * The name of the protocol
     */
    String getName();

    /**
     * An optional description for the protocol
     */
    String getDescription();

    /**
     * The protocol group, allowing for associating different protocols together
     */
    String getGroup();

    /**
     * Any tags that the protocol should be annotated with
     */
    String[] getTags();

    /**
     * Whether or not the protocol is required
     */
    boolean isRequired();

    /**
     * The severity of the protocol
     */
    String getSeverity();

}
