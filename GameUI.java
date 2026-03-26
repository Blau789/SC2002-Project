package com.game.ui;

import java.util.List;
import java.util.Scanner;

import com.game.model.combatant.Combatant;
import com.game.model.combatant.Enemy;
import com.game.model.combatant.Player;
import com.game.model.combatant.Warrior;
import com.game.model.combatant.Wizard;
import com.game.model.item.Item;

/**
 * Command Line Interface for the game.
 * SRP: handles all user interaction — display and input only.
 * Separated from battle logic (Boundary layer).
 */
public class GameUI {

    private final Scanner scanner;

    private static final String SEPARATOR = "════════════════════════════════════════════════════════";
    private static final String THIN_SEP  = "────────────────────────────────────────────────────────";

    public GameUI() {
        this.scanner = new Scanner(System.in);
    }

    // ========================
    // LOADING / SETUP SCREEN
    // ========================

    public void displayLoadingScreen() {
        System.out.println(SEPARATOR);
        System.out.println("       ⚔️  TURN-BASED COMBAT ARENA  ⚔️");
        System.out.println(SEPARATOR);
        System.out.println();
    }

    /**
     * Display player classes and let user choose.
     * @return "Warrior" or "Wizard"
     */
    public String selectPlayerClass() {
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║       SELECT YOUR CLASS          ║");
        System.out.println("╠══════════════════════════════════╣");
        System.out.println("║                                  ║");
        System.out.println("║  1. Warrior                      ║");
        System.out.println("║     HP: 260  ATK: 40             ║");
        System.out.println("║     DEF: 20  SPD: 30             ║");
        System.out.println("║     Skill: Shield Bash           ║");
        System.out.println("║     (Damage + Stun 2 turns)      ║");
        System.out.println("║                                  ║");
        System.out.println("║  2. Wizard                       ║");
        System.out.println("║     HP: 200  ATK: 50             ║");
        System.out.println("║     DEF: 10  SPD: 20             ║");
        System.out.println("║     Skill: Arcane Blast           ║");
        System.out.println("║     (Damage all + ATK bonus)     ║");
        System.out.println("║                                  ║");
        System.out.println("╚══════════════════════════════════╝");
        System.out.print("Enter choice (1 or 2): ");

        int choice = getIntInput(1, 2);
        return choice == 1 ? "Warrior" : "Wizard";
    }

    /**
     * Let user select 2 items (duplicates allowed).
     */
    public void displayItemSelection() {
        System.out.println();
        System.out.println(THIN_SEP);
        System.out.println("  AVAILABLE ITEMS");
        System.out.println(THIN_SEP);
        System.out.println("  1. Potion       — Heal 100 HP");
        System.out.println("  2. Power Stone  — Free special skill use");
        System.out.println("  3. Smoke Bomb   — Negate damage for 2 turns");
        System.out.println(THIN_SEP);
    }

    public int selectItemChoice(int itemNumber) {
        System.out.print("Select item " + itemNumber + " (1-3): ");
        return getIntInput(1, 3);
    }

    /**
     * Display difficulty levels and let user choose.
     */
    public int selectDifficulty() {
        System.out.println();
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║       SELECT DIFFICULTY          ║");
        System.out.println("╠══════════════════════════════════╣");
        System.out.println("║                                  ║");
        System.out.println("║  1. Easy                         ║");
        System.out.println("║     3 Goblins                    ║");
        System.out.println("║                                  ║");
        System.out.println("║  2. Medium                       ║");
        System.out.println("║     1 Goblin + 1 Wolf            ║");
        System.out.println("║     Backup: 2 Wolves             ║");
        System.out.println("║                                  ║");
        System.out.println("║  3. Hard                         ║");
        System.out.println("║     2 Goblins                    ║");
        System.out.println("║     Backup: 1 Goblin + 2 Wolves  ║");
        System.out.println("║                                  ║");
        System.out.println("╚══════════════════════════════════╝");
        System.out.println();

        // Display enemy stats
        System.out.println("  Enemy Stats:");
        System.out.println("  Goblin — HP: 55  ATK: 35  DEF: 15  SPD: 25");
        System.out.println("  Wolf   — HP: 40  ATK: 45  DEF: 5   SPD: 35");
        System.out.println();

        System.out.print("Enter difficulty (1-3): ");
        return getIntInput(1, 3);
    }

    // ========================
    // BATTLE DISPLAY
    // ========================

    public void displayBattleStart(Player player, List<Enemy> enemies) {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("  BATTLE START!");
        System.out.println(SEPARATOR);
        System.out.println("  Player: " + player);
        System.out.println("  Items:  " + formatItems(player.getItems()));
        System.out.println("  Enemies:");
        for (Enemy e : enemies) {
            System.out.println("    - " + e);
        }
        System.out.println(SEPARATOR);
        System.out.println();
    }

    public void displayRoundStart(int round) {
        System.out.println();
        System.out.println("╔═══════════════════════════╗");
        System.out.println("║       ROUND " + String.format("%-13d", round) + "║");
        System.out.println("╚═══════════════════════════╝");
    }

    public void displayRoundEnd(int round, Player player, List<Enemy> aliveEnemies,
                                 List<Combatant> allCombatants) {
        System.out.println();
        System.out.println(THIN_SEP);
        System.out.println("  End of Round " + round);
        System.out.println(THIN_SEP);

        // Player status
        String playerStatus = player.isAlive() ? "ALIVE" : "DEFEATED";
        System.out.println("  " + player.getName() + ": HP " + player.getHp()
                + "/" + player.getMaxHp() + " [" + playerStatus + "]");

        // Enemy status
        for (Combatant c : allCombatants) {
            if (c instanceof Enemy) {
                String status = c.isAlive()
                        ? "HP " + c.getHp() + "/" + ((Enemy) c).getMaxHp()
                        : "✗ ELIMINATED";
                System.out.println("  " + c.getName() + ": " + status);
            }
        }

        // Items and cooldown
        System.out.println("  Items: " + formatItems(player.getItems()));
        System.out.println("  Special Skill Cooldown: " + player.getCooldown() + " rounds");
        System.out.println(THIN_SEP);
    }

