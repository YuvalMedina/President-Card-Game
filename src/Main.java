import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
	
	// The smallest valued card:
	static final Card base = new Card(Rank.THREE, Suit.CLUBS);
	
	static int round = 1;
	// Map, mapping player positions to their names, aka:
	// 1 (first player to go) ––> "President"
	// 2 (second player to go)  ––> "Vice-President"
	// .... etc.
	// 7 (last player to go) ––> "Beggar"
	static Map<Integer, String> playerNames = new HashMap<Integer, String>();
	
	// This map maps player positions to their index in 'players' array
	static Map<Integer, Integer> playerPositions = new HashMap<Integer, Integer>();
	
	// Array of all the players
	static Player[] players = null;
	// Our player.
	static RealPlayer myPlayer = null;

	public static void main(String[] args) {
		System.out.println("Welcome to President");
		System.out.print("For Rules, type 'help'. ");
		System.out.print("Otherwise, begin by entering the ");
		System.out.println("number of players you wish to compete against.");
		
		Scanner scan = new Scanner(System.in);
		Player[] players = null;
		RealPlayer myPlayer = null;
		while(scan.hasNext()) {
			if(scan.hasNextInt()) {
				int n = scan.nextInt();
				if(n < 2 || n > 7) {
					System.out.println("Please enter a number between 2 and 7 players.");
					continue;
				}
				//else:
				System.out.println("Please enter your display-name");
				String name = "";
				while(scan.hasNext()) {
					name = scan.next();
					break;
				}
				
				players = new Player[n];
				myPlayer = new RealPlayer(name);
				players[0] = myPlayer;
				for(int i = 1; i < players.length; i++) {
					players[i] = new ComputerPlayer(i);
				}
				break;
			}
			
			String input = scan.next();
			if(input.equals("help")) {
				try {
					Scanner reader = new Scanner(new File("Game_Rules.txt"));
					while(reader.hasNext()) {
						System.out.println(reader.nextLine());
					}
					reader.close();
				} catch (FileNotFoundException e) {
					System.out.println("Oop, could not find game rules ??");
				}
				continue;
			}
		}
		
		System.out.println("Type 'start' to begin game");
		while(scan.hasNext("start")) {
			if(!startRound()) {
				break;
			}
		}
	}
	
	// Returns true if, at the close of thise round, the player
	// wishes to continue to the next round. Otherwise, false.
	public static boolean startRound() {
		Deck myDeck = new Deck();
		myDeck.deal(players);
		
		System.out.printf("Round %d begins! Here are the cards you were dealt:\n", round);
		myPlayer.displayCards();
		
		int starts = getStartingPlayerIndex(players);
		
		// which player is playing:
		int playerNum = starts;
		// start at smallest valued card:
		Card currentCard = base;
		while(!roundOver()) {
			// if one of the computer-players starts:
			if(playerNum != 0) {
				System.out.printf("%s's turn.\n", players[playerNum].getName());
				// set a timer in between computer plays, so that
				// they're not overly rapid
				try { TimeUnit.MILLISECONDS.sleep(200);}
				catch (InterruptedException e1) { System.out.println("Timer was interrupted.") }
				
				try {
					currentCard = players[playerNum].play(currentCard);
				}
				catch(CardNotFoundException e) {
					// We pass!
					System.out.printf("%s has passed", players[playerNum].getName());
					playerNum = (playerNum + 1) % players.length;
					// TODO: all players pass situation
					starts = (playerNum - 1) % players.length;
					continue;
				}
				
				System.out.printf("%s has played %s\n", players[playerNum].getName(), currentCard.toString());
				if(currentCard.getRank().equals(Rank.TWO)) {
					System.out.println("The current stack of cards is ~burned~!");
					currentCard = base;
				}
				
				playerNum = (playerNum + 1) % players.length;
				continue;
			}
			else {
				currentCard = promptRealPlayer();
				System.out.printf("You played %s. Your current hand:", currentCard.toString());
				myPlayer.displayCards();
			}
		}
	}
	
	public static Card promptRealPlayer() {
		System.out.println("It's your turn!");
		System.out.print("Please enter the value (rank) of the card you wish to play. ");
		System.out.print("You may use any format (alphabetic or numeric). Type 'peek' ");
		System.out.print("to take a look at your current hand.");
		
		Scanner scan = new Scanner(System.in);
		while(scan.hasNext()) {
			String token = scan.next();
			if(token.equals("peek")) {
				myPlayer.displayCards();
				continue;
			}
			else {
				Rank r = null;
				try {
					r = deriveRank(token);
				}
				catch(IllegalArgumentException e) {continue;}
				
				try {
					return myPlayer.play(new Card(r, Suit.SPADES));
				}
				catch(CardNotFoundException e) {
					System.out.println("A card of this rank was not found in your hand");
				}
			}
		}
		scan.close();
		
		// assert, should not reach this point.
		return null;
	}
	
	public static Rank deriveRank(String token) throws IllegalArgumentException {
		try {
			int i = Integer.parseInt(token);
			switch(i) {
			case 2 : return Rank.TWO;
			case 3 : return Rank.THREE;
			case 4 : return Rank.FOUR;
			case 5 : return Rank.FIVE;
			case 6 : return Rank.SIX;
			case 7 : return Rank.SEVEN;
			case 8 : return Rank.EIGHT;
			case 9 : return Rank.NINE;
			case 10 : return Rank.TEN;
			default : {
				System.out.println("That number does not correspond to a real rank.");
				throw new IllegalArgumentException();
			}
			}
		}
		catch(NumberFormatException e) {};
		
		switch(token.toLowerCase()) {
		case "two" : return Rank.TWO;
		case "three" : return Rank.THREE;
		case "four" : return Rank.FOUR;
		case "five" : return Rank.FIVE;
		case "six" : return Rank.SIX;
		case "seven" : return Rank.SEVEN;
		case "eight" : return Rank.EIGHT;
		case "nine" : return Rank.NINE;
		case "ten" : return Rank.TEN;
		case "jack" : return Rank.JACK;
		case "queen" : return Rank.QUEEN;
		case "king" : return Rank.KING;
		case "ace" : return Rank.ACE;
		case "joker" : return Rank.JOKER;
		default : {
			System.out.println("That name does not correspond to a real rank.");
			throw new IllegalArgumentException();
		}
		}
	}
	
	// Checks if roundOver. Round is over if only one player has cards.
	public static boolean roundOver() {
		int leftStanding = 0;
		for(int i = 0; i < players.length; i++) {
			if(!players[i].hasWon()) {
				leftStanding++;
			}
		}
		
		return leftStanding < 2;
	}
	
	public static int getStartingPlayerIndex(Player[] players) {
		if(round == 1) {
			// assert from beginning, 3 of Clubs is the first card
			// in any player's deck
			for(int i = 0; i < players.length; i++) {
				if(players[i].has3Clubs()) {
					return i;
				}
			}
			
			// assert, should not get here!
			return -1;
		}
		else {
			return playerPositions.get(1);
		}
	}
}
