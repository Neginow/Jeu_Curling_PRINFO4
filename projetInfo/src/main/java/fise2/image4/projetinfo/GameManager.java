// Importation des bibliothèques nécessaires
package fise2.image4.projetinfo;

import static java.lang.Integer.max;
import org.opencv.core.Point; // Gestion des points en 2D
import java.util.ArrayList; // Liste dynamique non synchronisée
import java.util.Collections; // Outils pour synchronisation et manipulation de collections
import java.util.List; // Interface pour les listes
import java.util.Random; // Générateur de nombres aléatoires
import javafx.fxml.FXML; // Annotation pour les contrôleurs JavaFX
import org.opencv.core.Mat; // Gestion des matrices pour OpenCV (images et données)

public class GameManager {

    // Variables pour suivre les scores des deux équipes (Rouge et Bleu)
    public int scoreGameRED;
    public int scoreGameBLUE;
    // Variable utilisée pour un score intermédiaire (logique spécifique non précisée)
    public int avLive;
    private Point launchArea; // Coordonnées de la zone de lancement initiale
    public int turnsRemainingRED = 8; // Tours restants pour l'équipe rouge (initialisé à 8)
    public int turnsRemainingBLUE = 8; // Tours restants pour l'équipe bleue (initialisé à 8)
    private int totalTurns = 16; // Total des tours jouables (rouge + bleu)

    // Coordonnées de la cible à atteindre dans le jeu
    private Point target;
    // Callback à exécuter en cas de mise à jour des scores
    private Runnable onScoreUpdate;

    // Listes synchronisées pour gérer les cercles du jeu et leurs propriétés
    private List<Point> centers; // Centres des cercles
    private List<Integer> radius; // Rayons des cercles
    private List<Boolean> teamid; // Identifie si le cercle appartient à l'équipe rouge (true) ou bleue (false)
    private int turnCounts; // Compteur pour le nombre total de tours effectués
    private List<Point> LaunchAreaPositions; // Positions possibles dans la zone de lancement

    // Constructeur par défaut
    public GameManager() {
        this.launchArea = new Point(0, 0); // La zone de lancement est initialisée à (0, 0)
        this.target = new Point(0, 0); // La cible est initialisée à (0, 0)
        // Création de listes synchronisées pour éviter les conflits d'accès en multithreading
        this.centers = Collections.synchronizedList(new ArrayList<>());
        this.radius = Collections.synchronizedList(new ArrayList<>());
        this.teamid = Collections.synchronizedList(new ArrayList<>());
        this.scoreGameRED = 0; // Score initial pour l'équipe rouge
        this.scoreGameBLUE = 0; // Score initial pour l'équipe bleue
        turnCounts = 0; // Initialisation du compteur de tours
    }

    // Constructeur avec une zone de lancement spécifiée
    public GameManager(Point initialLaunchArea) {
        this.target = new Point(100, 100); // Définit une cible par défaut à (100, 100)
        this.launchArea = initialLaunchArea; // Initialise la zone de lancement
        // Initialisation des listes synchronisées
        this.centers = Collections.synchronizedList(new ArrayList<>());
        this.radius = Collections.synchronizedList(new ArrayList<>());
        this.teamid = Collections.synchronizedList(new ArrayList<>());
        this.scoreGameRED = 0;
        this.scoreGameBLUE = 0;
        turnCounts = 0;
    }

    // Constructeur prenant une zone de lancement, une image (Mat), et un nombre de tours
    public GameManager(Point initialLaunchArea, Mat frame, int nbTurns) {
        this.launchArea = initialLaunchArea;
        this.target = generateTarget(frame); // Génère une cible basée sur l'image fournie
        this.centers = Collections.synchronizedList(new ArrayList<>());
        this.radius = Collections.synchronizedList(new ArrayList<>());
        this.teamid = Collections.synchronizedList(new ArrayList<>());
        this.scoreGameRED = 0;
        this.scoreGameBLUE = 0;
        this.turnsRemainingBLUE = nbTurns; // Initialise les tours restants pour l'équipe bleue
        this.turnsRemainingRED = nbTurns; // Initialise les tours restants pour l'équipe rouge
        this.totalTurns = 2 * nbTurns; // Total de tours = 2 * nbTurns
        turnCounts = 0;
    }

