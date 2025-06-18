package com.example.perso;

public class Bordure {

    public static boolean estCoin(int l, int c, int nbL, int nbC) {
        return (l == 0 && c == 0) ||                //Coin haut-gauche
                (l == 0 && c == nbC - 1) ||         //Coin haut-droit
                (l == nbL - 1 && c == 0) ||         //Coin bat-gauche
                (l == nbL - 1 && c == nbC - 1);     //Coin bat-droit
    }

    public static boolean estSurBord(int l, int c, int nbL, int nbC) {
        return l == 0 ||            // Bord haut
                l == nbL - 1 ||     // Bord droit
                c == 0 ||           // Bord gauche
                c == nbC - 1;       // Bord bat
    }

}
