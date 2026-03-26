package com.game.engine;

import com.game.model.combatant.Combatant;
import com.game.model.effect.SmokeBombEffect;
import com.game.model.effect.StatusEffect;
import com.game.model.effect.StunEffect;

/**
 * Manages status effects: applying, ticking, and removing expired effects.
 * SRP: solely responsible for status effect lifecycle.
 */
public class StatusEffectManager {

    /**
     * Tick all active effects on a combatant (decrement durations).
     */
    public void tickEffects(Combatant combatant) {
        for (StatusEffect effect : combatant.getStatusEffects()) {
            effect.tick();
        }
    }

    /**
     * Remove all expired effects from a combatant.
     */
    public void removeExpiredEffects(Combatant combatant) {
        combatant.removeExpiredEffects();
    }

    /**
     * Add a new status effect to a combatant.
     */
    public void addEffect(Combatant combatant, StatusEffect effect) {
        combatant.addStatusEffect(effect);
        effect.apply(combatant);
    }

    /**
     * Check if the combatant is currently stunned.
     */
    public boolean isStunned(Combatant combatant) {
        for (StatusEffect effect : combatant.getStatusEffects()) {
            if (effect instanceof StunEffect && !effect.isExpired()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a combatant has an active Smoke Bomb effect.
     */
    public boolean hasSmokeBombActive(Combatant combatant) {
        for (StatusEffect effect : combatant.getStatusEffects()) {
            if (effect instanceof SmokeBombEffect && !effect.isExpired()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get a summary of active effects on a combatant.
     */
    public String getEffectsSummary(Combatant combatant) {
        StringBuilder sb = new StringBuilder();
        for (StatusEffect effect : combatant.getStatusEffects()) {
            if (!effect.isExpired()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(effect.getName())
                  .append(" (").append(effect.getRemainingDuration()).append(" turns)");
            }
        }
        return sb.length() > 0 ? sb.toString() : "None";
    }
}
