package action;

import java.util.List;

public abstract class SpecialSkill implements Action {
    
    protected int currentCooldown;
    protected String skillName;

    public SpecialSkill(String skillName) {
        this.skillName = skillName;
        this.currentCooldown = 0;
    }

    // The Engine MUST call this ONLY when the combatant actively takes a turn.
    public void reduceCooldown() {
        if (currentCooldown > 0) {
            currentCooldown--;
        }
    }

    @Override
    public String execute(Combatant source, List<Combatant> targets) {
        if (currentCooldown > 0) {
            return String.format("%s is on cooldown for %d more active turns!", skillName, currentCooldown);
        }

       
        // If they cast it now, it should be unavailable for their NEXT 2 turns, 
        // and available on the 3rd turn after casting.
        this.currentCooldown = 3; 

        return performSkillEffect(source, targets);
    }

    // Child classes implement their specific damage/effects here
    protected abstract String performSkillEffect(Combatant source, List<Combatant> targets);

    @Override
    public String getActionName() {
        return this.skillName;
    }
}