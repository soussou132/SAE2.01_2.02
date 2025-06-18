package com.example.perso;

public class Animal {
    String type;
    Position pos;
    int vitesse;
    boolean fuite;

    public Animal(String type, Position pos) {
        this.type = type;
        this.pos = pos;
        this.vitesse = switch (type) {
            case "Mouton", "O" -> 2;
            case "Loup", "L" -> 3;
            default -> 2;
        };
        this.fuite = false;
    }

    public void setPosition(Position p) {
        this.pos = p;
    }

    public void activerFuite(boolean f) {
        this.fuite = f;
    }

    public boolean estEnFuite() {
        return fuite;
    }

    public Position getPosition() {
        return new Position(pos.ligne, pos.colonne);
    }
}
