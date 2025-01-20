package fise2.image4.projetinfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javafx.util.Duration;
import java.util.List;

import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.shape.Rectangle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.text.Text;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;

public class SecondaryController {

    @FXML
    private ImageView cameraView;
    @FXML
    private Label redScoreLabel;
    @FXML
    private Label blueScoreLabel;
    @FXML
    private Label teamTurnLabel;
    @FXML
    private Label elapsedTimeLabel;
    @FXML
    private Label redTurnsLabel;
    @FXML
    private Label blueTurnsLabel;
    @FXML 
    private Button pauseButton;
    @FXML
    private Rectangle dimOverlay;
    @FXML
    private Rectangle launchAreaSetOverlay;
    @FXML
    private Rectangle pauseOverlay;
    @FXML
    private Text gamePausedLabel;
    @FXML
    private Text launchAreaSetLabel;
    @FXML 
    private Label avantage;
    @FXML
    private Label fpsLabel;
    @FXML
    private Text targetSetLabel;
    @FXML
    private Button menuButton;
    @FXML
    private Text placeTokenLabel;

    private Timeline elapsedTimeTimeline;
    private int elapsedSeconds;
    private boolean isPaused = false;
    private boolean isCalibrationSet = false;
    private boolean isLaunchAreaSet = false;
    private boolean isTargetSet = false;
    private List<Point> launchAreaPositions = new ArrayList<>();
    private VideoCapture capture;
    private Timer frameTimer;
    private GameManager gameEngine;
    private TokenDetectorbis tkd;
    private long lastFpsCheck = 0;
    private int currentFPS = 0;
    private int totalFrames = 0;
    private int nbTurns;
    private List<Point> centers;
    private boolean state;

    @FXML
    private void switchToPrimary() throws IOException {
        if(!isPaused)togglePause();
        
        App.setRoot("primary");
        
        stopCamera();
    }
    
