
public class Card implements Comparable<Card>{
	
	private Rank r;
	private Suit s;
	
	public Card(Rank r, Suit s) {
		this.r = r;
		
		// a joker can't have a suit!
		if(this.r.value != 50) {
			this.s = s;
		}
	}

	@Override
	public int compareTo(Card o) {
		// 3 of clubs is the first card in the first round
		// we want it to always be first – can only be equal (0)
		// or less-than (-1)
		if(this.r.value == 3 && this.s.equals(Suit.CLUBS)) {
			if(o.r.value == 3 && o.s.equals(Suit.CLUBS)) {
				return 0;
			}
			return -1;
		}
		
		if(this.r.value < o.r.value) {
			return -1;
		}
		if(this.r.value > o.r.value) {
			return 1;
		}
		return 0;
	}
	
	public int value(){
		return r.value;
	}
	
	@Override
	public String toString() {
		if(this.r.value == 50) {
			return "Joker";
		}
		
		String ret = "";
		switch(this.r) {
		case TWO : ret += "Two";
		case THREE : ret += "Three";
		case FOUR : ret += "Four";
		case FIVE : ret += "Five";
		case SIX : ret += "Six";
		case SEVEN : ret += "Seven";
		case EIGHT : ret += "Eight";
		case NINE : ret += "Nine";
		case TEN : ret += "Ten";
		case JACK : ret += "Jack";
		case QUEEN : ret += "Queen";
		case KING : ret += "King";
		case ACE : ret += "Ace";
		}
		
		if(this.s.equals(Suit.CLUBS)) ret += " of Clubs";
		else if(this.s.equals(Suit.SPADES)) ret += " of Spades";
		else if(this.s.equals(Suit.HEARTS)) ret += " of Hearts";
		else ret += " of Diamonds";
		
		return ret;
	}
	
	public Rank getRank() {
		return this.r;
	}
	
	public Suit getSuit() {
		return this.s;
	}
}
