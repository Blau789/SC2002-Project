package com.game.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.game.level.Level;
import com.game.model.action.Action;
import com.game.model.action.BasicAttack;
import com.game.model.action.Defend;
import com.game.model.action.SpecialSkill;
import com.game.model.action.UseItem;
import com.game.model.combatant.Combatant;
import com.game.model.combatant.Enemy;
import com.game.model.combatant.Player;
import com.game.model.item.Item;
import com.game.strategy.TurnOrderStrategy;
import com.game.ui.GameUI;

/**
 * Core battle engine managing the game loop.
 * SRP: manages round flow only; delegates to other components.
 * DIP: depends on TurnOrderStrategy (abstraction), not concrete implementation.
 */
public class BattleEngine {

    private final Player player;
    private final List<Combatant> combatants;
    private final TurnOrderStrategy turnOrderStrategy;
    private final StatusEffectManager statusEffectManager;
    private final GameUI ui;
    private final Level level;
    private int roundCount;
    private boolean backupSpawned;

    public BattleEngine(Player player, Level level,
                        TurnOrderStrategy turnOrderStrategy,
                        StatusEffectManager statusEffectManager,
                        GameUI ui) {
        this.player = player;
        this.level = level;
        this.turnOrderStrategy = turnOrderStrategy;
        this.statusEffectManager = statusEffectManager;
        this.ui = ui;
        this.roundCount = 0;
        this.backupSpawned = false;

        // Initialize combatants list with player + initial enemies
        this.combatants = new ArrayList<>();
        this.combatants.add(player);
        this.combatants.addAll(level.getInitialEnemies());
    }

    /**
     * Main game loop: executes rounds until battle ends.
     */
    public void startBattle() {
        ui.displayBattleStart(player, getAliveEnemies());

        while (!isBattleOver()) {
            roundCount++;
            executeRound();
        }

        // Display result
        if (isPlayerVictory()) {
            ui.displayVictoryScreen(player.getHp(), player.getMaxHp(), roundCount);
        } else {
            ui.displayDefeatScreen(getAliveEnemies().size(), roundCount);
        }
    }

    /**
     * Execute a single round: determine turn order, process each turn.
     */
    private void executeRound() {
        ui.displayRoundStart(roundCount);

        // Get turn order for alive combatants
        List<Combatant> turnOrder = turnOrderStrategy.determineTurnOrder(getAliveCombatants());

        for (Combatant combatant : turnOrder) {
            // Skip eliminated combatants (may have been killed this round)
            if (!combatant.isAlive()) {
                continue;
            }

            processTurn(combatant);

            // Check for battle end after each turn
            if (isBattleOver()) {
                break;
            }

            // Check for backup spawn after enemies are cleared
            if (!backupSpawned && allInitialEnemiesDefeated() && level.hasBackupSpawn()) {
                handleBackupSpawn();
                // Don't break — continue the round with remaining turns
            }
        }

        // Tick status effects at end of round
        for (Combatant c : getAliveCombatants()) {
            statusEffectManager.tickEffects(c);
            statusEffectManager.removeExpiredEffects(c);
        }

        // Display end-of-round status
        ui.displayRoundEnd(roundCount, player, getAliveEnemies(), combatants);
    }

    /**
     * Process a single combatant's turn.
     */
    private void processTurn(Combatant combatant) {
        ui.displayTurnStart(combatant);

        // Check for stun
        if (statusEffectManager.isStunned(combatant)) {
            ui.displayStunned(combatant);
            return;
        }

        if (combatant instanceof Player) {
            processPlayerTurn((Player) combatant);
        } else if (combatant instanceof Enemy) {
            processEnemyTurn((Enemy) combatant);
        }
    }