    private void loadSettingsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("settings.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String key = parts[0];
                    String value = parts[1];
                    if(key.equals("nbTurns"))
                        nbTurns = Integer.parseInt(value);
                }
            }
            System.out.println("Settings loaded: Red Team Turns = " + nbTurns + ", Blue Team Turns = " + nbTurns);
        } catch (IOException e) {
            System.err.println("Failed to load settings from file.");
        }
    }

    public void initialize() {
        // Load OpenCV native library
        String libPath = System.getProperty("user.dir") + "/libs/opencv_java4100.dll";
        System.load(libPath);
        
        // Initialize settings;
        loadSettingsFromFile();

        // Initialize the video capture
        capture = new VideoCapture(0);
        
        centers = new ArrayList<>();
        state = true; 
        
        if (!capture.isOpened()) {
            System.out.println("Error: Unable to open video stream.");
            return;
        }

        // Initialize game manager and token detector
        Mat frame = captureFrame();
        if (!frame.empty()) {
            gameEngine = new GameManager(new Point(500,300),frame, nbTurns);
            Mat background = frame.clone();
            tkd = new TokenDetectorbis(background, List.of(0, 40));
        } else {
            System.out.println("Error: Unable to initialize background.");
            return;
        }

         // Start tracking elapsed time
        startElapsedTime();
        
        // Start frame update timer
        cameraView.setOnMouseClicked(event -> {
            // Adding click coordinates to the List
            launchAreaPositions.add(new Point(event.getX()/0.8, event.getY()/0.8));  
            
            // If two clicks occured then draw rectangle
            if(launchAreaPositions.size() == 2) {
                isLaunchAreaSet = true;
                gameEngine.setLaunchAreaPositions(launchAreaPositions);
            }
            
            // Third click is target position which must not be in launch area
            if(launchAreaPositions.size() == 3 && 
                event.getX()/0.8 >= Math.min(launchAreaPositions.get(0).x,launchAreaPositions.get(1).x) && 
                 event.getX()/0.8 <= Math.max(launchAreaPositions.get(0).x,launchAreaPositions.get(1).x) &&
                  event.getY()/0.8 >= Math.min(launchAreaPositions.get(0).y,launchAreaPositions.get(1).y) && 
                 event.getY()/0.8 <= Math.max(launchAreaPositions.get(0).y,launchAreaPositions.get(1).y))
                  {
                      launchAreaPositions.remove(2);
            }
            
            // Target position is not in launch area
            if(launchAreaPositions.size() == 3) {
                gameEngine.setTarget(launchAreaPositions.get(2));
                isTargetSet = true;
            }
        });
        startFrameTimer();
        applyRoundedCornersToCameraView();
        gameEngine.setOnScoreUpdate(() -> Platform.runLater(this::updateScoresUI));
    }
    
    private Double dist(Point A, Point B){
    	return Math.sqrt(Math.pow(A.x - B.x, 2) + Math.pow(A.y - B.y, 2));
    }
    
    public boolean isStable(List<Point> rayons, double threshold) {
        if (rayons.size() < 20) {
            return false; 
        }
        double sum = 0.0;
        for (int i = rayons.size() - 20; i < rayons.size() - 1; i++) {
            double distance = dist(rayons.get(i), rayons.get(i + 1));
            if (distance > threshold) {
                return false; 
            }
            sum += distance;
        }
        double normalizedSum = sum / 20.0;
        return normalizedSum < threshold;
    }

    public void updateUI() {
        // Update the UI with the remaining turns for each team
        redTurnsLabel.setText(String.valueOf(gameEngine.getTurnsRemainingRED()));
        blueTurnsLabel.setText(String.valueOf(gameEngine.getTurnsRemainingBLUE()));
        avantage.setText("AVANTAGE : " + String.valueOf(Math.abs(gameEngine.getAvLive())));
        // Red avantage
        if(gameEngine.getAvLive() < 0) {
            avantage.setStyle("-fx-text-fill: #ff0000; -fx-font-size: 20px; -fx-font-family: 'Immortal', sans-serif;");
        } else { // Blue avantage
            avantage.setStyle("-fx-text-fill: #0006ff; -fx-font-size: 20px; -fx-font-family: 'Immortal', sans-serif;");
        }
    }
    
    public void handleTurnMade(boolean isRedTeam) {
        gameEngine.setTurnsRemaining(isRedTeam);  // Red team turn
        updateUI();  // Refresh the UI to show the team turn
    }
    
    private void startElapsedTime() {
        elapsedSeconds = 0;
        // Create a Timeline to update the elapsed time every second
        elapsedTimeTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            elapsedSeconds++;
            updateElapsedTimeLabel();
        }));
        elapsedTimeTimeline.setCycleCount(Timeline.INDEFINITE); // Run indefinitely
        elapsedTimeTimeline.play();
    }

    private void updateElapsedTimeLabel() {
        int minutes = elapsedSeconds / 60;
        int seconds = elapsedSeconds % 60;
        elapsedTimeLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    public void resetElapsedTime() {
        elapsedTimeTimeline.stop();
        elapsedSeconds = 0;
        updateElapsedTimeLabel();
        elapsedTimeTimeline.play();
    }

    public void pauseElapsedTime() {
        elapsedTimeTimeline.pause();
    }

    public void resumeElapsedTime() {
        elapsedTimeTimeline.play();
    }
    
    private Mat captureFrame() {
        Mat frame = new Mat();
        lastFpsCheck = System.nanoTime();
        totalFrames = 0;
        currentFPS = 0;
        if (capture.isOpened()) {
            capture.read(frame);
            totalFrames++;
        }
        showFPS();
        return frame;
    }

    private void startFrameTimer() {
        frameTimer = new Timer(true);
        frameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Mat frame = captureFrame();
                if (!frame.empty()) {
                    //Gestion du jeu sans touches
                    updateFrame(frame);
                    if(isTargetSet && isLaunchAreaSet && !isPaused) {
                    Circle circle = tkd.detectToken(frame);
                    
                    // Calibration step
                    if(!isCalibrationSet) {
                        System.out.println("STARTING CALIBRATION, PLEASE PLACE TOKEN IN LAUNCH AREA");
                        placeTokenLabel.setVisible(true);
                        dimOverlay.setVisible(true);
                        while(!isPaused){
                            if (gameEngine.isCircleInLaunchArea(circle, launchAreaPositions)){
                                tkd.fixRadius(frame.clone());
                                System.out.println("CALIBRATION SUCCESSFULL");
                                break;
                            } else {
                                totalFrames++;
                                showFPS();
                            }
                            updateFrame(frame);
                            circle = tkd.detectToken(frame);
                            totalFrames++;
                            showFPS();
                        }
                        isCalibrationSet = !isCalibrationSet;
                        placeTokenLabel.setVisible(false);
                        dimOverlay.setVisible(false);
                    }
                    
                    
                    // Création de la frame à afficher
                    Mat displayFrame = frame.clone();

                    Point c = tkd.detectTokenWithCanny(displayFrame);
                    if(c != null) {
                        System.out.println("Pas null");
                        centers.add(c);
                    }

                    if (isStable(centers, 50) != state ) {
                        state = !state;
                        if (state) {
                                System.out.println("stable");
                        } else {
                                System.out.println("mooved");
                        }
                    }
                    
                    
                    // Waiting for player to place token in launching area
                    placeTokenLabel.setVisible(true);
                    dimOverlay.setVisible(true);
                    while (!gameEngine.isCircleInLaunchArea(circle, launchAreaPositions) && !isPaused) {
                        updateFrame(frame);
                        circle = tkd.detectToken(frame);
                        totalFrames++;
                        showFPS();
                    }
                    System.out.println("IN LAUNCH AREA");
                    placeTokenLabel.setVisible(false);
                    dimOverlay.setVisible(false);
            
                    
                    // Waiting for player to play
                    while (!gameEngine.isCircleOutLaunchArea(circle, launchAreaPositions) && !isPaused) {
                        updateFrame(frame);
                        circle = tkd.detectToken(frame);
                        totalFrames++;
                        showFPS();
                    }
                    
                    long startTime = System.currentTimeMillis();
                    
                    // Waiting 3s 
                    while ((System.currentTimeMillis()-startTime < 3000) && !isPaused){
                        updateFrame(frame);
                        totalFrames++;
                        showFPS();
                    }
                    
                    // Detect token
                    if(!isPaused) circle = tkd.detectToken(frame);
                    
                    // Adding circle
                    if ((gameEngine.isCircleOutLaunchArea(circle, launchAreaPositions)) && (circle!=null) && !isPaused){
                        gameEngine.addCircle(circle.getCenter(), circle.getRadius());
                        centers.clear();
                        System.out.println("LAUNCHED");
                    }

                    // Update UI
                    if(!isPaused) {
                        updateFrame(frame);
                        // Update the ImageView on the JavaFX Application Thread
                        Platform.runLater(() -> {
                            updateTurnsRemaining();
                            updateTurnUI();  // Update current team                     
                            updateScoresUI(); // Update UI safely
                            resetTurnsRemaining();
                            // Update game logic
                            gameEngine.update();
                        });     
                        }    
                    
                    
                    }
                }
            }
        }, 0, 33);
    }
    
 
    private void updateFrame(Mat frame){
        totalFrames++;
        if (!capture.read(frame)) {
                    System.out.println("Error: Could not read frame from stream.");
        } 
        Mat displayFrame = frame.clone();
        
        // Launch area set but not target : don't display circle yet
        if(isLaunchAreaSet && !isTargetSet) {
            tkd.displayLaunchArea(displayFrame, gameEngine.getLaunchAreaPositions());
            WritableImage writableImage = OpenCVUtils.matToImage(displayFrame);
            Platform.runLater(() -> cameraView.setImage(writableImage));
            while(!isTargetSet) {
                targetSetLabel.setVisible(true);
            } 
            targetSetLabel.setVisible(false);
            launchAreaSetOverlay.setVisible(false);
            }
        
        // Launch area and target set : display circles in addition
        if(isLaunchAreaSet && isTargetSet) {
            tkd.displayCircles(displayFrame, gameEngine.getCenters(), gameEngine.getRadius(), gameEngine.getTeamid());
            tkd.displayLaunchArea(displayFrame, gameEngine.getLaunchAreaPositions());
            tkd.displayTarget(displayFrame, gameEngine.getTarget());
        }
        
        // Show image
        WritableImage writableImage = OpenCVUtils.matToImage(displayFrame);
        Platform.runLater(() -> cameraView.setImage(writableImage));
        
        // Beggining of the game : launch area not set
        while(!isLaunchAreaSet) {
            launchAreaSetOverlay.setVisible(true);
            launchAreaSetLabel.setVisible(true);
        } 
        launchAreaSetLabel.setVisible(false);
    }

    private void showFPS() {
    if (System.nanoTime() - lastFpsCheck >= 1000000000) { // 1-second interval
        currentFPS = totalFrames;
        double frameTimeMs = (totalFrames > 0) ? (1000.0 / totalFrames) : 0; // Frame time in ms
        System.out.println(frameTimeMs + " ms");
        totalFrames = 0; // Reset frame count for the next second
        lastFpsCheck = System.nanoTime();
        Platform.runLater(() -> fpsLabel.setText(String.format("FPS: %d", currentFPS)));
    }
}

    private void resetTurnsRemaining(){
        if (gameEngine.getTurnCounts() == 2*nbTurns || gameEngine.getTurnCounts() == 0) {
            gameEngine.setTurnsRemainingRED(nbTurns);
            gameEngine.setTurnsRemainingBLUE(nbTurns);
            updateUI();
       }
    }
    
    private void updateTurnsRemaining() {
        if (gameEngine.getCurrentTeam().equals("Red Team")) handleTurnMade(false);
        else handleTurnMade(true);
    }
    
    private void updateScoresUI() {
        redScoreLabel.setText(String.valueOf(gameEngine.scoreGameRED));
        blueScoreLabel.setText(String.valueOf(gameEngine.scoreGameBLUE));
    }
    private void updateTurnUI() {
        String currentTeam = gameEngine.getCurrentTeam();
        teamTurnLabel.setText(currentTeam);
        // Change the label color based on the team
        if (currentTeam.equals("Red Team")) {
            teamTurnLabel.setStyle("-fx-text-fill: #ff0000; -fx-font-size: 30px; -fx-font-family: 'Immortal', sans-serif;");
        } else if (currentTeam.equals("Blue Team")) {
            teamTurnLabel.setStyle("-fx-text-fill: #0006ff; -fx-font-size: 30px; -fx-font-family: 'Immortal', sans-serif;");
        }
    }
    
    @FXML
    private void togglePause() {
        if (isPaused) {
            isPaused = !isPaused;
            resumeGame();
        } else {
            isPaused = !isPaused;
            pauseGame();
        }
    }
    
    private void pauseGame() {
        // Stop timers
        pauseElapsedTime();
        pauseButton.setStyle("-fx-background-color: #FF5555; -fx-background-radius: 155; -fx-font-family:  'Immortal', sans-serif; -fx-font-size: 20");
        // Show dim overlay to darken other UI elements
        if(!pauseOverlay.isVisible())pauseOverlay.setVisible(true);
            gamePausedLabel.setVisible(true);
        
        // Stop frame capture timer
        frameTimer.cancel();
        
        // Ensure it's set to null so we can recreate it on resume
        frameTimer = null; 
 
        // Disable camera
        cameraView.setDisable(true);
        System.out.println("Game paused.");
    }

    private void resumeGame() {
        // Restart timers
        resumeElapsedTime();
        startFrameTimer();

        // Re-enable interactions
        cameraView.setDisable(false);
        pauseButton.setStyle("-fx-background-color: #6fdbff; -fx-background-radius: 155; -fx-font-family:  'Immortal', sans-serif; -fx-font-size: 20");
        
        // Show dim overlay to darken other UI elements
        pauseOverlay.setVisible(false);
        gamePausedLabel.setVisible(false);

        System.out.println("Game resumed.");
    }

    
    public void stopCamera() {
        if (frameTimer != null) {
            frameTimer.cancel();
        }
        if (capture != null) {
            capture.release();
        }
    }
    
    private void applyRoundedCornersToCameraView() {
        // Define the width, height, and corner radius
        double cornerRadius = 20; // Adjust the radius as needed
        double width = cameraView.getFitWidth();
        double height = cameraView.getFitHeight();

        // Create a Rectangle to clip the ImageView with rounded corners
        Rectangle clip = new Rectangle(0, 0, width, height);
        clip.setArcWidth(cornerRadius);
        clip.setArcHeight(cornerRadius);

        // Clip the cameraView with the rectangle
        cameraView.setClip(clip);

        // Optional: Set the width and height of the ImageView (you can adjust this)
        cameraView.setFitWidth(width);
        cameraView.setFitHeight(height);
    }
}
