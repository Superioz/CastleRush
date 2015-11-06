package de.superioz.cr.common.game.division;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public enum GamePhase {

    WAIT("waiting"),
    BUILD("build"),
    CAPTURE("capture"),
    END("end"),
    UNDEFINED("undefined");

    String n;

    GamePhase(String name){
        this.n = name;
    }

    public String getSpecifier(){
        return n.toUpperCase();
    }

}
