package com.game.level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.game.model.combatant.Enemy;
import com.game.model.combatant.Goblin;
import com.game.model.combatant.Wolf;
import com.game.strategy.BasicAttackStrategy;
import com.game.strategy.EnemyActionStrategy;

/**
 * Factory class for creating Level objects based on difficulty.
 * Level 1 (Easy):   3 Goblins
 * Level 2 (Medium): 1 Goblin + 1 Wolf | Backup: 2 Wolves
 * Level 3 (Hard):   2 Goblins | Backup: 1 Goblin + 2 Wolves
 */
public class LevelFactory {

    public static Level createLevel(int difficulty) {
        EnemyActionStrategy strategy = new BasicAttackStrategy();

        switch (difficulty) {
            case 1:
                return new Level(1, "Easy",
                        Arrays.asList(
                                new Goblin("Goblin A", strategy),
                                new Goblin("Goblin B", strategy),
                                new Goblin("Goblin C", strategy)
                        ),
                        new ArrayList<>()
                );

            case 2:
                return new Level(2, "Medium",
                        Arrays.asList(
                                new Goblin("Goblin", strategy),
                                new Wolf("Wolf", strategy)
                        ),
                        Arrays.asList(
                                new Wolf("Wolf A", strategy),
                                new Wolf("Wolf B", strategy)
                        )
                );

            case 3:
                return new Level(3, "Hard",
                        Arrays.asList(
                                new Goblin("Goblin A", strategy),
                                new Goblin("Goblin B", strategy)
                        ),
                        Arrays.asList(
                                new Goblin("Goblin C", strategy),
                                new Wolf("Wolf A", strategy),
                                new Wolf("Wolf B", strategy)
                        )
                );

            default:
                throw new IllegalArgumentException("Invalid difficulty level: " + difficulty);
        }
    }
}
