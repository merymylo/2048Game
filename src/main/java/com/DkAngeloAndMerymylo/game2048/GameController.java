package com.DkAngeloAndMerymylo.game2048;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;

public class GameController {
    @FXML private GridPane gridPane;
    @FXML private Label scoreLabel;
    @FXML private Label bestScoreLabel;
    @FXML private HBox bottomBox;
    @FXML private AnchorPane anchorPane;

    private Tile[][] tiles;
    private int score = 0;
    private int bestScore = 0;
    boolean isNewBest = false;
    private int gridSize = 4; // default size
    private static final Map<Integer, String> TILE_COLORS = new HashMap<>();

    static {
        TILE_COLORS.put(2, "#eee4da"); // Light beige for 2
        TILE_COLORS.put(4, "#ede0c8"); // Light brown for 4
        TILE_COLORS.put(8, "#f2b179"); // Orange for 8
        TILE_COLORS.put(16, "#f59563"); // Darker orange for 16
        TILE_COLORS.put(32, "#f67c5f"); // Red-orange for 32
        TILE_COLORS.put(64, "#f65e3b"); // Red for 64
        TILE_COLORS.put(128, "#edcf72"); // Light yellow for 128
        TILE_COLORS.put(256, "#edcc61"); // Yellow for 256
        TILE_COLORS.put(512, "#edc850"); // Dark yellow for 512
        TILE_COLORS.put(1024, "#edc53f"); // Gold for 1024
        TILE_COLORS.put(2048, "#edc22e"); // Dark gold for 2048
    }
    public void setGridSize(int size) {
        this.gridSize = size;
        this.tiles = new Tile[gridSize][gridSize];
        initializeGrid();
        addRandomTile(true);
        addRandomTile(false);
    }

    @FXML
    public void initialize() {
        AnchorPane.setTopAnchor(gridPane, 0.0);
        AnchorPane.setBottomAnchor(gridPane, 50.0);
        AnchorPane.setLeftAnchor(gridPane, 0.0);
        AnchorPane.setRightAnchor(gridPane, 0.0);

        AnchorPane.setBottomAnchor(bottomBox, 0.0);
        AnchorPane.setLeftAnchor(bottomBox, 0.0);
        AnchorPane.setRightAnchor(bottomBox, 0.0);

        anchorPane.setOnKeyPressed(this::handleKeyPress);
        anchorPane.setFocusTraversable(true);
        Platform.runLater(() -> anchorPane.requestFocus());

        setGridSize(gridSize);

        bestScore = BestScore.getBestScore();
        bestScoreLabel.setText(String.valueOf(bestScore));
        updateUI();
    }

