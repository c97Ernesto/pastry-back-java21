package com.malva_pastry_shop.backend.domain.inventory;

/**
 * Unidades de medida para ingredientes de pasteleria.
 */
public enum UnitOfMeasure {

    // Peso
    GRAMO("g", "Gramo"),
    KILOGRAMO("kg", "Kilogramo"),
    MILIGRAMO("mg", "Miligramo"),
    LIBRA("lb", "Libra"),
    ONZA("oz", "Onza"),

    // Volumen
    MILILITRO("ml", "Mililitro"),
    LITRO("l", "Litro"),
    TAZA("tz", "Taza"),
    CUCHARADA("cda", "Cucharada"),
    CUCHARADITA("cdta", "Cucharadita"),

    // Unidades
    UNIDAD("u", "Unidad"),
    DOCENA("doc", "Docena"),
    PAQUETE("paq", "Paquete"),
    PIEZA("pza", "Pieza");

    private final String abbreviation;
    private final String displayName;

    UnitOfMeasure(String abbreviation, String displayName) {
        this.abbreviation = abbreviation;
        this.displayName = displayName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName + " (" + abbreviation + ")";
    }
}
