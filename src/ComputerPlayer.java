import java.util.ArrayList;
import java.util.List;

public class ComputerPlayer implements Player{
	
	String name;
	private List<Card> myCards = new ArrayList<Card>();
	
	public ComputerPlayer(int i) {
		name = "Auto-Player " + i;
	}

	@Override
	// inserts Card at its correct position in myCards.
	// assert that myCards is always sorted correctly.
	public void deal(Card c) {
		for(int i = 0; i < myCards.size(); i++) {
			if(c.compareTo(myCards.get(i)) < 0) {
				myCards.add(i, c);
				return;
			}
		}
		myCards.add(c);
	}
	
	@Override
	public Card play(Card c) throws CardNotFoundException{
		
		for(int i = 0; i < myCards.size(); i++) {
			if(myCards.get(i).compareTo(c) >= 0) {
				Card toPlay = myCards.get(i);
				// If it's a joker (wild card) we have to choose a value:
				if(toPlay.getRank() == Rank.JOKER) {
					// Let's pick an Ace for maximum value!
					toPlay = new Card(Rank.ACE, Suit.DIAMONDS);
				}
				myCards.remove(i);
				
				return toPlay;
			}
		}
		
		// We throw an error if there exists no card bigger than c
		// in the computer's hand. Essentially tells 'Main' to pass.
		throw(new CardNotFoundException());
	}
	
	public boolean has3Clubs() {
		Card threeClubs = new Card(Rank.THREE, Suit.CLUBS);
		
		if(myCards.get(0).compareTo(threeClubs) == 0) {
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

	public void giveNBestCards(Player takes, int n) {
		// If the Real-Player is taking our cards, we need
		// to ask them which cards they would like:
		if(takes.getClass() == RealPlayer.class) {
			for(int i = 0; i < n; i++) {
				System.out.print(this.name);
				Rank r = Main.promptPlayerExchange();
				Card toGive = new Card(r, Suit.CLUBS);
				
				int rem = this.indexOf(toGive);
				if(rem == -1) {
					System.out.printf("Sorry, %s does not have any "
							+ "cards of that rank.\n",
							this.name);
					i--;
					continue;
				}
				
				takes.deal(myCards.remove(rem));
			}
		}
		// Otherwise, give away our n best cards, by highest rank.
		for(int i = 0; i < n; i++) {
			Rank[] ranks = Rank.values();
			// Start at the highest rank.
			for(int j = ranks.length-1; j >= 0; j--) {
				Card toGive = new Card(ranks[j], Suit.CLUBS);
				int rem = this.indexOf(toGive);
				if(rem == -1) {
					continue;
				}
				
				takes.deal(myCards.remove(rem));
				break;
			}
		}
	}
	
	protected int indexOf(Card c) {
		for(int i = 0; i < myCards.size(); i++) {
			if(myCards.get(i).compareTo(c) == 0) {
				return i;
			}
			if(myCards.get(i).compareTo(c) > 0) {
				return -1;
			}
		}
		return -1;
	}
}
