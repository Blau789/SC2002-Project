package com.game.strategy;

import java.util.List;
import com.game.model.combatant.Combatant;

/**
 * Strategy interface for determining turn order each round.
 * Supports DIP: BattleEngine depends on this abstraction.
 * Supports OCP: new turn order mechanisms can be added without modifying BattleEngine.
 */
public interface TurnOrderStrategy {

    /**
     * Determine the order of actions for the given combatants.
     * @param combatants list of alive combatants
     * @return ordered list from first to last
     */
    List<Combatant> determineTurnOrder(List<Combatant> combatants);
}