    public void displayTurnStart(Combatant combatant) {
        System.out.println();
        System.out.println("  ► " + combatant.getName() + "'s Turn");
    }

    public void displayStunned(Combatant combatant) {
        System.out.println("    " + combatant.getName() + " is STUNNED! Turn skipped.");
    }

    public void displayStatusEffects(String name, String effects) {
        System.out.println("    Active effects on " + name + ": " + effects);
    }

    public void displayActionResult(String result) {
        // Handle multi-line results (e.g., Arcane Blast)
        String[] lines = result.split("\n");
        for (String line : lines) {
            System.out.println("    " + line);
        }
    }

    public void displayBackupSpawn(List<Enemy> enemies) {
        System.out.println();
        System.out.println("  ╔═══════════════════════════════════╗");
        System.out.println("  ║   ⚠ BACKUP ENEMIES SPAWNED! ⚠    ║");
        System.out.println("  ╚═══════════════════════════════════╝");
        for (Enemy e : enemies) {
            System.out.println("    + " + e);
        }
        System.out.println();
    }

    // ========================
    // PLAYER INPUT
    // ========================

    /**
     * Display action menu and get player choice.
     * @return index of chosen action (0-based)
     */
    public int getPlayerAction(Player player, List<String> options, List<Enemy> enemies) {
        System.out.println();
        System.out.println("    " + player.getName() + " — HP: " + player.getHp()
                + "/" + player.getMaxHp()
                + " | ATK: " + player.getAttack()
                + " | DEF: " + player.getDefense());
        System.out.println("    Choose an action:");

        for (int i = 0; i < options.size(); i++) {
            String extra = "";
            String opt = options.get(i);
            if (opt.equals("Special Skill")) {
                extra = " (" + player.getSpecialSkillName() + ")";
            } else if (opt.equals("Use Item")) {
                extra = " " + formatItems(player.getItems());
            }
            System.out.println("      " + (i + 1) + ". " + opt + extra);
        }

        System.out.print("    Enter choice: ");
        return getIntInput(1, options.size()) - 1;
    }

    /**
     * Let player select a target enemy.
     */
    public Combatant selectTarget(List<Enemy> enemies) {
        if (enemies.size() == 1) {
            System.out.println("    Target: " + enemies.get(0).getName());
            return enemies.get(0);
        }

        System.out.println("    Select target:");
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            System.out.println("      " + (i + 1) + ". " + e.getName()
                    + " [HP:" + e.getHp() + "/" + e.getMaxHp() + "]");
        }
        System.out.print("    Enter choice: ");
        int choice = getIntInput(1, enemies.size()) - 1;
        return enemies.get(choice);
    }

    /**
     * Let player select an item from inventory.
     */
    public Item selectItem(List<Item> items) {
        if (items.size() == 1) {
            System.out.println("    Using: " + items.get(0).getName());
            return items.get(0);
        }

        System.out.println("    Select item:");
        for (int i = 0; i < items.size(); i++) {
            System.out.println("      " + (i + 1) + ". " + items.get(i).getName());
        }
        System.out.print("    Enter choice: ");
        int choice = getIntInput(1, items.size()) - 1;
        return items.get(choice);
    }

    // ========================
    // END GAME SCREENS
    // ========================

    public void displayVictoryScreen(int remainingHp, int maxHp, int totalRounds) {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("  ★★★ VICTORY! ★★★");
        System.out.println(SEPARATOR);
        System.out.println("  Congratulations, you have defeated all your enemies.");
        System.out.println();
        System.out.println("  Statistics:");
        System.out.println("    Remaining HP:  " + remainingHp + "/" + maxHp);
        System.out.println("    Total Rounds:  " + totalRounds);
        System.out.println(SEPARATOR);
    }

    public void displayDefeatScreen(int enemiesRemaining, int totalRounds) {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("  ✗ DEFEATED ✗");
        System.out.println(SEPARATOR);
        System.out.println("  Defeated. Don't give up, try again!");
        System.out.println();
        System.out.println("  Statistics:");
        System.out.println("    Enemies Remaining:    " + enemiesRemaining);
        System.out.println("    Total Rounds Survived: " + totalRounds);
        System.out.println(SEPARATOR);
    }

    /**
     * Show end-game options: replay, new game, or exit.
     * @return 1=replay same settings, 2=new game, 3=exit
     */
    public int getEndGameChoice() {
        System.out.println();
        System.out.println("  What would you like to do?");
        System.out.println("    1. Replay with same settings");
        System.out.println("    2. Start a new game");
        System.out.println("    3. Exit");
        System.out.print("  Enter choice: ");
        return getIntInput(1, 3);
    }

    // ========================
    // UTILITY
    // ========================

    /**
     * Get validated integer input within a range.
     */
    private int getIntInput(int min, int max) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.print("  Invalid input. Enter a number (" + min + "-" + max + "): ");
            } catch (NumberFormatException e) {
                System.out.print("  Invalid input. Enter a number (" + min + "-" + max + "): ");
            }
        }
    }

    private String formatItems(List<Item> items) {
        if (items.isEmpty()) {
            return "[No items]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(items.get(i).getName());
        }
        sb.append("]");
        return sb.toString();
    }

    public void close() {
        scanner.close();
    }
}
