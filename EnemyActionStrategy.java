package com.game.strategy;

import java.util.List;
import com.game.model.action.Action;
import com.game.model.combatant.Combatant;
import com.game.model.combatant.Enemy;

/**
 * Strategy interface for enemy action decisions.
 * Supports DIP: Enemy depends on this abstraction.
 * Supports OCP: new enemy AI behaviours can be added without modifying Enemy.
 */
public interface EnemyActionStrategy {

    /**
     * Decide which action the enemy will take.
     * @param enemy the enemy making the decision
     * @param possibleTargets alive opponents
     * @return the Action to perform
     */
    Action decideAction(Enemy enemy, List<Combatant> possibleTargets);

    /**
     * Select a target from the possible targets.
     * @param possibleTargets alive opponents
     * @return the selected target
     */
    Combatant selectTarget(List<Combatant> possibleTargets);
}
