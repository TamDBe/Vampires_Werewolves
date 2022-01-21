package rpg;

/**
 * This class represents a single character in the game. 
 *
 * date: 5/05/21
 */
public class Character {
	
	private int HP; 
	private int defense; 
	private int attackPower; 
	private boolean isDefending; 
	private String team; 
	public Point pos;
	
	/**
	 * Constructor for a character with values for the fields passed in. 
	 * 
	 * @param HP, an int representing the character's health
	 * @param defense, an int representing the power of a character's defense 
	 * @param attackPower, an int representing the power of a character's attack
	 * @param team, an String either "AI" or "player" to signify the team  
	 * 
	 */
	public Character(int HP, int defense, int attackPower, String team) { 
		this.HP = HP; 
		this.defense = defense; 
		this.attackPower = attackPower; 
		this.team = team;
		this.pos = new Point(-1,-1);
	}
	
	/**
	 * @return int, represents the strength of the characters attack   
	 */
	public int attack() { 
		return this.attackPower; 
	}
	
	/**
	 * returns the power of the characters defense 
	 * 
	 * @return int, represents the power of the characters defense   
	 */
	public int defend() { 
		if (isDefending){ 
			return this.defense;
		} 
		return 0;  
	}
	
	/**
	 * sets the defense field to true  
	 * 
	 *  @param b, a boolean true if the character is defending, false otherwise.
	 */
	public void setDefense(boolean b) { 
		isDefending = b; 
	}
	
	/**
	 * This method lowers the HP of the character by the amount given  
	 * 
	 * @param loss, an int of by how much to lower the character's health 
	 */
	public void lowerHP(int loss) { 
		this.HP = this.HP - loss; 
	}
	
	/**
	 * Returns the team of the character 
	 * 
	 * @return the character's team, either "AI" or "player"  
	 */
	public String getTeam() { 
		return this.team;
	}
	
	/**
	 * Returns the character's health 
	 * 
	 * @return the character's health as an integer
	 */
	public int getHP() {
		return HP;
	}
	
	/**
	 * Sets the location of the character as a Point
	 * 
	 * @param int x, the x location of the character, 
	 * @param int y, the y location of the character
	 */
	public void setPoint(int x,int y) {
		pos.x = x;
		pos.y = y;
	}
	
	/**
	 * @return Point, an instance of Point class holding the location of the character 
	 */
	public Point getPoint() {
		return pos;
	}
	
}

/**
 * Package private class that holds the (x,y) location of the Character as a single object 
 */
class Point {
	int x;
	int y;
	/**
	 * Constructs the point class. 
	 * 
	 * @param int x, the x location of the character, 
	 * @param int y, the y location of the character
	 */
	Point(int x, int y){
		this.x = x;
		this.y = y;
	}
}
