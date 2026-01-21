package com.malva_pastry_shop.backend.util;

import java.text.Normalizer;

/**
 * Utilidad para generar slugs URL-friendly a partir de strings.
 * Normaliza texto eliminando acentos, convirtiendo a minúsculas y
 * reemplazando espacios por guiones.
 */
public class SlugUtil {

    /**
     * Genera un slug a partir de un string.
     * 
     * Proceso:
     * 1. Normaliza el texto para eliminar acentos (NFD)
     * 2. Elimina marcas diacríticas
     * 3. Convierte a minúsculas
     * 4. Reemplaza espacios y caracteres especiales por guiones
     * 5. Elimina guiones duplicados
     * 6. Elimina guiones al inicio y final
     * 
     * @param text Texto a convertir en slug
     * @return Slug generado, o null si el texto es null o vacío
     */
    public static String generateSlug(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        // Normalizar para separar caracteres base de diacríticos
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);

        // Eliminar marcas diacríticas (acentos)
        String withoutAccents = normalized.replaceAll("\\p{M}", "");

        // Convertir a minúsculas
        String lowercase = withoutAccents.toLowerCase();

        // Reemplazar espacios y caracteres no alfanuméricos por guiones
        String slug = lowercase.replaceAll("[^a-z0-9]+", "-");

        // Eliminar guiones al inicio y final
        slug = slug.replaceAll("^-+|-+$", "");

        return slug;
    }

    /**
     * Genera un slug único agregando un sufijo numérico si es necesario.
     * 
     * @param baseSlug Slug base
     * @param counter  Contador para sufijo (usar 1 para el primer intento)
     * @return Slug con sufijo si counter > 1, o slug base si counter == 1
     */
    public static String generateUniqueSlug(String baseSlug, int counter) {
        if (counter <= 1) {
            return baseSlug;
        }
        return baseSlug + "-" + counter;
    }
}
