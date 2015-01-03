package org.arachna.netweaver.dc.types;

import java.io.Serializable;
import java.util.Comparator;

/**
 * {@link java.util.Comparator} for {@link PublicPart}s by public part name.
 * 
 * @author Dirk Weigenand
 */
public final class PublicPartByNameComparator implements Comparator<PublicPart>, Serializable {
    /**
     * UID for serialization.
     */
    private static final long serialVersionUID = 4160842624880016549L;

    /**
     * Compares two {@link PublicPart}s by their respective public part names.
     * 
     * @param part1
     *            first public part to use in comparison.
     * @param part2
     *            second public part to use in comparison.
     * @return {@link StringComparatorResultMapper#mapResultToSpecificationValues(int)}
     *         .
     */
    public int compare(final PublicPart part1, final PublicPart part2) {
        return StringComparatorResultMapper.mapResultToSpecificationValues(part1.getPublicPart().compareTo(part2.getPublicPart()));
    }
}
