import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

public class Tetris extends JPanel{

	private final Point[][][] Tetraminos = {
			// I-Piece
			{
				{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
				{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) },
				{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
				{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) }
			},

			// J-Piece
			{
				{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0) },
				{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2) },
				{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2) },
				{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 0) }
			},

			// L-Piece
			{
				{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2) },
				{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2) },
				{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 0) },
				{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 0) }
			},

			// O-Piece
			{
				{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
				{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
				{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
				{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) }
			},

			// S-Piece
			{
				{ new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) },
				{ new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
				{ new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) },
				{ new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) }
			},

			// T-Piece
			{
				{ new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1) },
				{ new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
				{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2) },
				{ new Point(1, 0), new Point(1, 1), new Point(2, 1), new Point(1, 2) }
			},

			// Z-Piece
			{
				{ new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
				{ new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) },
				{ new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
				{ new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) }
			}
	};

	private final Color[] tetraminoColors = {
			Color.cyan, Color.blue, Color.orange, Color.yellow, Color.green, Color.white, Color.red
	};

	int i=0;
	public static Tetris te;
	private Point pieceOrigin;
	private int currentPiece;
	private int rotation;
	private ArrayList nextPieces = new ArrayList();
	private static boolean play=false;
	private volatile boolean exit=true;
	static JFrame f = new JFrame("Tetris");
	private final int id=4;
	
	//frame in not closed
	private boolean fexit=false;
	
	private int score;
	private Color[][] well;

	// Creates a border around the well and initializes the dropping piece
	private void init() {
		play=true;
		fexit=exit=false;
		score=0;
		well = new Color[12][24];
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 23; j++) {
				if (i == 0 || i == 11 || j == 22) {
					well[i][j] = Color.GRAY;
				} else {
					well[i][j] = Color.BLACK;
				}
			}
		}
		newPiece();
	}

	// Put a new, random piece into the dropping position
	public void newPiece() {
		if(!fexit){
			if(!collidesAt(4,0,0)){
				pieceOrigin = new Point(4, 0);
				rotation = 0;
				if (nextPieces.isEmpty()){
					Collections.addAll(nextPieces, 0, 1, 2, 3, 4, 5, 6);
					Collections.shuffle(nextPieces);
				}
				currentPiece = (int)nextPieces.get(0);
				nextPieces.remove(0);
			}
			else{
				play=false;
				exit=true;
				newGameOverDialog();
			}
		}
	}

	private void newGameOverDialog()
	{
		int dialogResult = JOptionPane.showConfirmDialog(null, 
				"Game Over...... " +
						"\nStart a new game.",
						"GameOver",
						JOptionPane.YES_NO_OPTION);
		if (dialogResult == JOptionPane.YES_OPTION){
			new player(id,score);
			init(); // re-initialize the Tetris
		}
		else{
			play=false;
			exit=true;
			new player(id,score);
			f.dispose();
		}
	}

	// Collision test for the dropping piece
	private boolean collidesAt(int x, int y, int rotation) {
		for (Point p : Tetraminos[currentPiece][rotation]) {
			if (well[p.x + x][p.y + y] != Color.BLACK) {
				return true;
			}
		}
		return false;
	}

	// Rotate the piece clockwise or counterclockwise
	public void rotate(int i) {
		int newRotation = (rotation + i) % 4;
		if (newRotation < 0) {
			newRotation = 3;
		}
		if (!collidesAt(pieceOrigin.x, pieceOrigin.y, newRotation)) {
			rotation = newRotation;
		}
		repaint();
	}

	// Move the piece left or right
	public void move(int i) {
		if (!collidesAt(pieceOrigin.x + i, pieceOrigin.y, rotation)) {
			pieceOrigin.x += i;	
		}
		repaint();
	}

	// Drops the piece one line or fixes it to the well if it can't drop
	public void dropDown() {
		if (!collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
			pieceOrigin.y += 1;
		} else {
			fixToWell();
		}	
		repaint();
	}

	// Make the dropping piece part of the well, so it is available for
	// collision detection.
	public void fixToWell() {
		for (Point p : Tetraminos[currentPiece][rotation]) {
			well[pieceOrigin.x + p.x][pieceOrigin.y + p.y] = tetraminoColors[currentPiece];
		}
		clearRows();
		newPiece();
	}

	public void deleteRow(int row) {
		for (int j = row-1; j > 0; j--) {
			for (int i = 1; i < 11; i++) {
				well[i][j+1] = well[i][j];
			}
		}
	}

	// Clear completed rows from the field and award score according to
	// the number of simultaneously cleared rows.
	public void clearRows() {
		boolean gap;
		int numClears = 0;

		for (int j = 21; j > 0; j--) {
			gap = false;
			for (int i = 1; i < 11; i++) {
				if (well[i][j] == Color.BLACK) {
					gap = true;
					break;
				}
			}
			if (!gap) {
				deleteRow(j);
				j += 1;
				numClears += 1;
			}
		}

		switch (numClears) {
		case 1:
			score += 10;
			break;
		case 2:
			score += 20;
			break;
		case 3:
			score += 30;
			break;
		case 4:
			score += 40;
			break;
		}
	}

	// Draw the falling piece
	private void drawPiece(Graphics g) {		
		g.setColor(tetraminoColors[currentPiece]);
		for (Point p : Tetraminos[currentPiece][rotation]) {
			g.fillRect((p.x + pieceOrigin.x) * 26, 
					(p.y + pieceOrigin.y) * 26, 
					25, 25);
		}
	}

	@Override 
	public void paintComponent(Graphics g)
	{
		// Paint the well
		g.fillRect(0, 0, 26*12, 26*23);
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 23; j++) {
				g.setColor(well[i][j]);
				g.fillRect(26*i, 26*j, 25, 25);
			}
		}

		// Display the score
		g.setColor(Color.WHITE);
		g.drawString("" + score, 19*12, 25);

		// Draw the currently falling piece
		drawPiece(g);
	}
	
	void New()
	{
		//Thread.interrupted();
		//f.dispose();
		//f.setVisible(false);		
		new Tetris_Menu();
	}

	Tetris(){
		
		JMenuBar menubar;
		JMenu fileMenu;
		JMenuItem NewGameMenu,exitMenu;
		
		
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setLocationRelativeTo(null);
		f.setSize(12*26+10, 26*23+25);
		f.setVisible(true);
		f.setResizable(false);
		addCloseWindowListener();

		init();
		
		menubar = new JMenuBar();
		   
	    fileMenu = new JMenu("Game");
	    fileMenu.setMnemonic(KeyEvent.VK_F);
	    

	    NewGameMenu = new JMenuItem("NewGame");
	    NewGameMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
	            ActionEvent.CTRL_MASK));
	    
	    NewGameMenu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			//System.out.println("Here");
			new player(id,score);
			init();
			}
		});
	    
	    exitMenu = new JMenuItem("Exit");
	    exitMenu.setToolTipText("Exit application");
	    exitMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
	            ActionEvent.CTRL_MASK));
	    exitMenu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
						f.setVisible(false);
						f.dispose();
						New();
			}
		});
	    
	    
	    //fileMenu.add(newMenu);
	    fileMenu.add(NewGameMenu);
	    fileMenu.add(exitMenu);

	   
	    menubar.add(fileMenu);
	    f.setJMenuBar(menubar);
	    setVisible(true);
		
		f.add(this);

		// Keyboard controls
		f.addKeyListener(new KeyListener(){
			public void keyReleased(KeyEvent e) {}

			public void keyTyped(KeyEvent e) {}

			public void keyPressed(KeyEvent e) {
				if(play){
					switch (e.getKeyCode()) {
					case KeyEvent.VK_UP:
						rotate(-1);
						break;
					case KeyEvent.VK_DOWN:
						dropDown();
						break;
					case KeyEvent.VK_LEFT:
						move(-1);
						break;
					case KeyEvent.VK_RIGHT:
						move(+1);
						break;
					case KeyEvent.VK_SPACE:
						break;
					} 
				}
			}
		});

		///Thread th=new Thread();
		//run();

		// Make the falling piece drop every second
		new Thread() {
			@Override public void run() {
				while (!exit){
					try {
						if(!fexit){//while frame is not closed execute commands
							Thread.sleep(1000);
							dropDown();
						}else{ //once exited
							f.dispose();
							interrupt();
						}
					} catch ( InterruptedException e ) {}
				}
			}
		}.start();
	}
	
	public static void main(String[] args) {
		te = new Tetris();
	}
	

	private void addCloseWindowListener()
	{
		// NOTE: Must be DO_NOTHING_ON_CLOSE for prompt to function correctly
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		f.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent we)
			{			
				
					//if frame is not closed show message
					int prompt = JOptionPane.showConfirmDialog(null,
							"Are you sure you want to quit?",
							"Quit?", 
							JOptionPane.YES_NO_OPTION);

					if (prompt == JOptionPane.YES_OPTION){
						//newGameOverDialog();
						//play=false;
						//exit=true;
						//init();
						f.dispose();
						new player(id,score);

					 
					}
					
					
			}
		});
	}
}
