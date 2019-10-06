import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
			if(myCards.get(i).compareTo(c) == 0) {
				return myCards.remove(i);
			}
		}
		
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

	public void displayCards() {
		System.out.println("Here are your cards:");
		for(Card c : myCards) {
			System.out.println(c.toString());
		}
		System.out.println();
	}
	
	public void giveNBestCards(Player takes, int n) {
		for(int i = 0; i < n; i++) {
			Rank[] ranks = Rank.values();
			// Start at the highest rank.
			for(int j = ranks.length-1; j >= 0; j--) {
				Card toGive = new Card(ranks[j], Suit.CLUBS);
				int rem = this.indexOf(toGive);
				if(rem == -1) {
					continue;
				}
				
				toGive = myCards.remove(rem);
				takes.deal(toGive);
				Card worst = takes.giveWorstCard();
				this.deal(worst);
				
				System.out.printf("~~~%s takes your %s.~~~~ :(\n",
						takes.getName(), toGive.toString());
				System.out.printf("You get back %s from %s\n", 
						worst.toString(), takes.getName());
				try { TimeUnit.MILLISECONDS.sleep(700); }
				catch (InterruptedException e) { System.out.println("Timer interrupted."); }
				
				break;
			}
		}
	}
	
	public Card giveWorstCard() {
		while(true) {
			Rank r = Main.promptPlayerForWorstCard();
			Card worst = new Card(r, Suit.CLUBS);
			
			int rem = this.indexOf(worst);
			if(rem == -1) {
				System.out.println("You don't have any cards of that rank.");
				continue;
			}
			
			return myCards.remove(rem);
		}
	}
	
	public int indexOf(Card c) {
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
