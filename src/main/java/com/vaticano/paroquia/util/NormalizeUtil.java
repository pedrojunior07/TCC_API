package com.vaticano.paroquia.util;

import java.text.Normalizer;

public class NormalizeUtil {

    /**
     * Normaliza um valor removendo espaços extras, BOMs, null, "NA", "undefined".
     * Retorna string vazia se o valor for inválido.
     */
    public static String normalizeValue(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        // Remove BOM (Byte Order Mark)
        value = value.replace("\uFEFF", "");

        // Trim espaços
        value = value.trim();

        // Verifica se é valor vazio semanticamente
        if (value.equalsIgnoreCase("NA") ||
            value.equalsIgnoreCase("N/A") ||
            value.equalsIgnoreCase("null") ||
            value.equalsIgnoreCase("undefined")) {
            return "";
        }

        return value;
    }

    /**
     * Normaliza para chave única: lowercase + remove acentos + remove não-alfanuméricos.
     * Usado para deduplicação de membros.
     */
    public static String normalizeForKey(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        // Remove acentos/diacríticos
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");

        // Lowercase
        normalized = normalized.toLowerCase();

        // Remove tudo que não seja alfanumérico
        normalized = normalized.replaceAll("[^a-z0-9]", "");

        return normalized;
    }

    /**
     * Remove múltiplos espaços consecutivos, deixando apenas um.
     */
    public static String normalizeSpaces(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return value.replaceAll("\\s+", " ").trim();
    }

    /**
     * Normaliza nome completo: capitaliza primeira letra de cada palavra.
     */
    public static String capitalizeName(String name) {
        if (name == null || name.isBlank()) {
            return "";
        }

        name = normalizeSpaces(name);
        String[] words = name.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1).toLowerCase());
                }
                result.append(" ");
            }
        }

        return result.toString().trim();
    }
}