    // Réinitialise le jeu avec un nombre de tours défini
    public void resetGame(int nbTurns) {
        // Vide et recrée les listes synchronisées
        this.centers = Collections.synchronizedList(new ArrayList<>());
        this.radius = Collections.synchronizedList(new ArrayList<>());
        this.teamid = Collections.synchronizedList(new ArrayList<>());
        this.scoreGameRED = 0;
        this.scoreGameBLUE = 0;
        this.turnsRemainingBLUE = nbTurns;
        this.turnsRemainingRED = nbTurns;
        this.totalTurns = 2 * nbTurns;
        turnCounts = 0; // Réinitialise le compteur de tours
    }

    // Décrémente le nombre de tours restants pour une équipe
    public void setTurnsRemaining(boolean isRedTeam) {
        if (isRedTeam) turnsRemainingRED = turnsRemainingRED - 1;
        else turnsRemainingBLUE = turnsRemainingBLUE - 1;
    }

    // Définit explicitement les tours restants pour l'équipe rouge
    public void setTurnsRemainingRED(int v) {
        turnsRemainingRED = v;
    }

    // Définit explicitement les tours restants pour l'équipe bleue
    public void setTurnsRemainingBLUE(int v) {
        turnsRemainingBLUE = v;
    }

    // Retourne le nombre de tours restants pour l'équipe rouge
    public int getTurnsRemainingRED() {
        return turnsRemainingRED;
    }

    // Retourne le nombre de tours restants pour l'équipe bleue
    public int getTurnsRemainingBLUE() {
        return turnsRemainingBLUE;
    }

    // Définit un callback à exécuter lors d'une mise à jour du score
    public void setOnScoreUpdate(Runnable callback) {
        this.onScoreUpdate = callback;
    }

    // Vérifie si un cercle chevauche un autre cercle existant
    private int circlesOverlapping(Point center, Integer radius) {
        for (int i = 0; i < centers.size(); i++) {
            if (twoCirclesOverlapping(center, radius, this.centers.get(i), this.radius.get(i))) {
                return i; // Retourne l'indice du premier cercle en chevauchement
            }
        }
        return -1; // Aucun chevauchement détecté
    }

    // Vérifie si deux cercles spécifiques se chevauchent
    private boolean twoCirclesOverlapping(Point newCenter, Integer newRadius, Point center, Integer radius) {
        if (newRadius == 0 || radius == 0) return false; // Si l'un des rayons est nul, il n'y a pas de chevauchement

        double dist = Math.sqrt(Math.pow(newCenter.x - center.x, 2) + Math.pow(newCenter.y - center.y, 2));
        if (dist >= newRadius + radius) return false; // Pas de chevauchement si la distance est supérieure à la somme des rayons

        // Calcul de la surface d'intersection entre deux cercles
        double A_newCircle = Math.PI * Math.pow(newRadius, 2);
        double t1 = Math.acos((Math.pow(dist, 2) + Math.pow(newRadius, 2) - Math.pow(radius, 2)) / (2 * dist * newRadius));
        double t2 = Math.acos((Math.pow(dist, 2) + Math.pow(radius, 2) - Math.pow(newRadius, 2)) / (2 * dist * radius));
        double t3 = (radius + newRadius - dist) * (dist + newRadius - radius) * (dist - newRadius + radius) * (dist + newRadius + radius);
        t3 = Math.sqrt(t3) / 2;

        double A_intersection = (Math.pow(newRadius, 2) * t1) + (Math.pow(radius, 2) * t2) - t3;
        return A_intersection > 0.05 * A_newCircle; // Seuil de chevauchement significatif
    }

    // Calcule la distance entre deux points
    private Double dist(Point A, Point B) {
        return Math.sqrt(Math.pow(A.x - B.x, 2) + Math.pow(A.y - B.y, 2));
    }

    // Supprime un cercle de la liste à l'indice spécifié
    public void removeCircle(int i) {
        centers.remove(i);
        radius.remove(i);
        teamid.remove(i);
    }

