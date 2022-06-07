//Name: Alvin Jian & Tyler Zeng
//Date: January 01, 2021
//Assignment: 2048 Final ISU Assignment
//Description: A game of 2048 created from scratch. Has the same features as the original 2048 with some added features such as music, skin selection, and hard mode

import java.awt.event.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import javax.swing.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class GameOf2048 extends JPanel implements Runnable, KeyListener, ActionListener, MouseListener {

	//JFrame
	static JFrame frame = new JFrame();

	//Window variables
	int FPS = 60;
	Thread thread;

	//States of the game
	final static int start = 0;
	final static int game = 1;
	final static int help = 2;
	final static int help2 = 3;
	final static int gameOver = 4;
	final static int skinScreen = 5;
	final static int win = 6;

	//Skins
	final static int defaultSkin = 0;
	final static int MLGSkin = 1;
	int skinState = 0;

	//Game state
	int gameState = start;

	//Press
	boolean isPressed = false;

	//Hardmode
	boolean hardMode = false;
	int turnCounter = 0;

	//Board
	int boardWidth = 4;
	int board[][] = new int [boardWidth][boardWidth];;

	//Scoring system
	static int highScore;
	static int score;

	//Colors for the board
	private Color gridColor = new Color(0xBBADA0);
	private Color emptyColor = new Color(0xCDC1B4);
	private Color startColor = new Color(0xFFEBCD);
	private Color tan = new Color(210,180,140);

	//Audio
	Clip backGroundSound;
	AudioClip soundFx, explosion;
	boolean isAudio = false;

	//Images
	Image o2, o4, o8, o16, o32, o64, o128, o256, o512, o1024, o2048, o4096, o8192;
	Image v2, v4, v8, v16, v32, v64, v128, v256, v512, v1024, v2048;
	Image helpPic, helpPic2;

	//Menu bar
	JMenuBar menuBar = new JMenuBar();

	//Game menu
	JMenu gameMenu, audioMenu, hardMenu, audioMenu2;
	JMenuItem newGame, exit;
	JMenuItem on, off, mon, moff;
	JMenuItem hardOn, hardOff;

	//For drawing images off screen
	Image offScreenImage;
	Graphics offScreenBuffer;

	public GameOf2048() {
		//Audio
		soundFx = Applet.newAudioClip (getCompleteURL ("soundFx.wav"));
		explosion = Applet.newAudioClip (getCompleteURL ("explosion.wav"));
		try {
			AudioInputStream sound = AudioSystem.getAudioInputStream(new File ("music.wav"));
			backGroundSound= AudioSystem.getClip();
			backGroundSound.open(sound);
		} 
		catch (Exception e) {
		}

		//Window
		setPreferredSize(new Dimension(900, 700));
		setBackground(new Color(0xFAF8EF));
		setFont(new Font("SansSerif", Font.BOLD, 48));
		setFocusable(true);

		//Game menu
		gameMenu = new JMenu("Game Menu");
		newGame = new JMenuItem("New Game");
		exit = new JMenuItem("Exit");

		gameMenu.add(newGame);
		gameMenu.add(exit);
		gameMenu.setVisible(true);

		//Audio menu
		audioMenu = new JMenu("Sound Effect Menu");
		on = new JMenuItem("On");
		off = new JMenuItem("Off");

		audioMenu.add(on);
		audioMenu.add(off);
		audioMenu.setVisible(true);

		audioMenu2 = new JMenu("Music Menu");
		mon = new JMenuItem("On");
		moff = new JMenuItem("Off");
		audioMenu2.add(mon);
		audioMenu2.add(moff);
		audioMenu2.setVisible(true);

		//Hard menu
		hardMenu = new JMenu("Difficulty Menu");
		hardOn = new JMenuItem("On");
		hardOff = new JMenuItem("Off");

		hardMenu.add(hardOn);
		hardMenu.add(hardOff);
		hardMenu.setVisible(false);

		//Adding menu to menu bar
		menuBar.add(gameMenu);
		menuBar.add(audioMenu);
		menuBar.add(audioMenu2);
		menuBar.add(hardMenu);
		//Adding menu bar to frame
		frame.setJMenuBar(menuBar);

		//Sets action listener to the JMenu buttons
		newGame.setActionCommand("New");
		newGame.addActionListener(this);
		exit.setActionCommand("Exit");
		exit.addActionListener(this);
		on.setActionCommand("On");
		on.addActionListener(this);
		off.setActionCommand("Off");
		off.addActionListener(this);
		mon.setActionCommand("MOn");
		mon.addActionListener(this);
		moff.setActionCommand("MOff");
		moff.addActionListener(this);
		hardOn.setActionCommand("hardOn");
		hardOn.addActionListener(this);
		hardOff.setActionCommand("hardOff");
		hardOff.addActionListener(this);

		//Adds images through media tracker
		//Default skins
		MediaTracker tracker = new MediaTracker (this);
		o2 = Toolkit.getDefaultToolkit ().getImage ("2.png");
		tracker.addImage (o2, 0);
		o4 = Toolkit.getDefaultToolkit ().getImage ("4.png");
		tracker.addImage (o4, 0);
		o8 = Toolkit.getDefaultToolkit ().getImage ("8.png");
		tracker.addImage (o8, 0);
		o16 = Toolkit.getDefaultToolkit ().getImage ("16.png");
		tracker.addImage (o16, 0);
		o32 = Toolkit.getDefaultToolkit ().getImage ("32.png");
		tracker.addImage (o32, 0);
		o64 = Toolkit.getDefaultToolkit ().getImage ("64.png");
		tracker.addImage (o64, 0);
		o128 = Toolkit.getDefaultToolkit ().getImage ("128.png");
		tracker.addImage (o128, 0);
		o256 = Toolkit.getDefaultToolkit ().getImage ("256.png");
		tracker.addImage (o256, 0);
		o512 = Toolkit.getDefaultToolkit ().getImage ("512.png");
		tracker.addImage (o512, 0);
		o1024= Toolkit.getDefaultToolkit ().getImage ("1024.png");
		tracker.addImage (o1024, 0);
		o2048= Toolkit.getDefaultToolkit ().getImage ("2048.png");
		tracker.addImage (o2048, 0);
		o4096= Toolkit.getDefaultToolkit ().getImage ("4096.png");
		tracker.addImage (o4096, 0);
		o8192= Toolkit.getDefaultToolkit ().getImage ("8192.png");
		tracker.addImage (o8192, 0);

		//MLG Skins
		v2 = Toolkit.getDefaultToolkit ().getImage ("v2.png");
		tracker.addImage (v2, 0);
		v4 = Toolkit.getDefaultToolkit ().getImage ("v4.png");
		tracker.addImage (v4, 0);
		v8 = Toolkit.getDefaultToolkit ().getImage ("v8.png");
		tracker.addImage (v8, 0);
		v16 = Toolkit.getDefaultToolkit ().getImage ("v16.png");
		tracker.addImage (v16, 0);
		v32 = Toolkit.getDefaultToolkit ().getImage ("v32.png");
		tracker.addImage (v32, 0);
		v64 = Toolkit.getDefaultToolkit ().getImage ("v64.png");
		tracker.addImage (v64, 0);
		v128 = Toolkit.getDefaultToolkit ().getImage ("v128.png");
		tracker.addImage (v128, 0);
		v256 = Toolkit.getDefaultToolkit ().getImage ("v256.png");
		tracker.addImage (v256, 0);
		v512 = Toolkit.getDefaultToolkit ().getImage ("v512.png");
		tracker.addImage (v512, 0);
		v1024 = Toolkit.getDefaultToolkit ().getImage ("v1024.png");
		tracker.addImage (v1024, 0);
		v2048 = Toolkit.getDefaultToolkit ().getImage ("v2048.png");
		tracker.addImage (v2048, 0);

		//Help screen pictures
		helpPic = Toolkit.getDefaultToolkit ().getImage ("help.png");
		tracker.addImage (helpPic, 0);
		helpPic2 = Toolkit.getDefaultToolkit ().getImage ("help2.png");
		tracker.addImage (helpPic2, 0);

		// Need this to set the focus to the panel in order to add the keyListener
		setFocusable (true); 
		addKeyListener (this);
		//starting the thread
		thread = new Thread(this);
		thread.start();
	}

	//Method name: getHighScore
	//Description: Gets the locally saved high score
	//Parameters: nothing
	//Returns: void
	public static void getHighScore() throws IOException{
		Scanner in = new Scanner (new File("highScore.txt"));	
		while (in.hasNextInt ()) {
			highScore = in.nextInt ();
		}
		in.close ();
	}

	//Method name: recordHighScore
	//Description: Saves the high score
	//Parameters: nothing
	//Returns: void
	public static void recordHighScore() throws IOException{
		PrintWriter out = new PrintWriter (new FileWriter ("highScore.txt", true));
		out.println (score);
		out.close ();
	}

	//Method: Audio
	//Parameters: Takes in a string; the file name
	//Description: Takes in a file location string of a file
	//Returns: Returns a url 
	public URL getCompleteURL (String fileName)
	{
		try{
			return new URL ("file:" + System.getProperty ("user.dir") + "/" + fileName);
		}
		catch (MalformedURLException e)	{
			System.err.println (e.getMessage ());
		}
		return null;
	}

	//USER INPUTS////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void actionPerformed(ActionEvent event) {
		String eventName = event.getActionCommand ();
		if (eventName.equals ("New")){
			resetBoard(board);
			score = 0;
		}
		else if (eventName.equals("Exit")) {
			System.exit(0);
		}
		else if(eventName.equals("On")) {
			isAudio = true;
		}
		else if(eventName.equals("Off")) {
			isAudio = false;
		}
		else if(eventName.equals("MOn")) {
			backGroundSound.loop(backGroundSound.LOOP_CONTINUOUSLY);
		}
		else if(eventName.equals("MOff")) {
			backGroundSound.stop();
		}
		else if(eventName.equals("hardOn")) {
			hardMode = true;
		}
		else if(eventName.equals("hardOff")) {
			hardMode = false;
		}

	}

	//Keypresses for different menu screens
	public void keyPressed(KeyEvent event) {
		//Game screen
		if(gameState == game) {
			if (event.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				gameState = start;
				hardMenu.setVisible(false);
			}
			if(!isPressed) {
				//Up
				if (event.getKeyCode() == KeyEvent.VK_UP) {
					checkUp(board);
					if(fullBoard(board)) {
						if(!hasMove(board)) {
							gameState = gameOver;
						}
					}
					if(turnCounter == 7 && hardMode == true) {
						explosion(board);
					}
					if(checkWin(board)) {
						gameState = win;
					}
					isPressed = true;
				}
				//Down
				else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
					checkDown(board);	
					if(fullBoard(board)) {
						if(!hasMove(board)) {
							gameState = gameOver;
						}
					}
					if(turnCounter == 7 && hardMode == true) {
						explosion(board);
					}
					if(checkWin(board)) {
						gameState = win;
					}
					isPressed = true;
				}
				//Left
				else if (event.getKeyCode() == KeyEvent.VK_LEFT) {
					checkLeft(board);
					if(fullBoard(board)) {
						if(!hasMove(board)) {
							gameState = gameOver;
						}
					}
					if(turnCounter == 7 && hardMode == true) {
						explosion(board);
					}
					if(checkWin(board)) {
						gameState = win;
					}
					isPressed = true;
				}
				//Right
				else if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
					checkRight(board);
					if(fullBoard(board)) {
						if(!hasMove(board)) {
							gameState = gameOver;
						}
					}
					if(turnCounter == 7 && hardMode == true) {
						explosion(board);
					}
					if(checkWin(board)) {
						gameState = win;
					}
					isPressed = true;
				}
			}

		}
		//Menu screen
		else if(gameState == start) {
			if (event.getKeyCode() == KeyEvent.VK_SPACE) {
				gameState = game;
				hardMenu.setVisible(true);
			}
			else if (event.getKeyCode() == KeyEvent.VK_H) {
				gameState = help;
			}
			else if (event.getKeyCode() == KeyEvent.VK_S) {
				gameState = skinScreen;
			}
		}

		//Help screen
		else if(gameState == help) {
			if (event.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				gameState = start;
			}
			if(event.getKeyCode() == KeyEvent.VK_RIGHT) {
				gameState = help2;
			}
		}
		else if(gameState == help2) {
			if (event.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				gameState = start;
			}
			if(event.getKeyCode() == KeyEvent.VK_LEFT) {
				gameState = help;
			}
		}
		//Skin screen
		else if(gameState == skinScreen) {
			if (event.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				gameState = start;
			}
			else if (event.getKeyCode() == KeyEvent.VK_LEFT) {
				skinState = defaultSkin;
				gameState = start;
			}
			else if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
				skinState = MLGSkin;
				gameState = start;
			}
		}
		//Game over screen
		else if(gameState == gameOver) {
			if (event.getKeyCode() == KeyEvent.VK_Y && score > highScore) {
				try {
					recordHighScore();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				gameState = start;
				resetBoard(board);
			}
			else if (event.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				gameState = start;
				resetBoard(board);
			}
		}

		//Win Screen
		else if (gameState == win) {
			if (event.getKeyCode() == KeyEvent.VK_Y) {
				gameState = game;
			}
			else if (event.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				gameState = start;
				resetBoard(board);
			}
		}

	}
	@Override
	//Used so user doesn't accidentally hold on key to move
	public void keyReleased(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_UP || event.getKeyCode() == KeyEvent.VK_DOWN || event.getKeyCode() == KeyEvent.VK_LEFT || event.getKeyCode() == KeyEvent.VK_RIGHT) {
			isPressed = false;
		}
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
	}
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	@Override
	public void run() {
		try {
			initialize();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(true) {
			//main game loop
			update();
			this.repaint();
			try {
				Thread.sleep(1000/FPS);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void initialize() throws IOException {
		resetBoard(board);
		getHighScore();
		score = 0;
	}
	public void update() {
	}
	//LOGIC METHODS/////////////////////////////////////////////////////////////////////////////////////////////

	//Method name: checkWin
	//Description: Checks if user wins
	//Parameters: Takes in a 2 dimensional array
	//Returns: boolean
	public boolean checkWin (int[][] board) {

		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				if(board[i][j] == 2048) {
					return true;
				}
			}
		}		
		return false;
	}

	//Method name: checkBoard
	//Description: Checks previous version of the board
	//Parameters: Takes in a 2 dimensional array
	//Returns: Returns the same array
	public int[][] checkBoard(int board[][]){	
		int[][] newBoard = new int[4][4];
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				newBoard[i][j] = board[i][j];
			}
		}
		return newBoard;
	}

	//Method name: Reset board
	//Description: Clears the board when user wants to reset game or when a game has restarted. Also sets a random spot for the number 2 to be placed
	//Parameters: Takes in a 2 dimensional array (rectangular array)
	//Return: void (modifies the array so that all elements within the 2-d array is zero)
	public void resetBoard(int board[][]) {
		score=0;
		turnCounter = 0;
		try {
			getHighScore();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		turnCounter = 0;
		for(int row = 0; row < board.length; row++) {
			for (int col = 0; col < board [row].length; col++) {
				board [row][col] = 0;
			}
		}
		//Random position 
		for (int i=0;i<=1;i++) {
			int randomRow = (int) (Math.random() * (boardWidth  - 0 + 0 ) + 0);
			int randomColumn = (int) (Math.random() * (boardWidth  - 0 + 0) + 0);
			while (board[randomRow][randomColumn]!=0) {
				randomRow = (int) (Math.random() * (boardWidth  - 0 + 0 ) + 0);
				randomColumn = (int) (Math.random() * (boardWidth  - 0 + 0) + 0);
			}
			int twoOrFour = (int)(Math.random() * (10 - 1 + 1) + 1);
			//Starts with either 2 or 4
			if(twoOrFour >= 1 && twoOrFour<10) {
				board[randomRow][randomColumn] = 2;
			}
			else if(twoOrFour == 10) {
				board[randomRow][randomColumn] = 4;
			}	
		}
	}

	//Method name: Place random
	//Description: After each turn place a random tile on a free tile
	//Parameterrs: Takes in the board
	//Returns: void (modifies the board through reference)
	public void placeRandom(int board [][]) {
		//Doesn't place if board is full
		if(fullBoard(board) == true) {
			return;
		}
		int randomRow = (int) (Math.random() * (boardWidth  - 0 + 0) + 0);
		int randomColumn = (int) (Math.random() * (boardWidth  - 0 + 0) + 0);

		while(board[randomRow][randomColumn] != 0) {
			randomRow = (int) (Math.random() * (boardWidth  - 0 + 0) + 0);
			randomColumn = (int) (Math.random() * (boardWidth  - 0 + 0) + 0);			
		}

		int twoOrFour = (int)(Math.random() * (10 - 1 + 1) + 1);
		//Starts with either 2 or 4
		if(twoOrFour >= 1 && twoOrFour<10) {
			board[randomRow][randomColumn] = 2;
		}
		else if(twoOrFour == 10) {
			board[randomRow][randomColumn] = 4;
		}	
		//Hard mode
		if(!fullBoard(board)) {
			if(hardMode == true && turnCounter == 0) {	
				int ifBomb = (int) (Math.random() * (20 - 1 + 1) + 1);
				if(ifBomb == 2) {
					int randomR = (int) (Math.random() * (boardWidth - 0 + 0) + 0);
					int randomC = (int) (Math.random() * (boardWidth - 0 + 0) + 0);
					while(board[randomR][randomC] != 0) {
						randomR = (int) (Math.random() * (boardWidth - 0 + 0) + 0);
						randomC = (int) (Math.random() * (boardWidth - 0 + 0) + 0);			
					}
					board[randomR][randomC] = -1;
					turnCounter= 1;
				}
			}
		}
	}

	//Method name: Merge up
	//Description: Checks if the user can combine pieces moving up
	//Parameters: Takes in the board
	//Returns: void (modifies board through reference)
	public void checkUp(int board [][]) {
		int[][] newBoard = checkBoard(board);
		boolean[] hasMerged = {false,false,false,false};
		boolean[] ticket = {false,false,false,false};
		for (int e=0;e<4;e++) {
			int count=0;
			while (count<4) {
				for (int i=1;i<4;i++) {
					if (board[i][e]==board[i-1][e] && board[i][e]!=0 && hasMerged[e]==false) {
						board[i-1][e]=board[i][e]*2;
						board[i][e]=0;
						score+=board[i-1][e];
						hasMerged[e]=true;
					}
					else if (i==2 && ticket[e]==false && board[i][e]==board[i+1][e] && board[i-1][e]==board[i][e]*2 && board[i-2][e]!=board[i-1][e] && board[i][e]!=0 && board[i-1][e]!=0 && board[i-2][e]!=0) {
						board[i][e]=board[i][e]*2;
						board[i+1][e]=0;
						score+=board[i][e];
						hasMerged[e]=true;
						ticket[e]=true;
					}
					else if (board[i-1][e]==0) {
						board[i-1][e]=board[i][e];
						board[i][e]=0;
					}
					else if (i==2 && board[i][e]==board[i-1][e] && board[i][e]!=0 && hasMerged[e]==true && ticket[e]==false) {
						board[i-1][e]=board[i][e]*2;
						board[i][e]=0;
						score+=board[i-1][e];
					}
				}
				count++;
			}
		}
		if(!Arrays.equals(newBoard, board)) {
			placeRandom(board);
			if(hardMode == true && turnCounter > 0) {
				turnCounter++;
			}
			if(isAudio) {
				soundFx.play();
			}
		}
	}

	//Method name: Merge down
	//Description: Checks if the user can combine pieces moving down
	//Parameters: Takes in the board
	//Returns: void (modifies board through reference)
	public void checkDown(int board [][]) {
		int[][] newBoard = checkBoard(board);
		boolean[] hasMerged = {false,false,false,false};
		boolean[] ticket = {false,false,false,false};
		for (int e=0;e<4;e++) {//column
			int count=0;
			while (count<4) {//forces to connect
				for (int i=2;i>=0;i--) {//row
					if (board[i][e]==board[i+1][e] && board[i][e]!=0 && hasMerged[e]==false){
						board[i+1][e]=board[i][e]*2;
						board[i][e]=0;
						score+=board[i+1][e];
						hasMerged[e]=true;
					}
					else if (i==1 && board[i][e]==board[i-1][e] && board[i+1][e]==board[i][e]*2 && board[i+2][e]!=board[i+1][e] &&board[i][e]!=0 &&board[i+1][e]!=0 && board[i+2][e]!=0 && ticket[e]==false) {
						board[i][e]=board[i][e]*2;
						board[i-1][e]=0;
						score+=board[i][e];
						hasMerged[e]=true;
						ticket[e]=true;
					}
					else if (i==1 && hasMerged[e]==true && board[i][e]==board[i+1][e]&& board[i][e]!=0 && ticket[e]==false) {
						board[i+1][e]=board[i][e]*2;
						board[i][e]=0;
						score+=board[i+1][e];
					}
					else if (board[i+1][e]==0) {
						board[i+1][e]=board[i][e];
						board[i][e]=0;
					}
				}
				count++;
			}
		}
		if(!Arrays.deepEquals(newBoard,board)) {
			placeRandom(board);
			if(hardMode == true && turnCounter > 0) {
				turnCounter++;
			}
			if(isAudio) {
				soundFx.play();
			}
		}
	}

	//Method name: Merge left
	//Description: Checks if the user can combine pieces moving left
	//Parameters: Takes in the board
	//Returns: void (modifies board through reference)
	public void checkLeft(int board [][]) {
		int[][] newBoard = checkBoard(board);
		boolean [] hasMerged = {false,false,false,false};
		boolean[] ticket = {false,false,false,false};
		for (int i=0;i<4;i++) {
			int count=0;
			while (count<4) {
				for (int e=1;e<4;e++) {
					if (board[i][e-1]==board[i][e] && board[i][e]!=0 && hasMerged[i]==false) {
						board[i][e-1]=board[i][e]*2;
						board[i][e]=0;
						score+=board[i][e-1];
						hasMerged[i]=true;
					}
					else if (hasMerged[i]==true && ticket[i]==false &&board[i][e]!=0 && board[i][e]==board[i][e-1] && e==2) {
						board[i][e-1]=board[i][e]*2;
						board[i][e]=0;
						score+=board[i][e-1];
					}
					else if (e==2 && board[i][e]==board[i][e+1] && board[i][e-1]==board[i][e]*2 && board[i][e-2]!=board[i][e-1] && board[i][e]!=0 && board[i][e-1]!=0 && board[i][e-2]!=0 && ticket[i]==false) {
						board[i][e]=board[i][e]*2;
						board[i][e+1]=0;
						score+=board[i][e];
						hasMerged[i]=true;
						ticket[i]=true;
					}
					else if (board[i][e-1]==0) {
						board[i][e-1]=board[i][e];
						board[i][e]=0;
					}
				}
				count++;
			}
		}
		if(!Arrays.deepEquals(newBoard,board)) {
			placeRandom(board);
			if(hardMode == true && turnCounter > 0) {
				turnCounter++;
			}
			if(isAudio) {
				soundFx.play();
			}
		}
	}

	//Method name: Merge right
	//Description: Checks if the user can combine pieces moving right
	//Parameters: Takes in the board
	//Returns: void (modifies board through reference)
	public void checkRight(int board [][]) {
		int[][] newBoard = checkBoard(board);
		boolean [] hasMerged = {false,false,false,false};
		boolean[] ticket = {false,false,false,false};
		for (int i=0;i<4;i++) {
			int count=0;
			while (count<4) {
				for (int e=2;e>=0;e--) {
					if (board[i][e+1]==board[i][e] && board[i][e]!=0 && hasMerged[i]==false) {
						board[i][e+1]=board[i][e]*2;
						board[i][e]=0;
						score+=board[i][e+1];
						hasMerged[i]=true;
					}
					else if (board[i][e+1]==0) {
						board[i][e+1]=board[i][e];
						board[i][e]=0;
					}
					else if (ticket[i]==false && hasMerged[i]==true && board[i][e]!=0 && board[i][e]==board[i][e+1] && e==1) {
						board[i][e+1]=board[i][e]*2;
						board[i][e]=0;
						score+=board[i][e+1];
					}
					else if (e==1 && ticket[i]==false && board[i][e]==board[i][e-1] && board[i][e+1]==board[i][e]*2 && board[i][e+2]!=board[i][e+1] && board[i][e]!=0 && board[i][e+1]!=0 &&  board[i][e+2]!=0) {
						board[i][e]=board[i][e]*2;
						board[i][e-1]=0;
						score+=board[i][e];
						hasMerged[i]=true;
						ticket[i]=true;
					}
				}
				count++;
			}
		}
		if(!Arrays.deepEquals(newBoard,board)) {
			placeRandom(board);
			if(hardMode == true && turnCounter > 0) {
				turnCounter++;
			}
			if(isAudio) {
				soundFx.play();
			}
		}
	}

	//Method name: explosion
	//Description: Bomb explodes 
	//Parameters: Takes in the board
	//Returns: void
	public void explosion(int[][]board) {
		for (int i=0;i<4;i++) {
			for (int e=0;e<4;e++) {
				if (board[i][e]==-1) {
					board[i][e]=0;
					if (i+1!=4) {
						board[i+1][e]=0;
					}
					if (i-1!=-1) {
						board[i-1][e]=0;
					}
					if (e+1!=4) {
						board[i][e+1]=0;
					}
					if (e-1!=-1) {
						board[i][e-1]=0;
					}
					if (i+1<4 && i-1>-1 && e+1<4 && e-1>-1) {
						board[i+1][e]=0;
						board[i-1][e]=0;
						board[i][e+1]=0;
						board[i][e-1]=0;
					}
				}
			}
		}
		explosion.play();
		turnCounter=0;
	}	


	//Method name: fullBoard
	//Description: Checks if the board is full
	//Parameters: Takes in the board
	//Returns: void (modifies board through reference)
	public boolean fullBoard(int board[][]) {
		int count = 0;
		for(int row = 0; row < 4; row++) {
			for(int col = 0; col < 4; col++) {
				if(board[row][col] != 0) {
					count++;
				}
			}
		}
		if(count != 16) {
			return false;
		}
		else {
			return true;
		}
	}

	//Method name: hasMove
	//Description: Checks if the player has any moves
	//Parameters: Takes in the board
	//Returns: void (modifies board through reference)
	public boolean hasMove (int board[][]) {
		for(int x = 0; x < 4; x++) {
			for(int y = 0; y < 4; y++) {

				if(x == 0) {
					if(y != 0) {
						if(board[x][y] == board[x][y-1]) {
							return true;
						}
					}
				}
				else {
					if(y != 0) {
						if(board[x][y] == board[x][y-1]) {
							return true;
						}
					}
					if(board[x][y] == board[x - 1][y]) {
						return true;
					}
				}	
			}	
		}	
		return false;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Paint component
	//Painting the actual graphics onto the screen
	public void paintComponent(Graphics g) {
		if (offScreenBuffer == null) {
			offScreenImage = createImage (this.getWidth (), this.getHeight ());
			offScreenBuffer = offScreenImage.getGraphics ();
		}
		offScreenBuffer.clearRect (0, 0, this.getWidth (), this.getHeight ());
		offScreenBuffer.setColor(gridColor);
		offScreenBuffer.fillRoundRect(200, 100, 499, 499, 15, 15);
		//Main game
		if (gameState == game) {
			offScreenBuffer.setColor(tan);
			offScreenBuffer.fillRect(0, 0, 900, 700);
			offScreenBuffer.setColor(gridColor);
			offScreenBuffer.fillRoundRect(200, 60, 499, 499, 15, 15);

			//Game board
			for (int r = 0; r < boardWidth; r++) {
				for (int c = 0; c < boardWidth; c++) {

					int x = 240 + c * 106;
					int y = 100 + r * 106;
					offScreenBuffer.setColor(emptyColor);
					offScreenBuffer.fillRoundRect(x, y, 100, 100, 7, 7);

					//Default skin
					if(skinState == 0) {
						if(board[r][c] == -1) {
							offScreenBuffer.setColor(Color.black);
							offScreenBuffer.fillRoundRect(x, y, 100, 100, 7, 7);
						}
						if(board[r][c] == 2) {
							offScreenBuffer.drawImage (o2, x, y, 100, 100, this);
						}
						else if(board[r][c] == 4) {
							offScreenBuffer.drawImage (o4, x, y, 100, 100, this);
						}			
						else if(board[r][c] == 8) {
							offScreenBuffer.drawImage (o8, x, y, 100, 100, this);
						}			
						else if(board[r][c] == 16) {
							offScreenBuffer.drawImage (o16, x, y, 100, 100, this);
						}	
						else if(board[r][c] == 32) {
							offScreenBuffer.drawImage (o32, x, y, 100, 100, this);
						}	
						else if(board[r][c] == 64) {
							offScreenBuffer.drawImage (o64, x, y, 100, 100, this);
						}	
						else if(board[r][c] == 128) {
							offScreenBuffer.drawImage (o128, x, y, 100, 100, this);
						}	
						else if(board[r][c] == 256) {
							offScreenBuffer.drawImage (o256, x, y, 100, 100, this);
						}	
						else if(board[r][c] == 512) {
							offScreenBuffer.drawImage (o512, x, y, 100, 100, this);
						}	
						else if(board[r][c] == 1024) {
							offScreenBuffer.drawImage (o1024, x, y, 100, 100, this);
						}	
						else if(board[r][c] == 2048) {
							offScreenBuffer.drawImage (o2048, x, y, 100, 100, this);
						}	
						else if(board[r][c] == 4096) {
							offScreenBuffer.drawImage (o4096, x, y, 100, 100, this);
						}	
						else if(board[r][c] == 8192) {
							offScreenBuffer.drawImage (o8192, x, y, 100, 100, this);
						}	
					}
					//MLG Skin
					else if(skinState == 1) {
						if(board[r][c] == -1) {
							offScreenBuffer.setColor(Color.black);
							offScreenBuffer.fillRoundRect(x, y, 100, 100, 7, 7);
						}
						if(board[r][c] == 2) {
							offScreenBuffer.drawImage (v2, x, y, 100, 100, this);
						}
						else if(board[r][c] == 4) {
							offScreenBuffer.drawImage (v4, x, y, 100, 100, this);
						}
						else if(board[r][c] == 8) {
							offScreenBuffer.drawImage (v8, x, y, 100, 100, this);
						}
						else if(board[r][c] == 16) {
							offScreenBuffer.drawImage (v16, x, y, 100, 100, this);
						}
						else if(board[r][c] == 32) {
							offScreenBuffer.drawImage (v32, x, y, 100, 100, this);
						}	
						else if(board[r][c] == 64) {
							offScreenBuffer.drawImage (v64, x, y, 100, 100, this);
						}	
						else if(board[r][c] == 128) {
							offScreenBuffer.drawImage (v128, x, y, 100, 100, this);
						}	
						else if(board[r][c] == 256) {
							offScreenBuffer.drawImage (v256, x, y, 100, 100, this);
						}	
						else if(board[r][c] == 512) {
							offScreenBuffer.drawImage (v512, x, y, 100, 100, this);
						}	
						else if(board[r][c] == 1024) {
							offScreenBuffer.drawImage (v1024, x, y, 100, 100, this);
						}	
						else if(board[r][c] == 2048) {
							offScreenBuffer.drawImage (v2048, x, y, 100, 100, this);
						}	
					}
				}
			}

			//Scoreboard
			//Scoreboard
			offScreenBuffer.setColor(Color.darkGray);
			offScreenBuffer.fillRoundRect(200, 580, 500, 100, 7, 7);
			offScreenBuffer.setColor(gridColor);
			offScreenBuffer.fillRoundRect(210, 595, 225, 70, 7, 7);
			offScreenBuffer.fillRoundRect(462, 595, 225, 70, 7, 7);
			offScreenBuffer.setColor(Color.darkGray);
			offScreenBuffer.setFont(new Font("Monospaced", Font.BOLD, 20));
			offScreenBuffer.drawString("Score", 290, 620);
			offScreenBuffer.drawString(Integer.toString(score), 290, 645);
			offScreenBuffer.drawString("Highscore", 525, 620);
			if(score > highScore) {
				offScreenBuffer.drawString(Integer.toString(score), 525, 645);
			}
			else {
				offScreenBuffer.drawString(Integer.toString(highScore), 525, 645);
			}
		}

		//Starting screen
		else if(gameState == start) {
			offScreenBuffer.setColor(tan);
			offScreenBuffer.fillRect(0, 0, 900, 700);
			offScreenBuffer.setColor(startColor);
			offScreenBuffer.fillRoundRect(200, 100, 499, 499, 15, 15);
			offScreenBuffer.setColor(startColor);
			offScreenBuffer.fillRoundRect(215, 115, 469, 469, 7, 7);
			offScreenBuffer.setColor(gridColor.darker());
			offScreenBuffer.setFont(new Font("SansSerif", Font.BOLD, 128));
			offScreenBuffer.drawString("2048", 310, 270);
			offScreenBuffer.setFont(new Font("SansSerif", Font.BOLD, 20));
			offScreenBuffer.setColor(gridColor);
			offScreenBuffer.drawString("press space to start game", 330, 470);
			offScreenBuffer.drawString("press 'h' for help", 330, 500);
			offScreenBuffer.drawString("press 's' to choose a skin", 330, 530);
			offScreenBuffer.drawString("Created by: Alvin Jian & Tyler Zeng", 300, 585);
		}

		//Help/credit screen
		else if(gameState == help) {
			offScreenBuffer.setColor(tan);
			offScreenBuffer.fillRect(0, 0, 900, 700);
			offScreenBuffer.drawImage (helpPic, 290, 100, 300, 300, this);
			offScreenBuffer.setColor(Color.black);
			offScreenBuffer.setFont(new Font("SansSerif", Font.BOLD, 15));
			offScreenBuffer.drawString("2048 is often played on a gray 4×4 grid, with numbered tiles that slide when a player moves them using the four arrow keys.", 25, 450);
			offScreenBuffer.drawString("Every turn, a new tile will randomly appear in an empty spot on the board with a value of either 2 or 4.", 85, 475);
			offScreenBuffer.drawString("Tiles slide as far as possible in the chosen direction until they are stopped by either another tile or the edge of the grid.", 30, 500);
			offScreenBuffer.drawString("If two tiles of the same number collide while moving, they will merge into a a single tile with both values combined", 40, 525);
			offScreenBuffer.drawString("Use arrow keys in order to move the tiles", 300, 550);
			offScreenBuffer.drawString("Right arrow to go next page", 350, 610);
			offScreenBuffer.drawString("Press backspace to go back", 350, 650);
			offScreenBuffer.setColor(Color.gray);
			offScreenBuffer.drawString("Press backspace to go back", 351, 651);
			offScreenBuffer.drawString("Right arrow to go next page", 351, 611);
		}
		//Second help screen
		else if(gameState == help2) {
			offScreenBuffer.setColor(tan);
			offScreenBuffer.fillRect(0, 0, 900, 700);
			offScreenBuffer.drawImage (helpPic2, 290, 100, 300, 300, this);
			offScreenBuffer.setColor(Color.black);
			offScreenBuffer.setFont(new Font("SansSerif", Font.BOLD, 15));
			offScreenBuffer.drawString("In hard mode, a bomb will have a 1/20 chance of spawning each turn.", 200, 450);
			offScreenBuffer.drawString("When the bomb spawns, you have 5 turns before the bomb explodes", 200, 475);
			offScreenBuffer.drawString("When the bomb explodes, all adjacent pieces will be removed", 220, 500);
			offScreenBuffer.drawString("Left arrow to go back to previous page", 300, 610);
			offScreenBuffer.drawString("Press backspace to go back", 350, 650);
			offScreenBuffer.setColor(Color.gray);
			offScreenBuffer.drawString("Press backspace to go back", 351, 651);
			offScreenBuffer.drawString("Left arrow to go back to previous page", 301, 611);
		}
		//Skin screen
		else if(gameState == skinScreen) {
			offScreenBuffer.setColor(tan);
			offScreenBuffer.fillRect(0, 0, 900, 700);
			offScreenBuffer.setColor(Color.gray);
			offScreenBuffer.fillRoundRect(205, 205, 150, 150, 7, 7);
			offScreenBuffer.fillRoundRect(555, 205, 150, 150, 7, 7);
			offScreenBuffer.drawImage (o2, 200,200, 150, 150, this);
			offScreenBuffer.drawImage (v2, 550, 200, 150, 150, this);
			//Skin name
			offScreenBuffer.setColor(Color.black);
			offScreenBuffer.setFont(new Font("SansSerif", Font.BOLD, 15));
			offScreenBuffer.drawString("Press left_arrow_key for default skin", 150, 400);
			offScreenBuffer.drawString("Press right_arrow_key for MLG skin", 500, 400);
			offScreenBuffer.drawString("Press backspace to go back", 350, 650);
			offScreenBuffer.setColor(Color.gray);
			offScreenBuffer.drawString("Press backspace to go back", 351, 651);

			offScreenBuffer.setColor(Color.black);
			offScreenBuffer.setFont(new Font("SansSerif", Font.BOLD, 13));
			offScreenBuffer.drawString("The original 2048 style", 207, 425);
			offScreenBuffer.drawString("Maybe try something new?", 550, 425);

		}
		//Game over screen
		else if(gameState == gameOver) {
			offScreenBuffer.setColor(tan);
			offScreenBuffer.fillRect(0, 0, 900, 700);
			offScreenBuffer.setColor(startColor);
			offScreenBuffer.fillRoundRect(200, 100, 499, 499, 15, 15);
			offScreenBuffer.setColor(startColor);
			offScreenBuffer.fillRoundRect(215, 115, 469, 469, 7, 7);
			offScreenBuffer.setColor(gridColor.darker());
			offScreenBuffer.setFont(new Font("SansSerif", Font.BOLD, 50));
			offScreenBuffer.drawString("GAME OVER", 300, 270);
			offScreenBuffer.setFont(new Font("SansSerif", Font.BOLD, 20));
			offScreenBuffer.drawString("Score: ", 350, 350);
			offScreenBuffer.drawString(Integer.toString(score), 500, 350);
			offScreenBuffer.drawString("High score: ", 350, 425);
			if(score > highScore) {
				offScreenBuffer.drawString(Integer.toString(score), 500, 425);
			}
			else {
				offScreenBuffer.drawString(Integer.toString(highScore), 500, 425);
			}
			if(score > highScore) {
				offScreenBuffer.setFont(new Font("SansSerif", Font.BOLD, 17));
				offScreenBuffer.setColor(gridColor.darker());
				offScreenBuffer.drawString("Highscore! Would you like to save? Press (Y) to save", 250, 500);			
			}
			offScreenBuffer.setColor(Color.gray);
			offScreenBuffer.drawString("Press backspace to go back to main menu", 275, 550);
		}

		//Win screen
		else if(gameState == win) {
			offScreenBuffer.setColor(tan);
			offScreenBuffer.fillRect(0, 0, 900, 700);
			offScreenBuffer.fillRect(0, 0, 900, 700);
			offScreenBuffer.setColor(startColor);
			offScreenBuffer.fillRoundRect(200, 100, 499, 499, 15, 15);
			offScreenBuffer.setColor(startColor);
			offScreenBuffer.fillRoundRect(215, 115, 469, 469, 7, 7);
			offScreenBuffer.setColor(gridColor.darker());
			offScreenBuffer.setColor(Color.gray);
			offScreenBuffer.fillRoundRect(395, 320, 110, 110, 15, 15);
			offScreenBuffer.setColor(tan);
			offScreenBuffer.setFont(new Font("SansSerif", Font.BOLD, 75));
			offScreenBuffer.drawString("You Won!", 275, 270);
			offScreenBuffer.drawImage (o2048, 400, 325, 100, 100, this);
			offScreenBuffer.setFont(new Font("SansSerif", Font.BOLD, 20));
			offScreenBuffer.drawString("Press 'Y' to continue or 'back_space' to restart", 230, 500);		
		}
		//Transfers final image onto screen
		g.drawImage (offScreenImage, 0, 0, this);
	}
	//Main method
	public static void main(String[] args) {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("2048");
		frame.setResizable(false);
		frame.add(new GameOf2048(), BorderLayout.CENTER);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
