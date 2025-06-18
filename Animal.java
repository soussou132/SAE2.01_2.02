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

}
