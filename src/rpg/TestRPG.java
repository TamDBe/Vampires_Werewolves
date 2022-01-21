package rpg;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Test Cases for RPGController, RPGModel, and Character classes 
 * 
 * date: 5/04/21
 * 
 * NOTE: There is some level of variance in this game - the obstacles 
 * are randomly assigned, which occasionally blocks the path of the 
 * test characters moving. When this happens, there is an assertion 
 * error in TestBattle, and the overall coverage is about 38%, 
 * the coverage for controller is about 75%, because it's random, 
 * another few runs should yield a run, where the overall coverage is 43.1%,
 *  and the coverage for controller is over 90%.
 */

public class TestRPG {
	
	@Test 
	public void TestModelSetUp() { 
		RPGModel model = new RPGModel(); 
		Character playerGame = model.getCharacterAt(0, 0);
		Character AIGame = model.getCharacterAt(19, 19);
		assertNotNull(playerGame);
		assertNotNull(AIGame);
		assertEquals(playerGame.getHP(), 100);
		assertEquals(AIGame.attack(), 10);
		playerGame.setDefense(true); 
		playerGame.setDefense(false); 
		assertEquals(playerGame.defend(), 0); 
		assertEquals(AIGame.getTeam(), "AI");
		
	}
	
	@Test public void TestControllerSetup() { 
		RPGController controller = new RPGController(); 
		Character[] testChars = new Character[10];
		for (int i = 0; i< 5; i++) {
			testChars[i] = new Character(100, 5, 10, "player");
		}
		//create AI characters
		for (int i = 5; i < 10 ; i++) {
			testChars[i] = new Character(100, 5, 10, "AI");
		}
		
		Character[] gameChars = controller.getCharacters(); 
		for (int j = 0; j < 10; j++) {
			assertEquals(gameChars[j].getTeam(),testChars[j].getTeam()); 
		}
		//assertTrue(.equals(characters)); 
		
	}
	
	@Test
	public void testMoves() {
		RPGModel model = new RPGModel(); 
		RPGController controller = new RPGController(model);
		Character playerGame = model.getCharacterAt(0, 0);
		Character AIGame = model.getCharacterAt(19, 17);
		assertTrue(controller.humanTurn("move", 0, 0, 3, 0, playerGame)); 
		assertEquals(controller.AIturn(19, 17, AIGame), "move"); 

		assertEquals(model.getCharLoc(playerGame)[0], 3);
		assertEquals(model.getCharLoc(playerGame)[1], 0);
		assertEquals(model.getCharLoc(AIGame)[0], 19);
		assertEquals(model.getCharLoc(AIGame)[1], 15);
	}
	
	@Test
	public void testBattle() {
		RPGModel model = new RPGModel(); 
		RPGController controller = new RPGController(model);
		Character AIGame = model.getCharacterAt(19, 17);
		controller.humanTurn("move", 0, 0, 3, 0, model.getCharacterAt(0, 0));
		controller.humanTurn("move", 0, 3, 6, 0, model.getCharacterAt(3, 0));
		controller.humanTurn("move", 0, 6, 9, 0, model.getCharacterAt(6, 0));
		controller.humanTurn("move", 0, 9, 12, 0, model.getCharacterAt(9, 0));
		controller.humanTurn("move", 0, 12, 15, 0, model.getCharacterAt(12, 0));
		controller.humanTurn("move", 0, 15, 18, 0, model.getCharacterAt(15, 0));
		controller.humanTurn("move", 0, 18, 19, 0, model.getCharacterAt(18, 0));
		controller.humanTurn("move", 0, 19, 19, 3, model.getCharacterAt(19, 0));
		controller.humanTurn("move", 3, 19, 19, 6, model.getCharacterAt(19, 3));
		controller.humanTurn("move", 6, 19, 19, 8, model.getCharacterAt(19, 6));
		controller.humanTurn("move", 8, 19, 19, 11, model.getCharacterAt(19, 8));
		controller.humanTurn("move", 11, 19, 19, 12, model.getCharacterAt(19, 11));
		
		controller.AIturn(17, 19, AIGame); 
		controller.AIturn(15, 19, AIGame); //should be at row 19 col 3 

		assertEquals(model.getCharLoc(AIGame)[0], 19);
		assertEquals(model.getCharLoc(AIGame)[1], 13);
		
		Character playerGame = model.getCharacterAt(19, 12);
		Character AIGame2 = model.getCharacterAt(19, 13);
		
		assertTrue(controller.humanTurn("atk", 12, 19, 19, 13, playerGame));
		assertTrue(controller.humanTurn("def", 12, 19, 19, 13, playerGame));
		assertEquals(playerGame.defend(), 5); 
		assertTrue(controller.humanTurn("atk", 12, 19, 19, 13, playerGame));
		assertEquals(playerGame.defend(), 0); 
		assertEquals(AIGame2.getHP(), 80);
		
		assertNotEquals(controller.AIturn(13, 19, AIGame), "move");
		assertNotEquals(controller.AIturn(13, 19, AIGame), "move");
		
	}
	
	@Test
	public void testGameOver() {
		RPGModel model = new RPGModel(); 
		RPGController controller = new RPGController(model);
		assertEquals(controller.isOver(), 0); 
		model.killCharacter(model.getCharacterAt(0, 0), 0, 0); 
		model.killCharacter(model.getCharacterAt(0, 2), 0, 2);
		model.killCharacter(model.getCharacterAt(2, 0), 2, 0);
		model.killCharacter(model.getCharacterAt(2, 3), 2, 3); 
		model.killCharacter(model.getCharacterAt(4, 1), 4, 1); 
		assertEquals(controller.isOver(), 2);
		model.killCharacter(model.getCharacterAt(19, 19), 19, 19); 
		model.killCharacter(model.getCharacterAt(19, 17), 19, 17);
		model.killCharacter(model.getCharacterAt(17, 19), 17, 19);
		model.killCharacter(model.getCharacterAt(17, 16), 17, 16); 
		model.killCharacter(model.getCharacterAt(15, 18), 15, 18); 
		assertEquals(controller.isOver(), 3);
	}


}