    // Ajout d'un cercle
    public void addCircle(Point newCenter, Integer newRadius) {
        int v = circlesOverlapping(newCenter, newRadius); // Vérifie si le nouveau cercle chevauche un cercle existant
        if (v >= 0) {
            removeCircle(v); // Supprime le cercle chevauché s'il y en a un
        }
        centers.add(newCenter); // Ajoute le centre du nouveau cercle
        radius.add(newRadius); // Ajoute le rayon du nouveau cercle
        teamid.add(turnCounts % 2 == 0); // Associe l'équipe au cercle (équipe bleue si le compteur est pair, rouge sinon)
        winnerLive(); // Met à jour l'état du score en direct
        turnCounts++; // Incrémente le compteur de tours
    }

    private Point generateTarget(Mat frame) {
        int width = frame.cols(); // Largeur de l'image
        int height = frame.rows(); // Hauteur de l'image
        Random random = new Random(); // Générateur de nombres aléatoires

        // Définir les coordonnées moyennes (centre de la cible) en fonction de la zone de lancement
        double meanX;
        double meanY;

        if (this.launchArea.x > width / 2) { // Si la zone de lancement est à droite de l'image
            meanX = width / 4.0; // La cible est orientée à gauche
            if (this.launchArea.y > height / 2) { // Si la zone de lancement est en bas
                meanY = height / 4.0; // La cible est en haut à gauche
            } else {
                meanY = 3 * height / 4.0; // La cible est en bas à gauche
            }
        } else { // Si la zone de lancement est à gauche de l'image
            meanX = 3 * width / 4.0; // La cible est orientée à droite
            if (this.launchArea.y > height / 2) { // Si la zone de lancement est en bas
                meanY = height / 4.0; // La cible est en haut à droite
            } else {
                meanY = 3 * height / 4.0; // La cible est en bas à droite
            }
        }

        // Définir l'écart-type pour générer des coordonnées autour des moyennes
        double stdDevX = width / 10.0; // Écart-type horizontal
        double stdDevY = height / 10.0; // Écart-type vertical

        // Génère des coordonnées aléatoires selon une distribution gaussienne
        double x = Math.max(0, Math.min(width - 1, random.nextGaussian() * stdDevX + meanX));
        double y = Math.max(0, Math.min(height - 1, random.nextGaussian() * stdDevY + meanY));

        return new Point(x, y); // Retourne la cible générée
    }

    public boolean getWinner() {
        if (centers.isEmpty()) {
            throw new IllegalStateException("Normalement impossible de voir ce message"); // Erreur si aucune cible n'existe
        }
        int minIndex = 0; // Index du cercle le plus proche de la cible
        double minDistance = 1000000; // Distance minimale initialisée à une valeur élevée
        for (int i = 1; i < centers.size(); i++) {
            double distance = dist(centers.get(i), target); // Calcule la distance au cercle
            if (distance < minDistance) {
                minDistance = distance; // Met à jour la distance minimale
                minIndex = i; // Met à jour l'index du cercle le plus proche
            }
        }
        return teamid.get(minIndex); // Retourne l'équipe (true pour rouge, false pour bleu) du cercle gagnant
    }

    // Vérifie si un cercle est dans la zone de lancement (zone carrée autour du point de lancement)
    public boolean isCircleInLaunchArea(Circle token) {
        if (token != null) {
            Point center = token.getCenter();
            int radius = token.getRadius();
            return ((center.x + radius) <= (launchArea.x + 100))
                    && ((center.x - radius) >= (launchArea.x - 100))
                    && ((center.y + radius) <= (launchArea.y + 100))
                    && ((center.y - radius) >= (launchArea.y - 100));
        } else return false; // Retourne faux si le cercle est nul
    }

