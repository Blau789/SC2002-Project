
package action;

import java.util.List;

public class UseItem implements Action {

    private Items itemToUse; 
    
    public UseItem(Items itemToUse) {
        this.itemToUse = itemToUse;
    }

    @Override
    public String execute(Combatant source, List<Combatant> targets) {
        if (this.itemToUse == null) {
            return "No item selected!";
        }
        
        // The item itself should contain the logic of what it does to the target
        return itemToUse.applyEffect(source, targets);
    }

    @Override
    public String getActionName() {
        return "Use " + itemToUse.getName();
    }
}