    private void initializeGrid() {
        gridPane.getChildren().clear();
        gridPane.getRowConstraints().clear();
        gridPane.getColumnConstraints().clear();

        // Impostazione dei constraints per le righe e le colonne, in maniera tale da renderle quadrate
        for (int i = 0; i < gridSize; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(100.0 / gridSize);
            gridPane.getRowConstraints().add(rowConstraints);

            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100.0 / gridSize);
            gridPane.getColumnConstraints().add(columnConstraints);
        }

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Pane pane = new Pane();
                pane.setStyle("-fx-border-color: black; -fx-background-color: lightgray;");
                pane.setPrefSize(100, 100); // Impostazione della dimensione preferita per rendere i tiles più grandi
                gridPane.add(pane, j, i);
            }
        }
    }

    private void addRandomTile(boolean isFirstTile) {
        Random rand = new Random();
        int x, y;
        do {
            x = rand.nextInt(gridSize);
            y = rand.nextInt(gridSize);
        } while (tiles[x][y] != null);
        if(isFirstTile){
            tiles[x][y] = new Tile(2);
        }else{
            int value;
            if (rand.nextInt(100) < 85) {
                value = 2; // 85% di probabilità
            } else {
                value = 4; // 15% di probabilità
            }

            tiles[x][y] = new Tile(value);
        }
        updateUI();
    }

    private void updateUI() {
        gridPane.getChildren().clear();
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                StackPane stackPane = new StackPane();
                if (tiles[i][j] != null) {
                    int value = tiles[i][j].getValue();
                    String color = TILE_COLORS.getOrDefault(value, "#cdc1b4");
                    stackPane.setStyle("-fx-border-color: black; -fx-background-color: "+color+";");
                    Label label = new Label(String.valueOf(value));
                    label.setStyle("-fx-font-size: 24px; -fx-text-fill: black; -fx-font-weight: bold;");
                    label.setAlignment(Pos.CENTER);
                    stackPane.getChildren().add(label);
                } else {
                    stackPane.setStyle("-fx-border-color: black; -fx-background-color: lightgray;");
                }
                stackPane.setPrefSize(100, 100); // Imposta la dimensione preferita per rendere i tiles più grandi
                gridPane.add(stackPane, j, i);
            }
        }
        scoreLabel.setText(String.valueOf(score));
        System.out.println("UI updated");
    }

    private void showGameOverDialog(boolean isWin) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Over");
        if(isWin){
            alert.setHeaderText("You win!");
        }
        else{
            alert.setHeaderText("Game Over!");
        }
        if(isNewBest) {
            alert.setContentText("It's a new record! Your score is: " + score + "\nWould you like to return to the main menu or exit the game?");
        }else{
            alert.setContentText("Your score is: " + score + "\nWould you like to return to the main menu or exit the game?");
        }
        
        ButtonType buttonTypeMenu = new ButtonType("Main Menu");
        ButtonType buttonTypeExit = new ButtonType("Exit");

        alert.getButtonTypes().setAll(buttonTypeMenu, buttonTypeExit);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeMenu) {
            returnToMainMenu();
        } else if (result.isPresent() && result.get() == buttonTypeExit) {
            Platform.exit();
        }
    }

    private void returnToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) gridPane.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleKeyPress(KeyEvent event) {
//        System.out.println("Key Pressed: " + event.getCode());
        boolean moved = false;
        if (event.getCode() == KeyCode.W || event.getCode() == KeyCode.UP) {
            moved = moveUp();
        } else if (event.getCode() == KeyCode.S || event.getCode() == KeyCode.DOWN) {
            moved = moveDown();
        } else if (event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT) {
            moved = moveRight();
        } else if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT) {
            moved = moveLeft();
        }

        if(moved){
            checkWinCondition();
            addRandomTile(false);
            if(isGameOver()){
//                System.out.println("Game Over");
                showGameOverDialog(false);
            }
        }
    }

    private void checkWinCondition() {
        for(int i = 0; i < gridSize; i++){
            for(int j = 0; j < gridSize; j++){
                if(tiles[i][j] != null && tiles[i][j].getValue() == 2048){
                    showGameOverDialog(true);
                }
            }
        }
    }

    private int[] extractNumbers(int fromThis, boolean isCol){
        int[] numbers = new int[gridSize];
        int i = 0;

        if(isCol) {
            // Estraggo i numeri dalla colonna "fromThis"
            for (int row = 0; row < gridSize; row++) {
                if (tiles[row][fromThis] != null) {
                    numbers[i++] = tiles[row][fromThis].getValue();
                    tiles[row][fromThis] = null;
                }
            }
        }
        else{
            // Estraggo i numeri dalla riga "fromThis"
            for (int col = 0; col < gridSize; col++) {
                if (tiles[fromThis][col] != null) {
                    numbers[i++] = tiles[fromThis][col].getValue();
                    tiles[fromThis][col] = null;
                }
            }
        }
//        System.out.println("Extracted numbers: " + Arrays.toString(numbers));
        return numbers;
    }

    private boolean combineNumbers(int[] numbers, boolean toTheLeft){
        // Combino i numeri consecutivi uguali e aggiorno lo score
        boolean moved = false;
        if(toTheLeft){
            // Le celle che assumono valore devono essere quelle piu' a sinistra nel vettore
            int changed = 0;
            for (int i = 0; i < gridSize; i++) {
                if (numbers[i] != 0) {
                    if (i + 1 < gridSize && numbers[i] == numbers[i + 1]) {
                        numbers[changed] = numbers[i] * 2;
                        score += numbers[changed];
                        updateBestScore();
                        moved = true;
                        i++;
                    } else {
                        numbers[changed] = numbers[i];
                    }
                    changed++;
                }
            }

            if(changed != gridSize) {
                moved = true;
                // Azzero le rimanenti posizioni
                while (changed < gridSize) {
                    numbers[changed++] = 0;
                }
            }

        }else{
            // Le celle che assumono valore devono essere quelle piu' a destra nel vettore
            int changed = gridSize - 1;
            for (int i = gridSize - 1; i >= 0; i--) {
                if (numbers[i] != 0) {
                    if ((i-1)>=0 && numbers[i] == numbers[i - 1]) {
                        numbers[changed] = numbers[i] * 2;
                        score += numbers[changed];
                        updateBestScore();
                        moved = true;
                        i--;
                    } else {
                        numbers[changed] = numbers[i];
                    }
                    changed--;
                }
            }
            if(changed != -1) {
                moved = true;
                // Azzero le rimanenti posizioni
                while (changed >= 0) {
                    numbers[changed--] = 0;
                }
            }
        }
//        System.out.println("Combined numbers: " + Arrays.toString(numbers));
        return moved;
    }
    private boolean moveUp() {
//        System.out.println("Move up init");
        boolean moved = false;
        if(isPossible("Up")) {
            for (int col = 0; col < gridSize; col++) {
                int[] numbers = extractNumbers(col, true);
                // Combino i numeri consecutivi uguali e aggiorno lo score
                if (combineNumbers(numbers, true)) {
                    moved = true;
                }

                // Aggiorna la colonna con i nuovi valori
                for (int i = 0; i < gridSize; i++) {
                    if (numbers[i] != 0) {
                        tiles[i][col] = new Tile(numbers[i]);
                    }
                }
            }
        }

//        System.out.println("Move up executed");
        return moved;
    }

    private boolean moveDown() {
//        System.out.println("Move down init");
        boolean moved = false;
        if(isPossible("Down")) {
            for (int col = 0; col < gridSize; col++) {
                int[] numbers = extractNumbers(col, true);

                // Combino i numeri consecutivi uguali e aggiorno lo score
                if (combineNumbers(numbers, false)) {
                    moved = true;
                }

                // Aggiorna la colonna con i nuovi valori
                for (int i = 0; i < gridSize; i++) {
                    if (numbers[i] != 0) {
                        tiles[i][col] = new Tile(numbers[i]);
                    }
                }
            }
        }

//        System.out.println("Move down executed");
        return moved;
    }

    private boolean moveLeft() {
//        System.out.println("Move left init");
        boolean moved = false;
        if(isPossible("Left")) {
            for (int row = 0; row < gridSize; row++) {
                int[] numbers = extractNumbers(row, false);
                // Combino i numeri consecutivi uguali e aggiorno lo score
                if (combineNumbers(numbers, true)) {
                    moved = true;
                }
                // Aggiorna la riga con i nuovi valori
                for (int i = 0; i < gridSize; i++) {
                    if (numbers[i] != 0) {
                        tiles[row][i] = new Tile(numbers[i]);
                    }
                }
            }
        }
//        System.out.println("Move left executed");
        return moved;
    }

    private boolean moveRight() {
//        System.out.println("Move right init");
        boolean moved = false;
        if(isPossible("Right")) {
            for (int row = 0; row < gridSize; row++) {
                int[] numbers = extractNumbers(row, false);

                // Combino i numeri consecutivi uguali e aggiorno lo score
                if (combineNumbers(numbers, false)) {
                    moved = true;
                }

                // Aggiorna la colonna con i nuovi valori
                for (int i = 0; i < gridSize; i++) {
                    if (numbers[i] != 0) {
                        tiles[row][i] = new Tile(numbers[i]);
                    }
                }
            }
        }
//        System.out.println("Move right executed");
        return moved;
    }

    public boolean isPossible(String move){
        switch (move) {
            case "Up" -> {
                for (int col = 0; col < gridSize; col++) {
                    for (int row = 1; row < gridSize; row++) {
                        if (tiles[row][col] != null && (tiles[row - 1][col] == null || tiles[row - 1][col].getValue() == tiles[row][col].getValue())) {
                            return true;
                        }
                    }
                }
                return false;
            }
            case "Down" -> {
                for (int col = 0; col < gridSize; col++) {
                    for (int row = gridSize - 2; row >= 0; row--) {
                        if (tiles[row][col] != null && (tiles[row + 1][col] == null || tiles[row + 1][col].getValue() == tiles[row][col].getValue())) {
                            return true;
                        }
                    }
                }
                return false;
            }
            case "Right" -> {
                for (int row = 0; row < gridSize; row++) {
                    for (int col = gridSize - 2; col >= 0; col--) {
                        if (tiles[row][col] != null && (tiles[row][col + 1] == null || tiles[row][col + 1].getValue() == tiles[row][col].getValue())) {
                            return true;
                        }
                    }
                }
            }
            case "Left" -> {
                for (int row = 0; row < gridSize; row++) {
                    for (int col = 1; col < gridSize; col++) {
                        if (tiles[row][col] != null && (tiles[row][col - 1] == null || tiles[row][col - 1].getValue() == tiles[row][col].getValue())) {
                            return true;
                        }
                    }
                }
                return false;
            }
        }

        return false;
    }
    private boolean isGameOver() {
        // Condizioni di game over: tutte le celle piene, nessuna combinazione disponibile
        for(int col = 0; col < gridSize; col++){
            for(int row = 0; row < gridSize; row++){
                if(tiles[row][col] == null){
                    return false;
                }
                else if((row+1)<gridSize && tiles[row+1][col]!= null && tiles[row][col].getValue() == tiles[row+1][col].getValue()){
                    return false;
                }
                else if((col+1)<gridSize && tiles[row][col+1]!= null && tiles[row][col].getValue() == tiles[row][col+1].getValue()){
                    return false;
                }
            }
        }
        return true;
    }

    private void updateBestScore() {
        if (score > bestScore) {
            bestScore = score;
            bestScoreLabel.setText(String.valueOf(bestScore));
            BestScore.saveBestScore(bestScore);
            isNewBest = true;
        }
    }
}