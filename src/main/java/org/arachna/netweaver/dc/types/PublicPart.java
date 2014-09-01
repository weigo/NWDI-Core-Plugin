/**
 *
 */
package org.arachna.netweaver.dc.types;

import java.util.Arrays;

/**
 * Public part of a development component.
 *
 * @author Dirk Weigenand
 */
public class PublicPart {
    /**
     * description of public part.
     */
    private String description = "";

    /**
     * name of public part.
     */
    private String publicPart = "";

    /**
     * caption of public part.
     */
    private String caption = "";

    /**
     * The type of this public part.
     */
    private PublicPartType type;

    /**
     * Create a new public part object.
     *
     * @param name
     *            name of public part.
     * @param caption
     *            caption of public part.
     * @param description
     *            description of public part.
     * @param type
     *            the type of this public part.
     */
    public PublicPart(final String name, final String caption, final String description, final PublicPartType type) {
        if (name == null) {
            throw new IllegalArgumentException("Name of public part must not be null!");
        }

        if (type == null) {
            throw new IllegalArgumentException("The type of this public part must not be null!");
        }

        publicPart = name;
        this.type = type;

        if (caption != null) {
            this.caption = caption;
        }

        if (description != null) {
            this.description = description;
        }
    }

    /**
     * Default constructor.
     */
    public PublicPart() {
        this("", "", "", PublicPartType.COMPILE);
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return the publicPart
     */
    public String getPublicPart() {
        return publicPart;
    }

    /**
     * @param publicPart
     *            the publicPart to set
     */
    public void setPublicPart(final String publicPart) {
        this.publicPart = publicPart;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("PublicPart [publicPart=%s, caption=%s, type=%s, description=%s]", publicPart, caption, type, description);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(new String[] { publicPart, type == null ? "" : type.toString() });
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final PublicPart other = (PublicPart)obj;

        return hashCode() == other.hashCode();
    }

    /**
     * Get short description of this public part.
     *
     * @return caption or short description of this public part.
     */
    public String getCaption() {
        return caption;
    }

    /**
     * @param caption
     *            caption or short description of this public part.
     */
    public void setCaption(final String caption) {
        this.caption = caption;
    }

    /**
     * Returns the type of this public part.
     *
     * @return the type of this public part
     */
    public PublicPartType getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(final PublicPartType type) {
        this.type = type;
    }
}
