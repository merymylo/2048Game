package com.DkAngeloAndMerymylo.game2048;

import java.io.*;

public class BestScore {

    private static final String BEST_SCORE_FILE = "Best_score.txt";
    public static int getBestScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(BEST_SCORE_FILE))) {
            return Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            return 0; // Se il file non viene letto, non esiste (o non puo' essere letto). Best score diventa 0
        }
    }

    public static void saveBestScore(int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BEST_SCORE_FILE))) {
            writer.write(String.valueOf(score));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
