import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
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
	static List<String> playerNames = new ArrayList<String>();
	// Uses the static social hierarchy below:
	static String[] hierarchy = {"President", "Vice-President", 
			"Secretary", "Citizen", "Peasant", "Vice-Beggar", "Beggar"};
	
	// This list maps, by index since they will be added in order as
	// they win,  the players to their standing in this round.
	static List<Player> playerStandings = new ArrayList<Player>();
	
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
				if(n < 3 || n > 7) {
					System.out.println("Please enter a number between 3 and 7 players.");
					continue;
				}
				//else:
				System.out.println("Please enter your display-name");
				String name = "";
				while(scan.hasNext()) {
					name = scan.next();
					break;
				}
				
				// Initialize players[] array, and myPlayer:
				players = new Player[n];
				myPlayer = new RealPlayer(name);
				players[0] = myPlayer;
				for(int i = 1; i < players.length; i++) {
					players[i] = new ComputerPlayer(i);
				}
				
				//Initialize our Social Hierarchy, according to n:
				playerNames.add(hierarchy[0]);
				switch(n) {
				case 3: {
					playerNames.add(hierarchy[3]);
				}
				case 4: {
					playerNames.add(hierarchy[1]);
					playerNames.add(hierarchy[5]);
				}
				case 5: {
					playerNames.add(hierarchy[1]);
					playerNames.add(hierarchy[3]);
					playerNames.add(hierarchy[5]);
				}
				case 6: {
					playerNames.add(hierarchy[1]);
					playerNames.add(hierarchy[3]);
					playerNames.add(hierarchy[3]);
					playerNames.add(hierarchy[5]);
				}
				case 7: {
					for(int i = 1; i < 6; i++) {
						playerNames.add(hierarchy[i]);
					}
				}
				}
				playerNames.add(hierarchy[6]);
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
		
		System.out.printf("Round %i begins! Here are the cards you were dealt:\n", round);
		myPlayer.displayCards();
		
		int starts = 0;
		if(round == 1) {
			starts = get3ClubsPlayer();
		}
		else {
			try {
				starts = getIndexOf(playerStandings.get(0));
			} catch (Exception e) {
				System.out.println("Total error. Winning player from last round not found.");
			}
		}
		
		// Reset playerStandings from last round:
		playerStandings.clear();
		// which player is playing:
		int playerNum = starts;
		// start at smallest valued card:
		Card currentCard = base;
		// set number of players that have passed to 0:
		int passed = 0;
		
		while(!roundOver()) {
			
			// checks if this player has already won (lost his hand):
			if(players[playerNum].hasWon()) continue;
			
			// if one of the computer-players starts:
			if(playerNum != 0) {
				System.out.printf("%s's turn.\n", players[playerNum].getName());
				
				// set a timer in between computer plays, so that
				// they're not overly rapid
				try { TimeUnit.MILLISECONDS.sleep(200);}
				catch (InterruptedException e1) { System.out.println("Timer was interrupted."); }
				
				
				try {
					currentCard = players[playerNum].play(currentCard);
				}
				catch(CardNotFoundException e) {
					// We couldn't find a card that's of a bigger rank in our hand
					// so we pass:
					System.out.printf("%s has passed", players[playerNum].getName());
					passed++;
					playerNum = (playerNum + 1) % players.length;
					if(passed >= players.length - 1) {
						currentCard = base;
						// Was it myPlayer's play that caused everyone to pass?
						// If so let's congratulate them personally!
						if(playerNum == 0) {
							System.out.println("Everyone passed your play! You get to start the next play.");
						}
						else {
							System.out.printf("Everyone passed %s's play. They get to start the next play.\n", players[playerNum].getName());
						}
					}
					continue;
				}
				
				// if Computer has not passed, it plays:
				System.out.printf("%s has played %s\n", players[playerNum].getName(), currentCard.toString());
				if(currentCard.getRank().equals(Rank.TWO)) {
					System.out.println("The current stack of cards is ~burned~!");
					currentCard = base;
				}
				
				// checks if the computer has won, if it has, print prompt:
				updateStanding(players[playerNum]);
				
				playerNum = (playerNum + 1) % players.length;
				// reset passed count:
				passed = 0;
				continue;
			}
			else {
				Card c = promptRealPlayer();
				// myPlayer has passed their turn:
				if(c == null) {
					System.out.println("You passed.");
					playerNum = (playerNum + 1) % players.length;
					passed++;
					if(passed >= players.length - 1) {
						currentCard = base;
						System.out.printf("Everyone passed %s's play. They get to start the next play.\n", players[playerNum].getName());
					}
					continue;
				}
				// else:
				currentCard = c;
				System.out.printf("You played %s. Your current hand:\n", currentCard.toString());
				if(currentCard.getRank().equals(Rank.TWO)) {
					System.out.println("The current stack of cards is ~burned~!");
					currentCard = base;
				}
				// reset passed count:
				passed = 0;
				playerNum = (playerNum + 1) % players.length;
				myPlayer.displayCards();
			}
		}
		
		// Round is over! Let's see if we want to continue to the next game.
		System.out.printf("The round is over and %s is crowned President.\n",
				playerStandings.get(0).getName());
		System.out.println("Do you want to continue to the next round?");
		Scanner scan = new Scanner(System.in);
		while(scan.hasNext()) {
			if(scan.next() == "yes") {
				return true;
			}
			else if(scan.next() == "no") {
				return false;
			}
		}
		
		return false;
	}
	
	public static void updateStanding(Player p) {
		if(p.hasWon()) {
			playerStandings.add(p);
			System.out.printf("%s has run out of cards and is crowned %s",
					p.getName(),
					playerNames.get(playerStandings.indexOf(p)));
		}
	}
	
	public static Card promptRealPlayer() {
		System.out.println("It's your turn!");
		System.out.print("Please enter the value (rank) of the card you wish to play. ");
		System.out.print("You may use any format (alphabetic or numeric). Type 'peek' ");
		System.out.println("to take a look at your current hand.");
		System.out.println("If you wish to pass, type 'pass'");
		
		Scanner scan = new Scanner(System.in);
		while(scan.hasNext()) {
			String token = scan.next();
			if(token.equals("peek")) {
				myPlayer.displayCards();
				continue;
			}
			if(token.equals("pass")) {
				return null;
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
					System.out.println("A card of this rank was not found in your hand.");
					System.out.println("If you want to take a look at your current hand, type 'peek'.");
					continue;
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
	
	// Gets the player who has 3 of clubs
	public static int get3ClubsPlayer() {
		// assert from beginning, 3 of Clubs is the first card
		// in any player's deck
		for(int i = 0; i < players.length; i++) {
			if(players[i].has3Clubs()) {
				return i;
			}
		}
		// assert, should not get here! One player has 3 of clubs.
		return -1;
	}
	
	// Gets the player's index in players[] array:
	public static int getIndexOf(Player p) throws Exception{
		for(int i = 0; i < players.length; i++) {
			if(players[i] == p) {
				return i;
			}
		}
		
		throw(new Exception());
	}
}
