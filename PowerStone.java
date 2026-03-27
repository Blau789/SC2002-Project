import java.util.List;
public class PowerStone implements Item{
	@Override
	public String use(Combatant user, List<Combatant> enemies) {
		System.out.println(user.getName() + "used Power Stone!");
		
		//Trigger special skill without cooldown
		user.useSpecialSkill(enemies,false);
	}

	@Override
	public String getName() {
		return "Power Stone";
	}
}