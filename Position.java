package com.example.perso;

public class Position {
    int ligne, colonne;

    Position(int l, int c) {
        ligne = l;
        colonne = c;
    }

    public boolean estValide(int maxL, int maxC) {
        return  0 <= ligne && ligne < maxL &&  0 <=colonne && colonne < maxC;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Position) {
            Position p = (Position) o;
            return ligne == p.ligne && colonne == p.colonne;
        }
        return false;
    }


    @Override
    public String toString() {
        return "(" + ligne + ", " + colonne + ")";
    }


}
