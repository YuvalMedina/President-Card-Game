import java.util.concurrent.ThreadLocalRandom;

public class Deck {
	
	// a standard deck + 2 jokers
	public static int DECKSIZE = 54;
	public Card[] cards = new Card[DECKSIZE];

	public Deck() {
		int i = 0;
		for(Rank r : Rank.values()) {
			// jokers have no need for a suit
			if(r.value == 50) {
				cards[i] = new Card(r, null);
				cards[i+1] = new Card(r, null);
				i += 2;
				continue;
			}
			// else if rank is not a joker:
			for(Suit s : Suit.values()) {
				cards[i] = new Card(r, s);
				i++;
			}
		}
	}
	
	// deal each player roughly DECKSIZE / n cards, after shuffling.
	// the players at the start of the array 'players' will always
	// get more cards if DECKSIZE % n != 0 – that is they get the
	// remainder of the cards by default
	public void deal(Player[] players){
		int n = players.length;
		shuffle(cards);
		
		int i = 0;
		while(i < DECKSIZE) {
			players[i % n].deal(cards[i]);
			i++;
		}
	}
	
	public Card[] getCards() {
		return cards;
	}
	
	
	public static void shuffle(Card[] cards) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		
		for(int i = 0; i < cards.length-1; i++) {
			int index = random.nextInt(i, cards.length);
			
			Card temp = cards[index];
			cards[index] = cards[i];
			cards[i] = temp;
		}
	}
}