package com.example.perso;

public class Animal {
    Position pos;
    int vitesse;
    String type;
    boolean fuite;

    
    Animal(String type, Position pos) {
        this.type = type;
        this.pos = pos;
        this.vitesse = type.equals("Mouton") ? 2 : 3;
        this.fuite = false;
    }
}
