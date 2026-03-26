package com.game.level;

import java.util.ArrayList;
import java.util.List;
import com.game.model.combatant.Enemy;

/**
 * Represents a difficulty level with initial and backup enemy spawns.
 */
public class Level {

    private int difficulty;
    private String difficultyName;
    private List<Enemy> initialEnemies;
    private List<Enemy> backupEnemies;

    public Level(int difficulty, String difficultyName,
                 List<Enemy> initialEnemies, List<Enemy> backupEnemies) {
        this.difficulty = difficulty;
        this.difficultyName = difficultyName;
        this.initialEnemies = new ArrayList<>(initialEnemies);
        this.backupEnemies = new ArrayList<>(backupEnemies);
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getDifficultyName() {
        return difficultyName;
    }

    public List<Enemy> getInitialEnemies() {
        return new ArrayList<>(initialEnemies);
    }

    public List<Enemy> getBackupEnemies() {
        return new ArrayList<>(backupEnemies);
    }

    public boolean hasBackupSpawn() {
        return !backupEnemies.isEmpty();
    }

    @Override
    public String toString() {
        return "Level " + difficulty + " (" + difficultyName + ") — "
                + initialEnemies.size() + " initial, "
                + backupEnemies.size() + " backup";
    }
}
