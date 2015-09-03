package au.org.ala.data.dwca

/**
 * A non-fatal breach of constraints
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class Caution extends Breach {
    /**
     * Is this a caution?
     *
     * @return True
     */
    boolean isCaution() {
        return true
    }

}
