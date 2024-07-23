package com.DkAngeloAndMerymylo.game2048;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class MenuController {

    @FXML
    private ChoiceBox<String> gridSizeChoiceBox;
    @FXML
    public void initialize() {

        // Imposta gli elementi nella ChoiceBox
        gridSizeChoiceBox.setItems(FXCollections.observableArrayList("4x4", "5x5", "6x6"));
        gridSizeChoiceBox.setValue("4x4"); // Imposta il valore predefinito
    }
    @FXML
    private void startGame() {
        String selectedGridSize = gridSizeChoiceBox.getValue();
        int gridSize = 4; // default

        if (selectedGridSize.equals("5x5")) {
            gridSize = 5;
        } else if (selectedGridSize.equals("6x6")) {
            gridSize = 6;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("game-view.fxml"));
            Parent root = loader.load();
            GameController gameController = loader.getController();
            gameController.setGridSize(gridSize);

            Stage stage = (Stage) gridSizeChoiceBox.getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showHelpDialog() {
        String helpText = "Welcome to 2048!\n"
                + "1. Move the tiles up, down, left and right to combine the tiles.\n"
                + "2. Tiles with the same number merge into one when they touch.\n"
                + "3. Add them up to reach 2048!\n\n"

                + "Key Bindings:\n"
                + "W / Up Arrow: Move Up\n"
                + "A / Left Arrow: Move Left\n"
                + "S / Down Arrow: Move Down\n"
                + "D / Right Arrow: Move Right";

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("How to Play");
        alert.setHeaderText("2048 Game Rules and Key Bindings");
        alert.setContentText(helpText);

        alert.showAndWait();
    }
}

