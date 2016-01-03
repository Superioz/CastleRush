package de.superioz.cr.common.settings;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public enum PluginLanguage {

    GERMAN("de"),
    ENGLISH("en");

    String lang;

    PluginLanguage(String lang){
        this.lang = lang;
    }

    public String getLanguage(){
        return lang;
    }

    /**
     * Gets language from given string
     * @param s The string
     * @return The language
     */
    public static PluginLanguage from(String s){
        for(PluginLanguage lang : values()){
            if(lang.getLanguage().equalsIgnoreCase(s))
                return lang;
        }
        return PluginLanguage.ENGLISH;
    }

}
