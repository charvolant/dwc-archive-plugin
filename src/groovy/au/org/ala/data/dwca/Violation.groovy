package au.org.ala.data.dwca

/**
 * Information about a specific violation of a constraint.
 * <p>
 * A violation is a breach that will mean that the archive cannot be loaded.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class Violation extends Breach {
    /**
     * Is this a contstraint violation?
     *
     * @return True
     */
    boolean isViolation() {
        return true
    }

}
