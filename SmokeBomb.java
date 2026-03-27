import java.util.List;
public class SmokeBomb implements Item {
	private static final int DURATION = 2;

	@Override
	public String use(Combatant user, List<Combatant> enemies) {
		System.out.println(user.getName() + "used Smoke Bomb!");
		
		user.addStatusEffect(new SmokeBombEffect(DURATION));
	}

	@Override
	public String getName() {
		return "SmokeBomb";
	}
	
}
