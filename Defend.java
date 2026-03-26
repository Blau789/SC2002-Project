package action;

import java.util.List;

public class Defend implements Action {

    @Override
    public String execute(Combatant source, List<Combatant> targets) {
        
        // because there is no built-in way to remove it after 2 rounds.
        // Your Combatant class MUST have a method to track temporary status effects.
        
        // Assuming you build a method like this in Combatant:
        // source.addDefenseBuff(10, 2); 
        
        return String.format("%s took a defensive stance! Defense increased by 10 for 2 rounds.", source.getName());
    }

    @Override
    public String getActionName() {
        return "Defend";
    }
}
