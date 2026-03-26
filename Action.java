

package action;

import java.util.List;

public interface Action {
    String execute(Combatant source, List<Combatant> targets);
    String getActionName();
}

