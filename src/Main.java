/*
 * Author: Yuval Medina
 * 10/06/19
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
	
	static final File GAME_RULES = new File("Game_Rules");
	
	// The smallest valued card:
	static final Card BASE = new Card(Rank.THREE, Suit.CLUBS);
	
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
	
	// playerStandings lists all the players by order of their win's,
	// since they will be added in order, by 'updateStandings()' as 
	// they win
	static List<Player> playerStandings = new ArrayList<Player>();
	
	// lists all the players who are still in the game.
	// players are removed by 'updateStandings()' as well
	static List<Player> leftStanding = new ArrayList<Player>();
	
	// Array of all the players
	static Player[] players = null;
	// Our player.
	static RealPlayer myPlayer = null;
	
	static Scanner scan = new Scanner(System.in);

	public static void main(String[] args) {
		System.out.println("Welcome to President");
		System.out.print("For Rules, type 'help'. ");
		System.out.print("Otherwise, begin by entering the ");
		System.out.println("number of players you wish to compete against.");
		
		while(true) {
			if(scan.hasNextInt()) {
				int n = scan.nextInt();
				if(n < 2 || n > 6) {
					System.out.println("Please enter a number between 2 and 6 players.");
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
				// n+1 long, consisting of n auto-players and 1 real-player
				n = n + 1;
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
					break;
				}
				case 4: {
					playerNames.add(hierarchy[1]);
					playerNames.add(hierarchy[5]);
					break;
				}
				case 5: {
					playerNames.add(hierarchy[1]);
					playerNames.add(hierarchy[3]);
					playerNames.add(hierarchy[5]);
					break;
				}
				case 6: {
					playerNames.add(hierarchy[1]);
					playerNames.add(hierarchy[3]);
					playerNames.add(hierarchy[3]);
					playerNames.add(hierarchy[5]);
					break;
				}
				case 7: {
					for(int i = 1; i < 6; i++) {
						playerNames.add(hierarchy[i]);
					}
					break;
				}
				}
				playerNames.add(hierarchy[6]);
				break;
			}
			
			String token = scan.next();
			if(token.equals("help")) {
				try {
					Scanner reader = new Scanner(GAME_RULES);
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
		String token = scan.next();
		while(true) {
			if(token.equals("start")) {
				if(!startRound()) {
					break;
				}
				System.out.println("Type 'start' to begin game");
				token = scan.next();
				continue;
			}
		}
		scan.close();
		return;
	}
	
	// Returns true if, at the close of thise round, the player
	// wishes to continue to the next round. Otherwise, false.
	public static boolean startRound() {
		Deck myDeck = new Deck();
		myDeck.deal(players);
		
		System.out.printf("Round %d begins!\n", round);
		myPlayer.displayCards();
		
		int starts = 0;
		if(round == 1) {
			starts = get3ClubsPlayer();
		}
		
		// If we are past round 1, the President gets to take the
		// beggar's best two cards, the vice-president the vice-
		// beggar's one best card.
		if(round > 1) {
			exchangeCards();
			// Add all players to leftStanding, by order of their
			// social standing:
			leftStanding.addAll(playerStandings);
		}
		else {
			// Add all players, as initially ordered, to leftStanding.
			leftStanding.clear();
			for(Player p : players) leftStanding.add(p);
		}
		
		// Reset playerStandings from last round:
		playerStandings.clear();
		
		// which player is playing:
		int playerNum = starts;
		// start at smallest valued card:
		Card currentCard = BASE;
		// set number of players that have passed to 0:
		int passed = 0;
		
		while(leftStanding.size() >= 2) {
			
			Player currentPlayer = leftStanding.get(playerNum);
			
			// if one of the computer-players starts:
			if(currentPlayer != myPlayer) {
				System.out.printf("%s's turn.\n", 
						currentPlayer.getName());
				
				// set a timer in between computer plays, so that
				// they're not overly rapid
				try { TimeUnit.MILLISECONDS.sleep(700);}
				catch (InterruptedException e1) { System.out.println("Timer was interrupted."); }
				
				
				try {
					currentCard = currentPlayer.play(currentCard);
				}
				catch(CardNotFoundException e) {
					// We couldn't find a card that's of a bigger rank in our hand
					// so we pass:
					System.out.printf("%s has passed\n", 
							currentPlayer.getName());
					passed++;
					playerNum = (playerNum + 1) % leftStanding.size();
					if(passed >= players.length - 1) {
						currentCard = BASE;
						// Was it myPlayer's play that caused everyone to pass?
						// If so let's congratulate them personally!
						if(playerNum == 0) {
							System.out.println("Everyone passed your "
									+ "play! You get to start the next "
									+ "play.");
						}
						else {
							System.out.printf("Everyone passed %s's "
									+ "play. They get to start the next "
									+ "play.\n", 
									leftStanding.get(playerNum).getName());
						}
					}
					continue;
				}
				
				// if Computer has not passed, it plays:
				System.out.printf("%s has played %s\n", 
						currentPlayer.getName(), currentCard.toString());
				
				// checks if 'two' was played. If so, the current play is burned,
				// and the player gets to start the next play.
				if(currentCard.getRank().equals(Rank.TWO)) {
					System.out.println("The current stack of cards is ~burned~!");
					currentCard = BASE;
					// reset passed count:
					passed = 0;
					// checks if the computer has won, if it has, 
					// print prompt, and return true
					if(updateStanding(currentPlayer)) {
						playerNum = playerNum % leftStanding.size();
					}
					continue;
				}
				else {
					// checks if the computer has won, if it has, 
					// print prompt, and return true
					if(!updateStanding(currentPlayer)) {
						// increment playerNum:
						playerNum = (playerNum + 1) % leftStanding.size();
					}
					else playerNum = playerNum % leftStanding.size();
					// reset passed count:
					passed = 0;
					continue;
				}
			}
			// Real player's (myPlayer's) turn:
			else {
				Card c = promptRealPlayer(currentCard);
				// myPlayer has passed their turn:
				if(c == null) {
					System.out.println("You passed.");
					playerNum = (playerNum + 1) % leftStanding.size();
					passed++;
					if(passed >= leftStanding.size() - 1) {
						currentCard = BASE;
						System.out.printf("Everyone passed %s's play. "
								+ "They get to start the next play.\n", 
								leftStanding.get(playerNum).getName());
					}
					continue;
				}
				// else:
				currentCard = c;
				System.out.printf("You played %s.\n", 
						currentCard.toString());
				if(currentCard.getRank().equals(Rank.TWO)) {
					System.out.println("The current stack of cards is ~burned~!");
					// reset currentCard:
					currentCard = BASE;
					// playerNum stays the same, that is we begin next play.
					// unless we won:
					if(updateStanding(myPlayer)) {
						playerNum = playerNum % leftStanding.size();
					}
				}
				else {
					if(updateStanding(myPlayer)) {
						playerNum = playerNum % leftStanding.size();
					}
					else playerNum = (playerNum + 1) % leftStanding.size();
				}
				
				// reset passed count:
				passed = 0;
				
				try { TimeUnit.MILLISECONDS.sleep(500);}
				catch (InterruptedException e1) { System.out.println("Timer was interrupted."); }
			}
		}
		
		// Round is over!
		System.out.printf("The round is over and %s is crowned President.\n",
				playerStandings.get(0).getName());
		// Increment round count:
		round++;
		Player loser = leftStanding.get(0);
		leftStanding.remove(loser);
		playerStandings.add(loser);
		
		System.out.println("Do you want to continue to the next round? (yes/no)");
		String token = scan.next();
		while(true) {
			if(token.equals("yes")) {
				return true;
			}
			else if(token.equals("no")) {
				return false;
			}
			token = scan.next();
		}
	}
	
	// The card-exchanges that happen after round1:
	public static void exchangeCards() {
		// President exchanges w/ beggar:
		Player president = playerStandings.get(0);
		Player beggar = playerStandings.get(players.length-1);
		if(president == myPlayer) {
			System.out.printf("Congratulations! As president you may "
					+ "take %s's two best cards away\n",
					beggar.getName());
		}
		beggar.giveNBestCards(president, 2);
		
		// Check to see if we have a vice-president:
		if(players.length > 3) {
			Player vicePres = playerStandings.get(1);
			Player viceBeggar = playerStandings.get(players.length-2);
			if(vicePres == myPlayer) {
				System.out.printf("Congratulations! As vice-president "
						+ "you may take %s's best card away\n",
						viceBeggar.getName());
			}
			viceBeggar.giveNBestCards(vicePres, 1);
		}
	}
	
	// if our player is crowned president or vice-president,
	// they get to choose the value of the card they want to take away.
	public static Rank promptPlayerExchange() {
		System.out.println(": 'Which value card do you want?'");
		String token = scan.next();
		while(true) {
			try {
				return deriveRank(token);
			}
			catch(IllegalArgumentException e) {
				token = scan.next();
			}
		}
	}
	
	// if our player is crowned president or vice-president,
	// they'll also need to pick the worst cards to give back to the
	// beggar / vice-beggar
	public static Rank promptPlayerForWorstCard() {
		System.out.println("Please enter any card you wish to give to ");
		System.out.println("the beggar/vice-beggar in exchange for their ");
		System.out.println("best card.");
		System.out.println("Type 'peek' to take a look at your current hand.");
		
		String token = scan.next();
		while(true) {
			
			if(token.equals("peek")) {
				myPlayer.displayCards();
				token = scan.next();
				continue;
			}
			
			else {
				Rank r = null;
				try { r = deriveRank(token); }
				catch(IllegalArgumentException e) {
					token = scan.next();
					continue;
				}
				
				return r;
			}
		}
	}
	
	// update the players's current standings
	// if they won, remove player from leftStanding and add them
	// to playerStandings.
	public static boolean updateStanding(Player p) {
		if(p.hasWon()) {
			playerStandings.add(p);
			leftStanding.remove(p);
			System.out.printf("%s has run out of cards and is crowned %s.\n",
					p.getName(),
					playerNames.get(playerStandings.indexOf(p)));
			return true;
		}
		return false;
	}
	
	public static Card promptRealPlayer(Card currentCard) {
		System.out.println("It's your turn!");
		System.out.println("Please enter the value (rank) of the card you wish to play.");
		System.out.println("You may use any format (alphabetic or numeric). Type 'peek' ");
		System.out.println("to take a look at your current hand.");
		System.out.println("If you wish to pass, type 'pass'.");
		
		Card toPlay = null;
		String token = scan.next();
		while(true) {
			
			if(token.equals("peek")) {
				myPlayer.displayCards();
				token = scan.next();
				continue;
			}
			
			if(token.equals("pass")) {
				return null;
			}
			
			else {
				Rank r = null;
				try { r = deriveRank(token); }
				catch(IllegalArgumentException e) {
					token = scan.next();
					continue;
				}
				
				Card ret = null;
				try {
					toPlay = new Card(r, Suit.CLUBS);
					if(currentCard.compareTo(toPlay) > 0) {
						throw(new IllegalPlayException());
					}
					// else:
					ret = myPlayer.play(toPlay);
				}
				catch(CardNotFoundException e) {
					System.out.println("A card of this rank was not found in your hand.");
					System.out.println("If you want to take a look at your current hand, type 'peek'.");
					token = scan.next();
					continue;
				}
				catch(IllegalPlayException e) {
					System.out.printf("You must play a card equal to "
							+ "or higher in rank than %s\n", currentCard.toString());
					System.out.println("If you can't, type 'pass'.");
					token = scan.next();
					continue;
				}
				
				// If we play a joker, we have to assign a value to it
				// (wild-card situation).
				if(ret.getRank() == Rank.JOKER) {
					System.out.println("What value (Rank) would you like to "
							+ "assign to your Joker (wild-card)?");
					while(true) {
						token = scan.next();
						try {
							r = deriveRank(token);
						}
						catch(IllegalArgumentException e) {
							token = scan.next();
							continue;
						}
						if(r == Rank.TWO || r == Rank.JOKER) {
							System.out.println("You may only assign Ranks "
									+ "3 through Ace to your wild-card.");
							token = scan.next();
							continue;
						}
						
						ret = new Card(r, Suit.DIAMONDS);
						break;
					}
				}
				return ret;
			}
		}
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

class IllegalPlayException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}
