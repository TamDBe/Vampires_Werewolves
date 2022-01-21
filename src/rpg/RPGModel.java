package rpg;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Random; 

/**
 * This class holds the model for the game, including the board, obstacles, and characters, as well as getters 
 * and setters. Is observable, observed by the view.
 *
 * date: 5/05/21
 */
@SuppressWarnings("deprecation")
public class RPGModel extends Observable {
	private static final int ROWS = 20; //row dimension of the grid
	private static final int COLS = 20; //column dimension of the grid
	// store map representation as 2d array
	private Character[][] map;
	private Character[] characters; //store references to characters
	
	// fields to store number of user's and AI's players left on map
	private int nbrOfUser;
	private int nbrOfAI;
	
	/**
	 * No-argument constructor that creates the array representation of the map 
	 */
	public RPGModel() {
		// initiate representation of map that is 20 row x 20 columns
		map = new Character[ROWS][COLS];
		// initialize number of characters to 20 to start (MAY CHANGE)
		nbrOfUser = 5;
		nbrOfAI = 5;
		
		// put starting characters on map
		characters = new Character[nbrOfUser + nbrOfAI];//array, length total num of characters
		//create player characters
		for (int i=0; i<nbrOfUser; i++) {
			characters[i] = new Character(100, 5, 10, "player");
		}
		//create AI characters
		for (int i=nbrOfUser; i<nbrOfUser+nbrOfAI; i++) {
			characters[i] = new Character(100, 5, 10, "AI");
		}
		//put player characters on map
		setCharacterAt(characters[0], 0, 0);
		setCharacterAt(characters[1], 0, 2);
		setCharacterAt(characters[2], 2, 0);
		setCharacterAt(characters[3], 2, 3);
		setCharacterAt(characters[4], 4, 1);
		//put AI characters on map
		setCharacterAt(characters[5], 19, 19);
		setCharacterAt(characters[6], 19, 17);
		setCharacterAt(characters[7], 17, 19);
		setCharacterAt(characters[8], 17, 16);
		setCharacterAt(characters[9], 15, 18);
		placeMountains(); 
		placeWater();
	}
	
	/**
	 * Places the mountain obstacles randomly on the middle of the map
	 */
	private void placeMountains() { 
		Random rand = new Random();
		ArrayList<Integer> emptyCols = new ArrayList<Integer>();
		for (int e = 4; e <= 15; e++) { 
			emptyCols.add(e); 
		} 
		int[] mountains = {4, 3, 2};
		for (int mSize : mountains) { 
			int j = emptyCols.get(rand.nextInt(emptyCols.size())); 
			for (int a = j; a <= j + mSize; a++) { 
				map[a][j] = new Character(0, 0, 0, "mountain");
			}
			emptyCols.remove(emptyCols.indexOf(j)); 
		}
	}
	
	/**
	 * Places the water obstacles randomly on the middle of the map
	 * 	  
	 */
	private void placeWater() { 
		Random rand = new Random();
		ArrayList<Integer> emptyCols = new ArrayList<Integer>();
		for (int e = 4; e <= 15; e++) { 
			emptyCols.add(e); 
		} 
		int[] waters = {1, 1, 2, 2};
		int y = ROWS - 1; 
		for(int wSize: waters) { 
			if (wSize == 1) { 
				int j = emptyCols.get(rand.nextInt(emptyCols.size())); 
				int i = rand.nextInt(y - wSize);
				Character a = map[i][j]; 
				if (a == null) { 
					map[i][j] = new Character(0, 0, 0, "water");
				} 	
			} else { 
				boolean goodSpot = false; 
				while (goodSpot != true) { 
					int j = emptyCols.get(rand.nextInt(emptyCols.size())); 
					int i = rand.nextInt(y - wSize);
					Character a = map[i][j]; 
					Character b = map[i][j+1];
					Character c = map[i+1][j];
					Character d = map[i+1][j+1];
					if (a == null && b == null && c == null && d == null) {
						goodSpot = true; 
						map[i][j] = new Character(0, 0, 0, "water");
						map[i][j+1] = new Character(0, 0, 0, "water");
						map[i+1][j] = new Character(0, 0, 0, "water");
						map[i+1][j+1] = new Character(0, 0, 0, "water");
						emptyCols.add(j);
						emptyCols.add(j + 1);
					}
				}
			}
		}
	}
	
	/**
	 * Getter method for character on map. Retrieves reference to character
	 * 
	 * @param row int that is the row of the desired character is
	 * @param col int that is the column of the desired character is
	 * @return	the Character at said location
	 */
	public Character getCharacterAt(int row, int col) {		
		return map[row][col];
	}
	
	/**
	 * Place a character onto a new position on the map
	 * 
	 * @param c - the character being moved
	 * @param row coordinate of destination
	 * @param col coordinate of destination
	 */
	public void setCharacterAt(Character c,int row,int col) {
		for (Character ch: characters) {
			if (ch != null && ch.equals(c)) {
				ch.setPoint(col, row);
				map[row][col] = ch;
			}
		}
	}
	
	/**
	 * Removes a character at a specified location in the board. if the 
	 * character does not exist at said location, false is returned.
	 * 
	 * @param row	the row where the character to remove is
	 * @param col	the column where the character to remove is
	 * @return true if the character is removed successfully, false otherwise
	 * 
	 */
	public boolean removeCharacterAt(int row, int col) {
		if (map[row][col] != null) {
			Character ch = map[row][col];
			map[row][col] = null;
			setChanged();
			notifyObservers(ch);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Returns the number of the user's characters left on the board.
	 * 
	 * @return int, the number of UserChars left 
	 */
	public int numberOfUserChars() {
		return nbrOfUser;
	}
	
	/**
	 * Returns the map of the game.
	 * 
	 * @return the map represented by a 2d grid of Character
	 */
	public Character[][] getMap() {
		return map;
	}
	
	/**
	 * Returns an array of Characters
	 * 
	 * @return a list of characters as an array of Character
	 */
	public Character[] getCharList() {
		setChanged();
		return characters;
	}
	
	/**
	 * Remove a character when its HP reaches 0
	 * 
	 * @param ch - character being removed
	 * @param row coordinate of the character on the map
	 * @param col coordinate of the character on the map
	 */
	public void killCharacter(Character ch, int row, int col) {
		for (int i = 0; i < characters.length; i++) {
			if (characters[i] != null && characters[i].equals(ch)) {
				characters[i] = null;
			}
		}
		map[row][col] = null;
		if (ch.getTeam().equals("AI")) {
			nbrOfAI--;
		} else {
			nbrOfUser--;
		}
		notifyObservers(getMap());
	}
	
	/**
	 * Get the coordinates of a specified character. Return [-1, -1] if the character
	 * does not exist.
	 * 
	 * @param c - a Character
	 * @return an integer array containing the coordinates, [row, col]
	 */
	public int[] getCharLoc(Character c) {
		for (int i=0; i<ROWS; i++) {
			for (int j=0; j<COLS; j++) {
				if (map[i][j] != null && map[i][j].equals(c)) {
					return new int[] {i,j};
				}
			}
		}
		return new int[]{-1,-1};
	}
	
	/**
	 * Returns the number of each teams' players left in an array
	 * 
	 * @return an int[] where the first number is the number of the user's players
	 * 			left and the second number is the number of the AI's players left
	 */
	public int[] playersLeft() {
		return new int[] {nbrOfUser, nbrOfAI};
	}
}
