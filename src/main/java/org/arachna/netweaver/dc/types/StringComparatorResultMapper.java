/**
 *
 */
package org.arachna.netweaver.dc.types;


/**
 * Mapper for return values of {@link String#compareTo(String)} to those
 * expected from {@link java.util.Comparator#compare(Object, Object)}.
 * 
 * @author Dirk Weigenand
 */
public final class StringComparatorResultMapper {
    /**
     * Should not be instantiated.
     */
    private StringComparatorResultMapper() {
    }

    /**
     * Map values returned from the {@link java.util.String#compareTo(String)}
     * method to those expected to be returned from the method
     * {@link java.util.Comparator#compare(Object, Object)}.
     * 
     * @param result
     *            value returned from a string comparison using the
     *            <code>compareTo</code> method.
     * @return the given value mapped to <code>-1, 0, 1</code> depending on the
     *         given value being smaller than, equal to or greater than
     *         <code>0</code>.
     */
    public static int mapResultToSpecificationValues(final int result) {
        return result < 0 ? -1 : (result > 0 ? 1 : 0);
    }
}