    /**
     * Handle the player's turn: show menu, get choice, execute.
     */
    private void processPlayerTurn(Player p) {
        // Decrement cooldown at the start of player's turn
        p.decrementCooldown();

        // Display status effects
        String effects = statusEffectManager.getEffectsSummary(p);
        if (!"None".equals(effects)) {
            ui.displayStatusEffects(p.getName(), effects);
        }

        // Get available actions
        List<String> options = getAvailableActions(p);
        int choice = ui.getPlayerAction(p, options, getAliveEnemies());

        Action action;
        List<Combatant> targets = new ArrayList<>();
        String actionType = options.get(choice);

        switch (actionType) {
            case "Basic Attack":
                action = new BasicAttack();
                Combatant target = ui.selectTarget(getAliveEnemies());
                targets.add(target);
                break;

            case "Defend":
                action = new Defend();
                targets.add(p); // self-target
                break;

            case "Use Item":
                Item selectedItem = ui.selectItem(p.getItems());
                action = new UseItem(selectedItem);
                targets.addAll(getAliveEnemiesAsCombatants());
                break;

            case "Special Skill":
                action = new SpecialSkill();
                if (p.getSpecialSkillName().equals("Shield Bash")) {
                    // Shield Bash targets single enemy
                    Combatant skillTarget = ui.selectTarget(getAliveEnemies());
                    targets.add(skillTarget);
                } else {
                    // Arcane Blast targets all enemies
                    targets.addAll(getAliveEnemiesAsCombatants());
                }
                break;

            default:
                action = new BasicAttack();
                targets.add(getAliveEnemies().get(0));
                break;
        }

        // Execute the action
        String result = action.execute(p, targets);
        ui.displayActionResult(result);
    }

    /**
     * Handle an enemy's turn: decide action, execute.
     */
    private void processEnemyTurn(Enemy enemy) {
        // Enemy always uses BasicAttack
        Action action = enemy.decideAction(Collections.singletonList(player));
        Combatant target = enemy.selectTarget(Collections.singletonList(player));

        List<Combatant> targets = new ArrayList<>();
        targets.add(target);

        String result = action.execute(enemy, targets);
        ui.displayActionResult(result);
    }

    /**
     * Spawn backup enemies when initial wave is fully defeated.
     */
    private void handleBackupSpawn() {
        List<Enemy> backupEnemies = level.getBackupEnemies();
        combatants.addAll(backupEnemies);
        backupSpawned = true;
        ui.displayBackupSpawn(backupEnemies);
    }

    // --- Helper methods ---

    private List<String> getAvailableActions(Player p) {
        List<String> actions = new ArrayList<>();
        actions.add("Basic Attack");
        actions.add("Defend");
        if (p.hasItems()) {
            actions.add("Use Item");
        }
        if (p.isSpecialSkillReady()) {
            actions.add("Special Skill");
        }
        return actions;
    }

    private List<Combatant> getAliveCombatants() {
        List<Combatant> alive = new ArrayList<>();
        for (Combatant c : combatants) {
            if (c.isAlive()) {
                alive.add(c);
            }
        }
        return alive;
    }

    private List<Enemy> getAliveEnemies() {
        List<Enemy> alive = new ArrayList<>();
        for (Combatant c : combatants) {
            if (c instanceof Enemy && c.isAlive()) {
                alive.add((Enemy) c);
            }
        }
        return alive;
    }

    private List<Combatant> getAliveEnemiesAsCombatants() {
        return new ArrayList<>(getAliveEnemies());
    }

    private boolean allInitialEnemiesDefeated() {
        for (Enemy e : level.getInitialEnemies()) {
            if (e.isAlive()) {
                return false;
            }
        }
        return true;
    }

    private boolean isBattleOver() {
        return isPlayerVictory() || isPlayerDefeated();
    }

    private boolean isPlayerVictory() {
        // All enemies (including backup) must be defeated
        if (!backupSpawned && level.hasBackupSpawn()) {
            return false; // Backup hasn't spawned yet
        }
        return getAliveEnemies().isEmpty();
    }

    private boolean isPlayerDefeated() {
        return !player.isAlive();
    }

    public int getRoundCount() {
        return roundCount;
    }
}
