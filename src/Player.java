public interface Player {
	
	void deal(Card c);
	// given the previous Card played:
	Card play(Card c) throws CardNotFoundException;
	boolean has3Clubs();
	boolean hasWon();
	String getName();
	void giveNBestCards(Player takes, int n);
}

class CardNotFoundException extends Exception{
	
}