    // Vérifie si un cercle est dans une zone de lancement personnalisée
    public boolean isCircleInLaunchArea(Circle token, List<Point> launchAreaPositions) {
        if (token != null && !launchAreaPositions.isEmpty()) {
            Point center = token.getCenter();
            Point point1 = launchAreaPositions.get(0);
            Point point2 = launchAreaPositions.get(1);
            double width = (Math.max(point1.x, point2.x) - Math.min(point1.x, point2.x));
            double height = (Math.max(point1.y, point2.y) - Math.min(point1.y, point2.y));
            double x = Math.min(point1.x, point2.x);
            double y = Math.min(point1.y, point2.y);
            int radius = token.getRadius();
            return ((center.x + radius) <= (x + width))
                    && ((center.x - radius) >= x)
                    && ((center.y + radius) <= (y + height))
                    && ((center.y - radius) >= y);
        } else return false;
    }

    // Vérifie si un cercle est en dehors de la zone de lancement personnalisée
    public boolean isCircleOutLaunchArea(Circle token, List<Point> launchAreaPositions) {
        if (token != null && !launchAreaPositions.isEmpty()) {
            Point center = token.getCenter();
            Point point1 = launchAreaPositions.get(0);
            Point point2 = launchAreaPositions.get(1);
            double width = (Math.max(point1.x, point2.x) - Math.min(point1.x, point2.x));
            double height = (Math.max(point1.y, point2.y) - Math.min(point1.y, point2.y));
            double x = Math.min(point1.x, point2.x);
            double y = Math.min(point1.y, point2.y);
            int radius = token.getRadius();
            return ((center.x + radius) <= x)
                    || ((center.x - radius) >= x + width)
                    || ((center.y + radius) <= y)
                    || ((center.y - radius) >= y + height);
        } else return false;
    }

    // Met à jour le score en temps réel
    public void winnerLive() {
        if (centers.isEmpty()) {
            throw new IllegalStateException("Normalement impossible de voir ce message"); // Erreur si aucun cercle n'est présent
        }
        List<Integer> indices = new ArrayList<>(); // Liste des indices des cercles
        for (int i = 0; i < centers.size(); i++) {
            indices.add(i);
        }
        indices.sort((i1, i2) -> Double.compare(
                dist(centers.get(i1), target),
                dist(centers.get(i2), target)
        )); // Trie les cercles par distance croissante à la cible
        int c = 1; // Compteur de cercles appartenant à la même équipe
        boolean currentTeam = teamid.get(indices.get(0)); // Équipe du cercle le plus proche

        for (int i = 1; i < indices.size(); i++) {
            if (teamid.get(indices.get(i)) == currentTeam) {
                c++; // Incrémente le compteur si le cercle appartient à la même équipe
            } else {
                break; // Arrête le comptage si l'équipe change
            }
        }
        avLive = currentTeam ? c : -c; // Met à jour le score live (positif pour rouge, négatif pour bleu)
    }


    public void update() {
        if (turnCounts == totalTurns) {
            turnCounts = 0;
            if(avLive < 0) {
                    scoreGameRED -= avLive;
            } else {
                    scoreGameBLUE += avLive;
            }
            avLive = 0 ;    
            centers.clear();
            radius.clear();
            teamid.clear();
            if (onScoreUpdate != null) {
            onScoreUpdate.run();
            }
        }
    }
    
    public int getAvLive() {
        return avLive;
    }
    
    public List<Point> getCenters() {
        return new ArrayList<>(centers);
    }

    public List<Integer> getRadius() {
        return new ArrayList<>(radius);
    }

    public List<Boolean> getTeamid() {
        return new ArrayList<>(teamid);
    }

    public void setTarget(Point target) {
        this.target = target;
    }

    public Point getTarget() {
        return target;
    }
    
    public String getCurrentTeam() {
        return (turnCounts % 2 == 0) ? "Blue Team" : "Red Team";
    }
    public int getTurnCounts() {
        return turnCounts;
    }
    
    public void setLaunchArea(Point launchArea) {
        this.launchArea = launchArea;
    }

    public Point getLaunchArea() {
        return launchArea;
    }
    
    public void setLaunchAreaPositions(List<Point> LaunchAreaPositions) {
        this.LaunchAreaPositions = LaunchAreaPositions; 
    }
    
    public List<Point> getLaunchAreaPositions() {
        return LaunchAreaPositions;
    }
}
