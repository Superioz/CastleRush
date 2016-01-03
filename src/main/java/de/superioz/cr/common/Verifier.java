package de.superioz.cr.common;

import de.superioz.library.java.util.SimpleStringUtils;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class Verifier {

    /**
     * Verifies an integer
     *
     * @param s          The raw string
     * @param defaultInt The default int
     *
     * @return The int
     */
    public static int verifyInteger(String s, int defaultInt){
        if(!SimpleStringUtils.isInteger(s)){
            return defaultInt;
        }
        return Integer.parseInt(s);
    }

}
