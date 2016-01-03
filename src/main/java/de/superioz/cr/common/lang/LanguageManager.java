package de.superioz.cr.common.lang;

import de.superioz.cr.main.CastleRush;
import de.superioz.library.java.file.properties.SuperProperties;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class LanguageManager {

    private static SuperProperties<String> properties_en;

    /**
     * Loads the properties
     */
    public static void load(){
        properties_en = new SuperProperties<>("strings", "language", CastleRush.getInstance().getDataFolder());
        properties_en.load(true);
    }

    public static String get(String property){
        return properties_en.get(property);
    }

}
