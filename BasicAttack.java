package action;

import java.util.List;

public class BasicAttack implements Action {

    @Override
    public String execute(Combatant source, List<Combatant> targets) {
        if (targets == null || targets.isEmpty()) {
            return "Attack failed: No target selected.";
        }

        Combatant target = targets.get(0);
        
        //   Damage = max(0, Attacker Attack - Target Defense)
        int rawDamage = source.getAttack() - target.getDefense();
        int finalDamage = Math.max(0, rawDamage); 

        // Apply damage
        int newHp = target.getHealth() - finalDamage;
        
        // Minimum HP post-damage is 0
        target.setHealth(Math.max(0, newHp));

        return String.format("%s used Basic Attack on %s for %d damage!", 
                             source.getName(), target.getName(), finalDamage);
    }

    @Override
    public String getActionName() {
        return "Basic Attack";
    }
}
