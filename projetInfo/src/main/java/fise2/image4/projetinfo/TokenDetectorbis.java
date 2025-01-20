package fise2.image4.projetinfo;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;

public class TokenDetectorbis {

    private Mat background;
    private List<Integer> range;
    private Integer fixedRadius;

    // Constructeur pour initialiser le détecteur avec une image de fond et une plage de valeurs
    public TokenDetectorbis(Mat background, List<Integer> range) {
        setBackground(background);
        this.range = range;
        this.fixedRadius = null;  // Le rayon est inconnu au début
    }

    // Méthode pour définir l'image de fond
    public void setBackground(Mat background) {
        Mat grayFrame = new Mat();
        Imgproc.cvtColor(background, grayFrame, Imgproc.COLOR_BGR2GRAY);
        this.background = grayFrame.clone();  // Cloner l'image grise pour la sauvegarder comme fond
    }

    // Méthode pour augmenter le contraste de l'image en ajustant alpha et beta
    public Mat increaseContrast(Mat frame, double alpha, double beta) {
        Mat result = new Mat();
        frame.convertTo(result, -1, alpha, beta);  // Conversion de l'image avec les facteurs de contraste
        return result;
    }

    // Détection du jeton avec l'algorithme Canny
    public Point detectTokenWithCanny(Mat frame) {
        Mat grayFrame = new Mat();
        Mat edges = new Mat();
        
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);  // Conversion en niveaux de gris

        Core.absdiff(grayFrame, background, grayFrame);  // Soustraction de l'image de fond

        Imgproc.Canny(grayFrame, edges, 50, 150);  // Détection des bords avec Canny

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(edges, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);  // Trouver les contours

        Point centerPoint = null;

