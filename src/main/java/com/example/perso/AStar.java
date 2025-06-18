package com.example.perso;

import java.util.*;

public class AStar {

    private static class Node {
        Node parent;
        Position pos;
        int g, h, f;


        public Node(Position p, int g, int h, Node parent) {
            this.pos = p;
            this.g = g;
            this.h = h;
            this.f = g + h;
            this.parent = parent;
        }
    }

    public static List<Position> aStarAlgo(Position pos, Position fin, boolean estMouton) {
        if (pos == null || fin == null) return new ArrayList<>();

        PriorityQueue<Node> lstOuverts = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        Set<Position> lstFermes = new HashSet<>();

        Node start = new Node(pos, 0, heuristique(pos, fin), null);
        lstOuverts.add(start);

        int[][] direction = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        while (!lstOuverts.isEmpty()) {
            Node current = lstOuverts.poll();

            if (current.pos.equals(fin)) return cheminFinal(current);

            lstFermes.add(current.pos);

            for (int[] dirs : direction) {
                int nl = current.pos.ligne + dirs[0];
                int nc = current.pos.colonne + dirs[1];
                Position nvP = new Position(nl, nc);

                if (!estPosiValid(nvP, estMouton) || lstFermes.contains(nvP)) continue;

                int coutG = current.g + 1;
                int coutH = heuristique(nvP, fin);



                Node exist = null;
                for (Node n : lstOuverts) {
                    if (n.pos.equals(nvP)) {
                        exist = n;
                        break;
                    }
                }

                if (exist == null) {
                    lstOuverts.add(new Node(nvP, coutG, coutH, current));
                } else if (coutG < exist.g) {
                    lstOuverts.remove(exist);
                    exist.g = coutG;
                    exist.f = coutG + coutH;
                    exist.parent = current;
                    lstOuverts.add(exist);
                }
            }
        }

        return new ArrayList<>();
    }

    private static List<Position> cheminFinal(Node fin) {
        List<Position> c = new ArrayList<>();
        while (fin.parent != null) {
            c.addFirst(fin.pos);
            fin = fin.parent;
        }
        return c;
    }

    private static boolean estPosiValid(Position pos, boolean mouton) {
        if (!pos.estValide(Outils.lignes, Outils.colonnes)) return false;

        char c = Outils.labyrinthe[pos.ligne][pos.colonne];
        if (c == 'R') return false;
        if (pos.equals(Outils.sortie)) return mouton;

        return true;
    }


    public static Position prochainePosition(Position a, Position b, boolean mouton) {
        if (a == null || b == null) return null;
        if (a.equals(b)) return null;

        List<Position> chemin = aStarAlgo(a, b, mouton);

        if (chemin.isEmpty()) return null;
        return chemin.getFirst();
    }

    public static int distanceManhattan(Position a, Position b) {
        if (a == null || b == null) return Integer.MAX_VALUE;
        return Math.abs(a.ligne - b.ligne) + Math.abs(a.colonne - b.colonne);
    }

    public static int heuristique(Position p, Position objectif) {
        if (p == null || objectif == null) return Integer.MAX_VALUE;
        return distanceManhattan(p, objectif);
    }
}
