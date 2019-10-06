// in president, cards THREE to ACE all have normal, ascending values.
// JOKER may have any value chosen by player.
// TWO is valued as the best card, and can end any round.
public enum Rank{
	THREE (3), FOUR (4), FIVE (5),
	SIX (6), SEVEN (7), EIGHT (8), NINE (9),
	TEN (10), JACK (11), QUEEN (12), KING (13),
	ACE (14), JOKER (50), TWO (100);
	
	int value;
	
	private Rank(int value){
		this.value = value;
	}
	
	protected int value() {
		return this.value;
	}
}