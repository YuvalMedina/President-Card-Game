import java.util.ArrayList;
import java.util.List;

public class RealPlayer implements Player{

	String name;
	private List<Card> myCards = new ArrayList<Card>();
	
	public RealPlayer(String name) {
		this.name = name;
	}
	
	@Override
	// inserts Card at its correct position in myCards.
	// assert that myCards is always sorted correctly.
	public void deal(Card c) {
		for(int i = 0; i < myCards.size(); i++) {
			if(c.compareTo(myCards.get(i)) > 0) {
				myCards.add(i, c);
				return;
			}
		}
		myCards.add(c);
	}

	@Override
	public Card play(Card c) throws CardNotFoundException{
		
		for(int i = 0; i < myCards.size(); i++) {
			if(myCards.get(i).compareTo(c) == 0) {
				return myCards.remove(i);
			}
		}
		
		throw(new CardNotFoundException());
	}
	
	public boolean has3Clubs() {
		Card threeClubs = new Card(Rank.THREE, Suit.CLUBS);
		
		if(myCards.get(0).equals(threeClubs)) {
			return true;
		}
		
		return false;
	}
	
	public boolean hasWon() {
		if(myCards.size() == 0) {
			return true;
		}
		return false;
	}
	
	public String getName() {
		return this.name;
	}

	public void displayCards() {
		System.out.println("Here are your cards:");
		for(Card c : myCards) {
			System.out.println(c.toString());
		}
		System.out.println();
	}
}
