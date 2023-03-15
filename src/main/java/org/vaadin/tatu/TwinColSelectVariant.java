package org.vaadin.tatu;

public enum TwinColSelectVariant {
    VERTICAL("vertical"), NO_BUTTONS("no-buttons");

    private final String variant;

    TwinColSelectVariant(String variant) {
        this.variant = variant;
    }

    /**
     * Gets the variant name.
     *
     * @return variant name
     */
    public String getVariantName() {
        return variant;
    }
}
