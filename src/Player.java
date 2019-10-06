public interface Player {
	
	void deal(Card c);
	// given the previous Card played:
	Card play(Card c) throws CardNotFoundException;
	boolean has3Clubs();
	boolean hasWon();
	String getName();
	
}

class CardNotFoundException extends Exception{
	
}