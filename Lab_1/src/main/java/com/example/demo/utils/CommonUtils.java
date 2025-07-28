package com.example.demo.utils;

public class CommonUtils {

    /**
     * Checks if the input object is null.
     * @param obj the input object
     * @return true if null, false otherwise
     */
    public static boolean isNull(Object obj) {
        return obj == null;
    }
    
    /**
     * Checks if a string is null or empty after trimming.
     * @param str the input string
     * @return true if null or empty, false otherwise
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Checks if the input string is a valid number (integer or decimal).
     * @param str the input string
     * @return true if the string is a number, false otherwise
     */
    public static boolean isNumber(String str) {
        if (isEmpty(str)) return false;
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if the input string contains only alphabetic characters (A-Z, a-z).
     * @param str the input string
     * @return true if only alphabets, false otherwise
     */
    public static boolean isAlphabetOnly(String str) {
        return str != null && str.matches("[a-zA-Z]+");
    }

}
