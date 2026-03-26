package com.game;

import com.game.engine.BattleEngine;
import com.game.engine.StatusEffectManager;
import com.game.level.Level;
import com.game.level.LevelFactory;
import com.game.model.combatant.Player;
import com.game.model.combatant.Warrior;
import com.game.model.combatant.Wizard;
import com.game.model.item.Item;
import com.game.model.item.Potion;
import com.game.model.item.PowerStone;
import com.game.model.item.SmokeBomb;
import com.game.strategy.SpeedBasedTurnOrder;
import com.game.strategy.TurnOrderStrategy;
import com.game.ui.GameUI;

/**
 * Main game class — entry point for the Turn-Based Combat Arena.
 * Orchestrates game setup and replay loop.
 */
public class Game {

    private final GameUI ui;

    // Store last game settings for replay
    private String lastPlayerClass;
    private int lastItem1Choice;
    private int lastItem2Choice;
    private int lastDifficulty;

    public Game() {
        this.ui = new GameUI();
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.run();
    }

    /**
     * Main game loop: setup → battle → replay/new game/exit.
     */
    public void run() {
        ui.displayLoadingScreen();

        boolean running = true;
        boolean isNewGame = true;

        while (running) {
            if (isNewGame) {
                setupGame();
            }

            // Create player, items, and level from settings
            Player player = createPlayer(lastPlayerClass);
            addItemToPlayer(player, lastItem1Choice);
            addItemToPlayer(player, lastItem2Choice);
            Level level = LevelFactory.createLevel(lastDifficulty);

            // Initialize engine with injected dependencies (DIP)
            TurnOrderStrategy turnOrder = new SpeedBasedTurnOrder();
            StatusEffectManager effectManager = new StatusEffectManager();
            BattleEngine engine = new BattleEngine(player, level, turnOrder, effectManager, ui);

            // Start battle
            engine.startBattle();

            // End game menu
            int choice = ui.getEndGameChoice();
            switch (choice) {
                case 1: // Replay with same settings
                    isNewGame = false;
                    break;
                case 2: // New game
                    isNewGame = true;
                    System.out.println();
                    ui.displayLoadingScreen();
                    break;
                case 3: // Exit
                    running = false;
                    System.out.println();
                    System.out.println("  Thanks for playing! Goodbye.");
                    break;
            }
        }

        ui.close();
    }

    /**
     * Collect all game setup choices from the player.
     */
    private void setupGame() {
        // Select player class
        lastPlayerClass = ui.selectPlayerClass();

        // Select 2 items
        ui.displayItemSelection();
        lastItem1Choice = ui.selectItemChoice(1);
        lastItem2Choice = ui.selectItemChoice(2);

        // Select difficulty
        lastDifficulty = ui.selectDifficulty();
    }

    /**
     * Create a Player instance based on class name.
     */
    private Player createPlayer(String playerClass) {
        switch (playerClass) {
            case "Warrior":
                return new Warrior();
            case "Wizard":
                return new Wizard();
            default:
                throw new IllegalArgumentException("Unknown player class: " + playerClass);
        }
    }

    /**
     * Add an item to the player based on item choice number.
     */
    private void addItemToPlayer(Player player, int itemChoice) {
        Item item;
        switch (itemChoice) {
            case 1:
                item = new Potion();
                break;
            case 2:
                item = new PowerStone();
                break;
            case 3:
                item = new SmokeBomb();
                break;
            default:
                throw new IllegalArgumentException("Unknown item choice: " + itemChoice);
        }
        player.addItem(item);
    }
}
