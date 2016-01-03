package de.superioz.cr.common.stats;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public enum DatabaseType {

    SQLITE("sqlite"),
    MYSQL("mysql");

    String s;

    DatabaseType(String spe){
        s = spe;
    }

    public String getSpecifier(){
        return s;
    }

    public static DatabaseType from(String string){
        for(DatabaseType db : values()){
            if(db.getSpecifier().equalsIgnoreCase(string))
                return db;
        }
        return DatabaseType.SQLITE;
    }

}
