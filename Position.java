package com.example.perso;
import java.util.Objects;

public class Position {
    int ligne, colonne;

    Position(int l, int c) {
        ligne = l;
        colonne = c;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Position)) return false;
        Position p = (Position) obj;
        return ligne == p.ligne && colonne == p.colonne;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ligne, colonne);
    }
}