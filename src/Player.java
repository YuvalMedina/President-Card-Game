public interface Player {
	
	void deal(Card c);
	// given the previous Card played:
	Card play(Card c) throws CardNotFoundException;
	boolean has3Clubs();
	boolean hasWon();
	String getName();
	void giveNBestCards(Player takes, int n);
	Card giveWorstCard();
	int indexOf(Card c);
}

class CardNotFoundException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}