package rpg;

import javafx.application.Application;

/**
 * This class launches the game: Vampires VS Werewolves.
 * The game puts the player into the shoes of the last band of vampires who are fighting against the last
 * band of werewolves. The player must strategically move and fight the enemy werevoles until either team
 * is eliminated. Characters are represented by sprites and the map is presented in a top-down fashion
 * with grid elements in which the characters can move and act in. This program is implement using MVC
 * architecture.
 *
 * @author Tam Be, Sasha Sepulveda, Giulia Ghidoli, Tina Le
 * date: 5/05/21
 */
public class RPG {
	public static void main(String[] args) {
		Application.launch(RPGView.class, args);
	}
}