        if (centerPoint != null) {
            return centerPoint;  // Si un centre est trouvé, retourner sa position
        }
        return null;  // Retourner null si aucun centre n'est détecté
    }

    // Détection du jeton avec HoughCircles pour trouver des cercles dans l'image
    public Circle detectToken(Mat frame) {
        Mat grayFrame = new Mat();
        Mat blackHat = new Mat();
        Mat blurred = new Mat();
        Mat background = new Mat();
        Mat circles = new Mat();
        Mat morphKernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new org.opencv.core.Size(10, 10));  // Élément structurant pour morphologie

        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);  // Conversion en niveaux de gris

        Imgproc.morphologyEx(grayFrame, blackHat, Imgproc.MORPH_BLACKHAT, morphKernel);  // Morphologie pour obtenir l'effet "black-hat"
        Core.subtract(grayFrame, blackHat, grayFrame);  // Soustraction de l'effet "black-hat"
        Mat m = increaseContrast(grayFrame, 2.0, 5.);  // Augmenter le contraste de l'image

        Imgproc.GaussianBlur(m, blurred, new org.opencv.core.Size(7, 7), 1.5, 1.5);  // Flou gaussien pour réduire le bruit

        // Détection des cercles avec la méthode Hough
        if (fixedRadius == null) {
                Imgproc.HoughCircles(blurred, circles, Imgproc.HOUGH_GRADIENT, 1.5, 30, 100, 0.9, 15, 30);  // HoughCircles sans rayon fixe
        } else {
                int minRadius = (int) (fixedRadius * 0.5);  // Rayon minimum ajusté
                int maxRadius = (int) (fixedRadius * 1.5);  // Rayon maximum ajusté
                Imgproc.HoughCircles(blurred, circles, Imgproc.HOUGH_GRADIENT, 1.5, 30, 100, 0.9, minRadius, maxRadius);  // HoughCircles avec rayon fixe
        }

        // Vérification si des cercles ont été trouvés et retour du premier cercle détecté
        if (!circles.empty()) {
            double[] circleData = circles.get(0, 0);
            if (circleData != null && circleData.length >= 3) {
                int x = (int) circleData[0];
                int y = (int) circleData[1];
                int radius;
                if (fixedRadius == null) {
                        radius = (int) circleData[2];
                } else {
                        radius = (int) fixedRadius;
                }
                return new Circle(new Point(x, y), radius);  // Retourne un nouvel objet Circle avec les coordonnées et le rayon
            }
        }
        return null;  // Retourner null si aucun cercle n'a été trouvé
    }

    // Méthode pour afficher les cercles détectés sur l'image
    public Mat displayCircles(Mat frame, List<Point> centers, List<Integer> radius, List<Boolean> teamid) {
        if (centers.size() == radius.size() && radius.size() == teamid.size()) {
            for (int i = 0; i < centers.size(); i++) {
                // Définir la couleur en fonction de l'équipe
                int color1 = 255 * (teamid.get(i) ? 1 : 0);
                int color2 = 255 * (!teamid.get(i) ? 1 : 0);
                Imgproc.circle(frame, centers.get(i), radius.get(i), new Scalar(color1, 0, color2), -1);  // Dessiner un cercle
            }
        } else {
            System.err.println("il y a un probleme au niveau des tableaux!!!!");  // Vérifier si les tailles des listes sont cohérentes
        }
        return frame;
    }

    // Méthode pour afficher l'interface utilisateur (scores)
    public Mat displayHud(Mat frame, int scoreGameBlue, int scoreGameRed,int advantage) {
        int fontFace = 0;
        double fontScale = 0.5;
        int thickness = 1;

        Point redTeamPosition = new Point(50, 50);
        Point blueTeamPosition = new Point(frame.cols() - 200, 50);
        Point advantagePosition = new Point(frame.cols() / 2 - 70, 50);
        Scalar redColor = new Scalar(0, 0, 255);
        Scalar blueColor = new Scalar(255, 0, 0);
        Scalar advColor = new Scalar(0, 255, 0);

        // Définir la couleur de l'avantage en fonction de l'équipe
        if (advantage > 0) {
            advColor = blueColor;
        }

        if (advantage < 0) {
                advColor = redColor;
        }

        // Afficher le texte de l'avantage et les scores
        Imgproc.putText(frame, "Advantage : " + Math.abs(advantage), advantagePosition, fontFace, fontScale, advColor, thickness);
        Imgproc.putText(frame, "Red Team: " + scoreGameRed, redTeamPosition, fontFace, fontScale, redColor, thickness);
        Imgproc.putText(frame, "Blue Team: " + scoreGameBlue, blueTeamPosition, fontFace, fontScale, blueColor, thickness);

        return frame;  // Retourner l'image avec les annotations
    }

    // Méthode pour afficher une cible (représentée par un croix) sur l'image
    public Mat displayTarget(Mat frame, Point target) {
        int crossSize = 20; 
        Scalar greenColor = new Scalar(0, 255, 0); 
        int thickness = 2;

        // Dessiner une croix autour de la cible
        Imgproc.line(frame, new Point(target.x - crossSize, target.y), new Point(target.x + crossSize, target.y), greenColor, thickness);
        Imgproc.line(frame, new Point(target.x, target.y - crossSize), new Point(target.x, target.y + crossSize), greenColor, thickness);

        return frame;  // Retourner l'image modifiée
    }

    // Méthode pour afficher une zone de lancer
    public Mat displayLaunchArea(Mat frame, Point launchArea){
        int zoneSize = 150;
        Scalar greenColor = new Scalar(0, 255, 0);
        int thickness = 2;

        // Dessiner un carré représentant la zone de lancement
        Imgproc.line(frame, new Point(launchArea.x - zoneSize / 2, launchArea.y - zoneSize / 2), new Point(launchArea.x + zoneSize / 2, launchArea.y - zoneSize / 2), greenColor, thickness);
        Imgproc.line(frame, new Point(launchArea.x - zoneSize / 2, launchArea.y + zoneSize / 2), new Point(launchArea.x + zoneSize / 2, launchArea.y + zoneSize / 2), greenColor, thickness);
        Imgproc.line(frame, new Point(launchArea.x - zoneSize / 2, launchArea.y - zoneSize / 2), new Point(launchArea.x - zoneSize / 2, launchArea.y + zoneSize / 2), greenColor, thickness);
        Imgproc.line(frame, new Point(launchArea.x + zoneSize / 2, launchArea.y - zoneSize / 2), new Point(launchArea.x + zoneSize / 2, launchArea.y + zoneSize / 2), greenColor, thickness);

        return frame;  // Retourner l'image avec la zone de lancer dessinée
    }

    // Méthode pour afficher une zone de lancement sous forme de rectangle
    public Mat displayLaunchArea(Mat frame, List<Point> launchAreaPositions){

        Point point1 = launchAreaPositions.get(0);
        Point point2 = launchAreaPositions.get(1);

        Scalar greenColor = new Scalar(0, 255, 0);
        int thickness = 2;

        // Dessiner un rectangle pour la zone de lancement
        Imgproc.rectangle(frame, new Point(Math.min(point1.x, point2.x), Math.min(point1.y, point2.y)), new Point(Math.max(point1.x, point2.x), Math.max(point1.y, point2.y)), greenColor, thickness);

        return frame;  // Retourner l'image avec la zone de lancement dessinée
    }

    // Méthode pour fixer le rayon d'un cercle sur la base d'une détection préalable
    public void fixRadius(Mat frame) {
        Mat grayFrame = new Mat();
        Mat absDiff = new Mat();
        Mat blurred = new Mat();
        Mat circles = new Mat();

        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);  // Conversion en niveaux de gris

        Core.absdiff(grayFrame, background, absDiff);  // Soustraction de l'image de fond

        Imgproc.GaussianBlur(absDiff, blurred, new org.opencv.core.Size(9, 9), 6);  // Flou gaussien

        Mat m = increaseContrast(blurred, 10.0, 5.);  // Augmenter le contraste

        Imgproc.HoughCircles(m, circles, Imgproc.HOUGH_GRADIENT, 1.3, 30, 150, 30, 0, 200);  // Détection des cercles

        // Vérification et enregistrement du rayon fixe
        if (!circles.empty()) {
            double[] circleData = circles.get(0, 0);
            if (circleData != null && circleData.length >= 3) {
                fixedRadius = (int) circleData[2];  // Enregistrer le rayon détecté
                System.out.println(fixedRadius);
            }
        }
    }
}
