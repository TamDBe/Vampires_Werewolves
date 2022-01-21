package rpg;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Random;

/**
 * This class is the controller, methods are called by the view to change the model. 
 *
 * date: 5/05/21
 */

@SuppressWarnings("deprecation")
public class RPGController{
	private RPGModel model;

	/**
	 * Constructor for controller. Initializes a new model
	 */
	public RPGController() {
		this.model = new RPGModel();
	}
	
	/**
	 * Constructor for controller. Takes in a model.
	 */
	public RPGController(RPGModel model) {
		this.model = model;
	}

	/**
	 * Processes the turn the player makes and determines which action to take, move, defend, or attack  
	 * 	
	 * @param act, a String, the action the player made 	 
	 * @param col, an int
	 * @param row, an int
	 * @param row2, an int 
	 * @param col2, an int 
	 * @param c, a Character, the character making the move 
	 * 
	 *@boolean, true if an action happened during the turn, false otherwise 
	 */	

	public boolean humanTurn(String act,int col, int row, int row2, int col2, Character c) {
		if (act.equals("move")) {
			return move(col, row, row2, col2);
		} else if (act.equals("def")) {		
			defend(row, col); 
			return true;
		} else if (act.equals("atk")) {
			System.out.println("Character is attacking");
			attack(row, col, row2, col2, "AI");
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Get the map of the game
	 * 
	 * @return the map of the game represented by a 2d array of Character
	 */
	public Character[][] getMap() {
		return model.getMap();
	} 

	/**
	 * Get a list of characters 
	 * 
	 * @return the list of characters represented by an array of Character.
	 */
	public Character[] getCharacters() {
		return model.getCharList();
	}

	/**
	 * Get the coordinates of a specified character
	 * 
	 * @param c - a Character
	 * @return an integer array containing the coordinates, [x, y]
	 */
	public int[] getCharLoc(Character c) {
		return model.getCharLoc(c);
	}

	/**
	 * Does the AI turn by determining whether the character should move, defend, or attack 
	 * based on if an enemy character is next to them 
	 * 	
	 * @param col, an int
	 * @param row, an int
	 * @param c, a Character, the character making the move 
	 * @String, "def," "atk," "move," or "" based on what action 
	 * 		was taken 
	 */	
	public String AIturn(int col, int row, Character c) { 

		int i = 2; 

		ArrayList<int[]> directionList = new ArrayList<int[]>(); 		
		directionList.add(new int[]{-1, 0}); //n, up a row  
		directionList.add(new int[]{1, 0}); //s, down a row
		directionList.add(new int[]{0, 1}); //e, to the right 
		directionList.add(new int[]{0, -1}); //w, to the left
		directionList.add(new int[]{-1, 1}); //ne
		directionList.add(new int[]{-1, -1}); //nw
		directionList.add(new int[]{1, 1}); //se
		directionList.add(new int[]{1, -1}); //sw

		//if there's anyone next to them, attack or defend
		for (int[] direction : directionList) { 
			int nextRow = row + direction[0];
			int nextCol = col + direction[1];
			int cols = model.getMap()[0].length;
			int rows = model.getMap().length; 
			if 	((nextRow >= 0) && (nextRow < rows) && (nextCol >= 0) && (nextCol < cols)) {
				Character possibleEnemy =  model.getCharacterAt(nextRow, nextCol);
				if (possibleEnemy != null && possibleEnemy.getTeam().equals("player")) { 
					//then there is a character there 
					Random ran = new Random();
					i = ran.nextInt(2); // either 0 or 1 
					if (i == 0) { //attack
						attack(row, col, nextRow, nextCol, "player");
						return "atk";
					}
					else { //if (i == 1) {  //defend 
						defend(row, col);
						return "def";
					}
				}
			}
		}

		//if theres no one next to them, then move 
		if (i == 2) { 
			Character[] pChar = model.getCharList();
			int[] dist = new int[pChar.length];
			int path = 0;
			for (int j = 0; j< 5; j++) {
				if (pChar[j] != null) {
					int dx = Math.abs(c.getPoint().x - pChar[j].getPoint().x);
					int dy = Math.abs(c.getPoint().y - pChar[j].getPoint().y);
					dist[j] = dx +dy;
					path = j;
				}
			}
			for (int j = 1;j < 5; j++) {
				if ( dist[j] < dist[path]) {
					path = j;
				}
			}
			AImove(c,pChar[path]);
			return "move";
		}
		return "";
	}

	/**
	 * Move AI character towards a target character.
	 * 
	 * @param enemy - AI being moved
	 * @param target - destination being moved towards
	 */
	private void AImove(Character enemy,Character target) {
		int []x = {-1,0,0,1};
		int []y = {0,-1,1,0};
		LinkedList<Point> q = new LinkedList<Point>();
		List<Point> path = new ArrayList<Point>();
		q.add(enemy.getPoint());
		int n = model.getMap().length;
		int m = model.getMap()[0].length;
		boolean[][] visited = new boolean[n][m];
		int[][] d = new int[n][m];
		Point[][] p2 = new Point[n][m];
		visited[enemy.getPoint().x][enemy.getPoint().y] = true;
		d[enemy.getPoint().x][enemy.getPoint().y] = 0;
		Point p = new Point(enemy.getPoint().x, enemy.getPoint().y);
		while(!q.isEmpty()){
			int dist = d[p.y][p.x];
			if (p.x == target.getPoint().x && p.y == target.getPoint().y) {
				break;
			}
			for(int i = 0; i < 4; i++){
				int a = p.x + x[i];
				int b = p.y + y[i];
				if(inRange(b,a) && !visited[b][a] && model.getMap()[b][a] == null){
					d[b][a] = dist;
					p2[b][a] = p;
					visited[b][a] = true;	
					q.add(new Point(a,b));
				}
				
			}
			p = q.removeFirst();
		}
		while (p != null) {
			path.add(0, new Point (p.x, p.y));
			p = p2[p.y][p.x];
		}
		AImove2(path,enemy);
	}

	/**
	 * Helper method for AImove
	 * 
	 * @param path, a List<Point> collection holding the first location, 
	 * 		the final location, and spots inbetween. 
	 * @param enemy, Character
	 */
	private void AImove2(List<Point> path,Character enemy) {
		int row = enemy.getPoint().y;
		int col = enemy.getPoint().x;
        if (path.size() > 2) {
			model.setCharacterAt(enemy,path.get(2).y,path.get(2).x);
			model.removeCharacterAt(row, col);
		} else {
			model.setCharacterAt(enemy,path.get(0).y,path.get(0).x);
			model.removeCharacterAt(row, col);
		}
		
	}
	

	/**
	 * Add the view as an observer of the model
	 * @param view
	 */
	public void bindObservable(RPGView view) {
		Observable obs = (Observable) model;
		obs.addObserver(view);
		obs.hasChanged();
		obs.notifyObservers(obs);
	}

	/**
	 * Makes a character move from from one location on the model board to the other 
	 * 
	 * @param col, an int
	 * @param row, an int
	 * @param row2, an int 
	 * @param col2, an int 
	 * @return boolean, true if the move was made, false otherwise 
	 */
	public boolean move(int col, int row,int row2, int col2) {
		if (inRange(col,row) && inRange(col2,row2) && model.getMap()[row2][col2] == null && model.getMap()[row][col] != null) {
			Character c = model.getCharacterAt(row, col);
			c.setDefense(false);
			for (int i = 0; i < model.getCharList().length; i++) {
				if (model.getCharList()[i] != null && model.getCharList()[i].equals(c)) {
					if (c.getTeam().equals("AI")) {
						System.out.println("Werewolf " + (i-4) + " moves.");
					} else {
						System.out.println("Vampire " + (i+1) + " moves.");
					}
				}
			}
			model.setCharacterAt(c,row2,col2);
			model.removeCharacterAt(row, col);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Makes a character attack the character at another location 
	 * 
	 * @param col, an int (attacker's location)
	 * @param row, an int (attacker's location)
	 * @param row2, an int (attacked's location)
	 * @param col2, an int (attacked's location)
	 * @param enemy, a String, either "AI" or "player", the team being attacked 
	 */
	private void attack(int row, int col, int row2, int col2, String enemy) { 
		if (inRange(col,row) && inRange(col2,row2)) {
			Character attacker = model.getCharacterAt(row, col);
			attacker.setDefense(false);
			Character attacked = model.getCharacterAt(row2, col2);
			if (attacked != null && attacked.getTeam().equals(enemy)) { 
				int attack = attacker.attack() - attacked.defend(); 
				attacked.lowerHP(attack); 
				for (int i = 0; i < model.getCharList().length; i++) {
					if (model.getCharList()[i] != null && model.getCharList()[i].equals(attacker)) {
						if (attacker.getTeam().equals("AI")) {
							System.out.println("Werewolf " + (i-4) + " attacks.");
						} else {
							System.out.println("Vampire " + (i+1) + " attacks.");
						}
					}
				}
				model.notifyObservers(model.getCharList());
				if (attacked.getHP() <= 0) { // remove character from game when their HP reaches 0
					model.killCharacter(attacked, row2, col2);
				}
			} else { 
				System.out.println("Cannot attack here");
			}
		} else { 
			System.out.println("Cannot attack here");
		}
	}

	/**
	 * Activate the defense of the character at a specified location.
	 * 
	 * @param row coordinate of the character
	 * @param col coordinate of the character
	 */
	private void defend(int row, int col) { 
		Character defender = model.getCharacterAt(row, col);
		defender.setDefense(true); 
		for (int i = 0; i < model.getCharList().length; i++) {
			if (model.getCharList()[i] != null && model.getCharList()[i].equals(defender)) {
				if (defender.getTeam().equals("AI")) {
					System.out.println("Werewolf " + (i-4) + " is defending.");
				} else {
					System.out.println("Vampire " + (i+1) + " is defending.");
				}
			}
		}
	}


	/**
	 * Purpose: Checks that a coordinate is within the bounds of the board
	 * 
	 * @param x, an int
	 * @param y, an int
	 * @return a boolean, if the location given is within bounds of the board 
	 */
	private boolean inRange(int x, int y) {
		if ((x >= 0) && (x < model.getMap()[0].length) && (y >= 0) && (y < model.getMap().length) 
				&&  (model.getCharacterAt(y, x) == null || (!model.getCharacterAt(y, x).getTeam().equals("mountain") &&
				!model.getCharacterAt(y, x).getTeam().equals("water")))) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the game is over
	 * 
	 * @return int, 0 if game is not over, 1 if the user won, 2 if the AI won, 3 if tie(?)
	 */
	public int isOver() {
		int[] playersLeft = model.playersLeft();
		int userPlayers = playersLeft[0];
		int AIplayers = playersLeft[1];
		// if both still have players -> game is not over
		if (userPlayers > 0 && AIplayers > 0) {
			return 0;
		}
		// if no more AIplayers -> user won
		else if (userPlayers > 0 && AIplayers == 0) {
			return 1;
		}
		// if no more userPlayers -> AI won
		else if (userPlayers == 0 && AIplayers > 0) {
			return 2;
		} else {
			return 3; // tie
		}
	}
}
