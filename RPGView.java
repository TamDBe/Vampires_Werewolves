package rpg;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
/**
 * This class draws the GUI for the game. Implements observer, 
 * observes the model. 
 *
 * date: 5/05/21
 */

@SuppressWarnings("deprecation")

/**
 * Intilizes the view and the game, extends Application and 
 * implements observable. 
 */
public class RPGView extends Application implements Observer {
	private static final int ROWS = 20; //row dimension of the grid
	private static final int COLS = 20; //column dimension of the grid
	private static final int STARTING_PLAYERS = 5; // number of players to start

	private RPGController controller; //store the controller
	private GridPane grid; //store the grid of the game
	private BorderPane mainPane; //contains the entire GUI
	private VBox userBar; //left partition of the GUI, contains the buttons of the game
	private Button makePlayBtn; //button to perform the player's actions
	private Button charBtn[]; //button for choosing the actions of each character
	private StackPane[][] squares; //store the tiles of the grid
	private Group group; //canvas for character animations
	private ImageView[] sprite; //store circles which represent the characters on the map
	private Rectangle[][] moveTiles; //these tiles present available movement squares to the player
	private Rectangle[][] atkTiles; //these tiles present available attack squares to the player
	private Text[] healthLabel;

	/**
	 * Updates the view, having been notified by the Observable model
	 */
	@Override
	public void update(Observable o, Object obj) {
		if (obj instanceof Character[]) {
			updateStats();
		} else if (obj instanceof Character) {
			animateMove((Character)obj);
		} else if (obj instanceof Character[][]) {
			try {
				drawMap();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Start method that draws the view 
	 */
	@Override
	public void start(Stage stage) throws Exception {
		//initiate fields
		sprite = new ImageView[10];
		controller = new RPGController();
		controller.bindObservable(this);
		squares = new StackPane[ROWS][COLS];
		moveTiles = new Rectangle[ROWS][COLS];
		atkTiles = new Rectangle[ROWS][COLS];
		mainPane = new BorderPane();
		grid = new GridPane();
		group = new Group();
		healthLabel = new Text[STARTING_PLAYERS*2];
		setBG();
		stage.setTitle("RPG");

		//initiate stack panes for the grid
		for (int i=0; i<ROWS; i++) {
			for (int j=0; j<COLS; j++) {
				moveTiles[i][j] = new Rectangle(38, 38);
				moveTiles[i][j].setFill(Color.rgb(0, 191, 255, 0.7));
				moveTiles[i][j].setVisible(false);
				atkTiles[i][j] = new Rectangle(38, 38);
				atkTiles[i][j].setFill(Color.rgb(255,  100,  0, 0.7));
				atkTiles[i][j].setVisible(false);
				squares[i][j] = new StackPane();
				squares[i][j].setPrefSize(40, 40);
				squares[i][j].setMaxSize(40, 40);
				squares[i][j].getChildren().addAll(moveTiles[i][j], atkTiles[i][j]);
				// set border of stack panes to black with transparency
				squares[i][j].setBorder(new Border(new BorderStroke(Color.rgb(0,0,0,0.1),
						BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
				grid.add(squares[i][j], j, i);
			}
		}
		mainPane.setCenter(grid);
		mainPane.autosize();
		group.getChildren().add(mainPane);
		group.prefHeight(mainPane.getHeight());
		group.prefWidth(mainPane.getWidth());
		drawObstacles();
		// add dark overlay to give a nighttime effect
		Rectangle overlay = new Rectangle();
		overlay.setMouseTransparent(true);
		overlay.setHeight(grid.getHeight());
		overlay.setWidth(grid.getWidth());
		overlay.setFill(Color.rgb(0, 0, 100, 0.3));
		overlay.setLayoutX(150);
		group.getChildren().add(overlay);
		drawMap();
		Scene scene = new Scene(group);
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Setup the background and the left and right panels of the GUI.
	 * @throws FileNotFoundException 
	 */
	private void setBG() throws FileNotFoundException {
		Background gridBG = new Background(new BackgroundFill(Color.FORESTGREEN,
				CornerRadii.EMPTY, Insets.EMPTY));
		grid.setPadding(new Insets(8,8,8,8));
		grid.setBackground(gridBG);

		/** Layout left pane **/
		// create userBar to make character moves
		userBar = new VBox(10);
		Background vboxBG = new Background(new BackgroundFill(Color.DARKGRAY,
				CornerRadii.EMPTY, Insets.EMPTY));
		userBar.setBackground(vboxBG);
		userBar.setPrefWidth(150);
		userBar.setPrefHeight(100);
		userBar.setPadding(new Insets(15, 15, 15, 15));
		userBar.setAlignment(Pos.CENTER);
		//setup characters' buttons and set their handlers
		charBtn = new Button[STARTING_PLAYERS];
		for (int i = 0; i < STARTING_PLAYERS; i++) {
			Label charLabel = new Label("Vampire " + (i+1));
			charBtn[i] = new Button();
			charBtn[i].setPrefSize(100, 50);
			charBtn[i].setText("Click to Choose Action");
			charBtn[i].setBackground(new Background(new BackgroundFill(Color.BLUE,
					new CornerRadii(5), Insets.EMPTY)));
			charBtn[i].setTextFill(Color.WHITE);
			charBtn[i].setWrapText(true);
			charBtn[i].setOnMouseClicked((event) -> {changeActionBtn((Button)event.getSource());});
			userBar.getChildren().addAll(charLabel, charBtn[i]);
		}

		Rectangle emptySquare = new Rectangle();
		emptySquare.setWidth(100); 
		emptySquare.setHeight(50);
		emptySquare.setFill(Color.TRANSPARENT);
		userBar.getChildren().add(emptySquare);
		// add button 
		makePlayBtn = new Button("Make Play!");
		makePlayBtn.setOnMouseClicked((event) -> {makePlay();});
		userBar.getChildren().add(makePlayBtn);
		mainPane.setLeft(userBar);

		/** Layout right pane **/
		// add photo on right pane
		StackPane rightPane = new StackPane();
		InputStream stream = new FileInputStream("right.jpg");
		Image image = new Image(stream);
		ImageView jacobRight = new ImageView();
		// Setting image to the image view
		jacobRight.setImage(image);
		mainPane.setRight(rightPane);
		// layout health display
		VBox container = new VBox();
		StackPane upper = new StackPane();
		StackPane lower = new StackPane();
		upper.setPadding(new Insets(20, 20, 20, 20));
		lower.setPadding(new Insets(20, 20, 20, 20));
		GridPane upperGrid = new GridPane();
		GridPane lowerGrid = new GridPane();
		container.getChildren().add(upper);
		container.getChildren().add(lower);
		upper.getChildren().add(upperGrid);
		lower.getChildren().add(lowerGrid);
		upper.setBackground(new Background(new BackgroundFill(Color.rgb(135, 206, 255, 0.6), new CornerRadii(10), new Insets(10, 10, 20, 10))));
		lower.setBackground(new Background(new BackgroundFill(Color.rgb(205, 92, 92, 0.6), new CornerRadii(10), new Insets(0, 10, 10, 10))));
		upper.setPrefSize(272, 400);
		lower.setPrefSize(272, 420);
		upperGrid.setPadding(new Insets(0,0,0,20));
		lowerGrid.setPadding(new Insets(0,0,0,20));
		rightPane.getChildren().addAll(jacobRight, container);
		for (int i = 0; i < STARTING_PLAYERS; i++) {
			RowConstraints r = new RowConstraints();
			r.setPercentHeight(20);
			upperGrid.getRowConstraints().add(r);
			lowerGrid.getRowConstraints().add(r);
		}
		for (int i = 0; i < 2; i ++) {
			ColumnConstraints c = new ColumnConstraints();
			c.setPercentWidth(50);
			upperGrid.getColumnConstraints().add(c);
			lowerGrid.getColumnConstraints().add(c);
		}
		// add health text
		Character[] chars = controller.getCharacters();
		for (int i = 0; i < 5; i++) {
			Text name = new Text("Vampire " + (i+1) + ":");
			healthLabel[i] = new Text(chars[i].getHP() + "HP");
			healthLabel[i].setFont(Font.font("System", FontWeight.EXTRA_BOLD, 16));
			name.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 16));
			upperGrid.add(name, 0, i);
			upperGrid.add(healthLabel[i], 1, i);
			name.setTextAlignment(TextAlignment.RIGHT);
			healthLabel[i].setTextAlignment(TextAlignment.LEFT);
		}
		for (int i = 5; i < 10; i++) {
			Text name = new Text("Werewolf " + (i-4) + ":");
			healthLabel[i] = new Text(chars[i].getHP() + "HP");
			healthLabel[i].setFont(Font.font("System", FontWeight.EXTRA_BOLD, 16));
			name.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 16));
			lowerGrid.add(name, 0, i-5);
			lowerGrid.add(healthLabel[i], 1, i-5);
			name.setTextAlignment(TextAlignment.RIGHT);
			healthLabel[i].setTextAlignment(TextAlignment.LEFT);
		}
		openingAlert();
	}

	/**
	 * Create opening alert message.
	 * 
	 * @param message
	 * @throws FileNotFoundException 
	 */
	private static void openingAlert() throws FileNotFoundException {
		Alert a = new Alert(Alert.AlertType.INFORMATION);
		a.setTitle("Vampires vs. Werewolves");
		a.setContentText("For centuries, the Vampires and Werewolves have feuded for dominance. \n"
				+ "The Werewolves have continuously ravaged the Vampires' land, and it is time to finally"
				+ " put an end to it.\n"
				+ "In an epic battle for the win, the two powers will fight each other till only one dynstaty is left standing.\n"
				+ "The Vampires are relying on YOU to defeat the monstrous Werewolves.\n\n"
				+ "		Click OK to play.");
		a.setHeaderText("Vampires vs. Werewolves:\n The Final Battle");

		// set graphic 
		InputStream stream = new FileInputStream("group.jpeg");
		Image image = new Image(stream);
		ImageView imageView = new ImageView();
		//Setting image to the image view
		imageView.setImage(image);
		a.setGraphic(imageView);
		a.showAndWait();
	}

	/**
	 * Change the text of an action button. Options are Move, Attack, or Defend.
	 * 
	 * @param button being toggled
	 */
	private void changeActionBtn(Button btn) {
		switch (btn.getText()) {
			case "Click to Choose Action":
				btn.setText("Move");
				btn.setFont(new Font(16));
				break;
			case "Move":
				btn.setText("Attack");
				break;
			case "Attack":
				btn.setText("Defend");
				break;
			case "Defend":
				//fall through
			default:
				btn.setText("Move");
		}
	}

	/**
	 * Finalize the player's selection and perform their selected actions.
	 */
	private void makePlay() {
		//check that an action is selected for all characters
		for (int i=0; i<charBtn.length; i++) {
			if (charBtn[i].getText().equals("Click to Choose Action")) {
				Alert a = new Alert(AlertType.INFORMATION, "You must select an action for all characters");
				a.setHeaderText("");
				a.setGraphic(null);
				a.showAndWait();
				return;
			}
		}
		//disable the buttons in while characters are performing their actions
		for (int i=0; i<charBtn.length; i++) {
			charBtn[i].setDisable(true);
		}
		makePlayBtn.setDisable(true);
		performAction(0);
	}

	/**
	 * Let player perform the selected action for each character.
	 * 
	 * @param i - the index of the current character performing the action
	 */
	private void performAction(int i) {
		if (i>charBtn.length-1) {
			runAI(i);
			return;
		}
		Character current = controller.getCharacters()[i];
		if (charBtn[i].getText().equals("Attack")) {
			enableAtk(current, i);
		} else if (charBtn[i].getText().equals("Move")) {
			enableMove(current, i);
		} else {
			int[] loc = controller.getCharLoc(current);
			controller.humanTurn("def", loc[1], loc[0], 0, 0, current);
			performAction(i+1);
		}
	}

	/**
	 * Run AI.
	 * @param i - index of the AI in the characters list
	 */
	private void runAI(int i) {
		if (i > 9) {
			for (int j=0; j<charBtn.length; j++) {
				charBtn[j].setDisable(false);
			}
			makePlayBtn.setDisable(false);
			return;
		}
		Character[] chars = controller.getCharacters();
		if (chars[i] != null) {
			int[] loc = controller.getCharLoc(chars[i]);
			String action = controller.AIturn(loc[1], loc[0], chars[i]);
			if (action.equals("def") ||  action.equals("atk")) {
				runAI(i+1);
			}
		} else {
			runAI(i+1);
		}
	}

	/**
	 * Present the tiles in which a character can move and set its handlers.
	 * @param c - the character being moved
	 * @param n - the index of the current character performing the action
	 */
	private void enableMove(Character c, int n) {
		int[] loc = controller.getCharLoc(c);
		int row = loc[0];
		int col = loc[1];

		drawMoveTiles(col, row);
		//set mouseclick handler for the grid
		//this handler places the player's piece if the move is valid and then advances to the computer's turn.
		grid.setOnMouseClicked((event)-> {
			double x = event.getX();
			double y = event.getY();
			for (int i=0; i<ROWS; i++) {
				for (int j=0; j<COLS; j++) {
					if (squares[i][j].getBoundsInParent().contains(new Point2D(x, y)) && moveTiles[i][j].isVisible()) {
						if (controller.humanTurn("move", col, row, i, j, c)) {
							((GridPane)event.getSource()).setOnMouseClicked(null);
							setInvisible(moveTiles);
							performAction(n+1);
						}
					}
				}
			}
			;});
	}

	/**
	 * Draw the tiles that are availble for movement of a character.
	 * 
	 * @param x - the x coordinate of the character
	 * @param y - the y coordinate of the character
	 */
	private void drawMoveTiles(int x, int y) {
		drawTilesHelper(x, y, 4, moveTiles);
	}

	/**
	 * Present the tiles in which a character can atk and set its handlers.
	 * @param c - the character being moved
	 * @param n - the index of the current character performing the action
	 */
	private void enableAtk(Character c, int n) {
		int[] loc = controller.getCharLoc(c);
		int row = loc[0];
		int col = loc[1];

		drawAtkTiles(col, row);
		//set mouseclick handler for the grid
		//this handler places the player's piece if the move is valid and then advances to the computer's turn.
		grid.setOnMouseClicked((event)-> {
			double x = event.getX();
			double y = event.getY();

			for (int i=0; i<ROWS; i++) {
				for (int j=0; j<COLS; j++) {
					if (squares[i][j].getBoundsInParent().contains(new Point2D(x, y)) && atkTiles[i][j].isVisible()) {
						if (controller.humanTurn("atk", col, row, i, j, c)) {
							((GridPane)event.getSource()).setOnMouseClicked(null);
							setInvisible(atkTiles);
							performAction(n+1);
						}
					}
				}
			}
			;});
	}

	/**
	 * Draw the tiles that are available for attack of a character.
	 * 
	 * @param x - the x coordinate of the character
	 * @param y - the y coordinate of the character
	 */
	private void drawAtkTiles(int x, int y) {
		drawTilesHelper(x, y, 2, atkTiles);
	}

	/**
	 * Helper for drawing move and atk tiles.
	 * 
	 * @param x - x coordinate of the character
	 * @param y - y coordinate of the character
	 * @param distance
	 * @param tiles
	 */
	private void drawTilesHelper(int x, int y, int distance, Rectangle[][] tiles) {
		//check if coordinates are within bound or if distance is 0
		if (distance == 0 || x<0 || x>=ROWS || y<0 || y>=COLS) return;

		if (tiles.equals(moveTiles) && controller.getMap()[y][x] != null) {
		} else {
			tiles[y][x].setVisible(true);
		}
		drawTilesHelper(x+1, y, distance-1, tiles); //move right
		drawTilesHelper(x-1, y, distance-1, tiles); //move left
		drawTilesHelper(x, y+1, distance-1, tiles); //move down
		drawTilesHelper(x, y-1, distance-1, tiles); //move up
	}

	/**
	 * Make a grid of tiles invisible.
	 * @param tiles to make invisible
	 */
	private void setInvisible(Rectangle[][] tiles) {
		for (int i=0; i<ROWS; i++) {
			for (int j=0; j<COLS; j++) {
				tiles[i][j].setVisible(false);
			}
		}
	}

	/**
	 * Draw the characters onto the map
	 * @throws FileNotFoundException 
	 */
	private void drawMap() throws FileNotFoundException {
		Character[] character = controller.getCharacters();
		for (int i = 0; i<character.length; i++) {
			if (character[i] != null && sprite[i] == null) { //initiate character sprites
				sprite[i] = new ImageView();
				sprite[i].setMouseTransparent(true);
				group.getChildren().add(sprite[i]);
				if (character[i].getTeam().equals("player")) {
					sprite[i].setImage(new Image("rpg/vampire_sprite.png"));
				} else if (character[i].getTeam().equals("AI")){
					sprite[i].setImage(new Image("rpg/werewolf_sprite.png"));
				}
				int[] loc = controller.getCharLoc(character[i]);
				sprite[i].setX(getCharCoordX(loc[1])-20);
				sprite[i].setY(getCharCoordY(loc[0])-20);
			} else if (character[i] == null && sprite[i] != null) { //delete sprite if character dies
				sprite[i].setVisible(false);
				sprite[i] = null;
				// check if game is over after removing sprite
				int gameResult = controller.isOver();
				if (gameResult > 0) {
					if (gameResult == 1) {
						gameOver("user");
					} else if (gameResult == 2) {
						gameOver("AI");
					} else {
						gameOver("tie");
					}
				}
			} 
		}
	}

	/**
	 * Display game over alert
	 * 
	 * @throws FileNotFoundException 
	 */
	private void gameOver(String winner) throws FileNotFoundException {
		Alert a = new Alert(Alert.AlertType.INFORMATION);
		String content = null;
		String header = null;
		String photoTitle = null;
		if (winner.equals("user")) {
			content = "The Vampires successfully defeated the Werewolves and\n"
					+ "order has been restored.\n\n"
					+ "		Click OK to exit.";
			header = "You Won!";
			photoTitle = "vampires.jpg";
			// set image title and text for content, header text too
		} else if (winner.equals("AI")) {
			content = "The Werewolves defeated the Vampires and will continue to ravage the land.\n\n"
					+ "		Click OK to exit.";
			header = "You Lost!";
			photoTitle = "werewolves.jpg";
		} else {
			header = "It's a Tie!";
			content = "The fued has ended, but at the cost of a destructive war.\n\n"
					+ "		Click OK to exit.";
			photoTitle = "vampires.jpg";
		}
		a.setTitle("Vampires vs. Werewolves");
		a.setContentText(content);
		a.setHeaderText(header);

		// set graphic 
		InputStream stream = new FileInputStream(photoTitle);
		Image image = new Image(stream);
		ImageView imageView = new ImageView();
		//Setting image to the image view
		imageView.setImage(image);
		a.setGraphic(imageView);
		a.showAndWait();
		System.exit(0);
	}

	/**
	 * Draw the obstacles features onto the map
	 */
	private void drawObstacles() { 
		Character[][] map = controller.getMap();	
		for (int i=0; i< map.length; i++) {
			for (int j=0; j<map[i].length; j++) {
				if (map[i][j] != null) {
					if (map[i][j].getTeam().equals("mountain")) { 
						double x = getCharCoordX(j);
						double y = getCharCoordY(i);
						ImageView mountain = new ImageView("rpg/mountain.png"); 
						mountain.setX(x-20);
						mountain.setY(y-20);
						mountain.setOnMouseClicked((event) -> {
							Alert a = new Alert(AlertType.INFORMATION, "Rough terrain... shouldn't go there.");
							a.setHeaderText("");
							ImageView t = new ImageView("rpg/mountain.png"); 
							t.setX(x);
							t.setY(y);
							a.setGraphic(t);
							a.showAndWait();
						});
						group.getChildren().add(mountain);
					}
					else if(map[i][j].getTeam().equals("water")) { 
						double x = getCharCoordX(j);
						double y = getCharCoordY(i);
						ImageView water = new ImageView("rpg/water.png"); 
						water.setX(x-20);
						water.setY(y-20);
						water.setOnMouseClicked((event) -> {
							Alert a = new Alert(AlertType.INFORMATION, "There's water... can't go there.");
							a.setHeaderText("");
							ImageView e = new ImageView("rpg/water.png"); 
							e.setX(x);
							e.setY(y);
							a.setGraphic(e);
							a.showAndWait();
						});
						group.getChildren().add(water);
					}
				}
			}
		}
	}

	/**
	 * Animate sprite movement.
	 * 
	 * @param c - the sprite being moved
	 * @param newRow - destination row
	 * @param newCol - destination column
	 */
	private void animateMove(Character ch) {
		Character[][] map = controller.getMap();
		Character[] chs = controller.getCharacters();
		int spriteIndex = 0; 
		for (int i = 0; i<sprite.length; i++) {
			if (chs[i] != null && chs[i].equals(ch)) {
				spriteIndex = i;
			}
		}
		int[] loc = new int[] {controller.getCharacters()[spriteIndex].pos.y, controller.getCharacters()[spriteIndex].pos.x};
		ImageView c = sprite[spriteIndex];
		int i = getSpriteCoordRow(c.getY()+20);
		int j = getSpriteCoordCol(c.getX()+20);
		List<Integer[]> l = getShortestPath(map, i, j, loc[0], loc[1], ch.getTeam());
		Path path = new Path();
		path.getElements().add(new MoveTo(c.getX()+20, c.getY()+20));
		int duration = 0;
		while (!l.isEmpty()) {
			Integer[] next = l.remove(0);
			path.getElements().add(new LineTo(getCharCoordX(next[1]), getCharCoordY(next[0])));
			duration += 150;
		}
		PathTransition pTrans = new PathTransition();
		pTrans.setDuration(Duration.millis(duration));
		pTrans.setNode(sprite[spriteIndex]);
		pTrans.setPath(path); 
		pTrans.play();
		pTrans.setOnFinished((event) -> {
			pTrans.setNode(null);
			pTrans.stop();
			c.setY(getCharCoordY(loc[0])-20);
			c.setX(getCharCoordX(loc[1])-20);
			c.setTranslateX(0);
			c.setTranslateY(0);
			if (ch.getTeam().equals("AI")) {
				for (int k = 5; k < chs.length; k++) {
					if (chs[k] != null && chs[k].equals(ch)) {
						runAI(k+1);
					}
				}
			}
		});
	}


	/**
	 * Find the shortest path from a source point to a destination point.
	 * 
	 * @param map - represented by a Character grid
	 * @param i - source row
	 * @param j - source column
	 * @param i2 - destination row
	 * @param j2 - destination column
	 * @return a list of coordinates that form the path
	 */
	private List<Integer[]> getShortestPath(Character[][] map, int r, int c, int r2, int c2, String team) {
		List<Integer[]> path = new ArrayList<Integer[]>();
		boolean[][] visited = new boolean[ROWS][COLS];
		Loc[][] p = new Loc[ROWS][COLS];
		int[][] d = new int[ROWS][COLS];
		Deque<Loc> q = new ArrayDeque<>();

		for (int i = 0; i < ROWS; i++) {
			Arrays.fill(d[i], Integer.MAX_VALUE);
		}

		visited[r][c] = true;
		Loc s = new Loc(r, c);
		d[r][c] = 0;
		while (s.y != r2 || s.x != c2) {
			int dist = d[s.y][s.x] + 1;
			if (s.y-1 >= 0 && !visited[s.y-1][s.x] && (map[s.y-1][s.x] == null || map[s.y-1][s.x].getTeam().equals(team))) {
				if (p[s.y-1][s.x] == null) {
					d[s.y-1][s.x] = dist;
					p[s.y-1][s.x] = s;
					q.addLast(new Loc(s.y-1, s.x));
				} else {
					if (dist < d[s.y-1][s.x]) {
						d[s.y-1][s.x] = dist;
					}
				}
			}
			if (s.x+1 < COLS && !visited[s.y][s.x+1] && (map[s.y][s.x+1] == null || map[s.y][s.x+1].getTeam().equals(team))) {
				if (p[s.y][s.x+1] == null) {
					d[s.y][s.x+1] = dist;
					p[s.y][s.x+1] = s;
					q.addLast(new Loc(s.y, s.x+1));
				} else {
					if (dist < d[s.y][s.x+1]) {
						d[s.y][s.x+1] = dist;
					}
				}
			}
			if (s.y+1 < ROWS && !visited[s.y+1][s.x] && (map[s.y+1][s.x] == null || map[s.y+1][s.x].getTeam().equals(team))) {
				if (p[s.y+1][s.x] == null) {
					d[s.y+1][s.x] = dist;
					p[s.y+1][s.x] = s;
					q.addLast(new Loc(s.y+1, s.x));
				} else {
					if (dist < d[s.y+1][s.x]) {
						d[s.y+1][s.x] = dist;
					}
				}
			}
			if (s.x-1 >= 0 && !visited[s.y][s.x-1] && (map[s.y][s.x-1] == null || map[s.y][s.x-1].getTeam().equals(team))) {
				if (p[s.y][s.x-1] == null) {
					d[s.y][s.x-1] = dist;
					p[s.y][s.x-1] = s;
					q.addLast(new Loc(s.y, s.x-1));
				} else {
					if (dist < d[s.y][s.x-1]) {
						d[s.y][s.x-1] = dist;
					}
				}
			}
			s = q.removeFirst();
			visited[s.y][s.x] = true; 
		}

		while (s != null) {
			path.add(0, new Integer[] {s.y, s.x});
			s = p[s.y][s.x];
		}
		return path;
	}

	/**
	 * Create objects for storing coordinates. Used for finding the shortest path.
	 */
	private class Loc{
		Loc(int y, int x) {
			this.x = x;
			this.y = y;
		}
		int x;
		int y;
	}

	/**
	 * Update characters' HP
	 */
	private void updateStats() {
		Character[] chars = controller.getCharacters();
		for (int i = 0; i < 10; i++) {
			if (chars[i] == null) {
				healthLabel[i].setText(0 + "HP");
			} else {
				healthLabel[i].setText(chars[i].getHP() + "HP");
			}
		}
	}

	/**
	 * Translate the column coordinate of a character to its X coordinate in px.
	 * 
	 * @param x - column coordinate to translate
	 * @return X coordinate in px
	 */
	private double getCharCoordX(int x) {
		return 178+40*x; //178 is the offset from the left edge of the scene to column 0
	}

	/**
	 * Translate the row coordinate of a character to its Y coordinate in px.
	 * 
	 * @param y - the row coordinate to translate
	 * @return Y coordinate in px
	 */
	private double getCharCoordY(int y) {
		return 28+40*y; //28 is the offset from the top edge of the scene to row 0
	}

	/**
	 * Translate a sprite's y coordinate to the row coordinate on the grid
	 * 
	 * @return row coordinate as an integer
	 */
	private int getSpriteCoordRow(double y) {
		return (int)(y-28)/40; //28 is the offset from the top edge of the scene to row 0
	}

	/**
	 * Translate a sprite's x coordinate to the row coordinate on the grid
	 * 
	 * @return row coordinate as an integer
	 */
	private int getSpriteCoordCol(double x) {
		return (int)(x-178)/40; //178 is the offset from the left edge of the scene to column 0
	}

}
