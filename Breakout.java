/**
 * File: Breakout.java

 * This file implements the breakout game.
 * For more info, check here:
 * https://en.wikipedia.org/wiki/Breakout_%28video_game%29
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;
	
/**	Color of paddle */	
	private static final Color PADDLE_COLOR = Color.BLACK;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

/** Separation between bricks */
	private static final int BRICK_SEP = 4;

/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;
	
/**	Color of the ball */
	private static final Color BALL_COLOR = Color.BLACK;
	
/**	The initial value of the velocity of the ball in the y direction */
	private static final double BALL_Y_VELOCITY = 3.0;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;
	
/**	The delay variable; after these many milliseconds, the ball is moved.
 * 	
 */
	private static final int DELAY = 42;

/** Runs the Breakout program. */
	public void run() {
		setupEnviron();
		startGame();
		
		/* Need to call this method to add mouse listener events */
		addMouseListeners();
	}
	
/**	This method is called each time the mouse moves
 * 	On movement of the mouse, the paddle location has to be updated, a method is called to update that
 * 	The mouse location has to be updated in the instance variable, which is used the next time the mouse moves 
 */
	public void mouseMoved(MouseEvent e) {
		setupPaddleListener(e);
		
		//Updating the past mouse location, this is used later to calculate the change in position
		mouseLocation = e.getPoint();
	}
	
/**	Sets up the environment for the breakout game
 * 	The bricks on the top row are created, which are used to play the game
 * 	Creates the paddle off which the ball will bounce
 * 	The ball is created at its starting location
 */
	private void setupEnviron() {
		setupBricks();
		setupPaddle();
		setInitialMouseLocation();
		setBallAtInitialLocation();
	}
	
/**	Clears the GraphicsProgram container of all elements.
 * 	This method is used at the end of a turn, where there may be some bricks left over, 
 * 	the ball may be in a random location 
 *  These elements have to be removed to prepare for the next turn.	
 */
	private void clearEnviron() {
		removeAll();
	}
	
/**	Start the game. Consists of initializing the velocity of the ball and starting its motion
 * 	Running a loop NTURNS number of times, the whole turn of a player is captured inside this loop
 */
	private void startGame() {
		for (int i=0; i < NTURNS; i++) {
			initializeInitialVelocityOfBall();
			/* This method will finish when a turn is up, or bricks are over */
			bounceAroundBall();
			/* At end of a turn, remove all elements from screen */
			clearEnviron();
			/* Reset the environment for the next turn, or if turns are over just display the environment
			 * {to show the user what s/he are missing >:) } */
			setupEnviron();
		}
	}
	
	
/**	Setting the initial mouse location to be the same as the x, y co-ordinates of the 
 * 	paddle object.
 */
	private void setInitialMouseLocation() {
		mouseLocation = getPaddleInitialPoint();
	}
	

	
/**	METHODS RELATED TO BALL MOTION */
	
/**	Sets up the ball at the center of the screen.
 * 	Creates a GOval object, with the BALL_RADIUS as a parameter for the width and height of rectangle
 * 	Centers the ball in the middle of the screen, slight adjustments have to be made as x,y
 * 	co-ordinates are the top left corner of the rectangle and not the center of ball	
 */
	private void setBallAtInitialLocation() {
		double ballx = (WIDTH / 2) - (BALL_RADIUS / 2);
		double bally = (HEIGHT / 2) - (BALL_RADIUS / 2);
		ball = new GOval(ballx, bally, BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		ball.setFillColor(BALL_COLOR);
		add(ball);
	}
	
/**	Sets the initial velocity of the ball, at the beginning of the game.
 * 	Sets the ballvy to a constant, this velocity is initially always positive (in the downwards direction)
 * 	randomly chooses the ballvx value (a method is called for it), 
 * 	it can be positive or negative, which is also randomly chosen 
 */
	private void initializeInitialVelocityOfBall() {
		ballvy = BALL_Y_VELOCITY;
		ballvx = getRandomXVelocity();
		/* Gets the next boolean value (true or false) with probability 0.5, it is possible to set a
		 * biased coin with this variation of the nextBoolean method, 
		 * used here to randomly set the x velocity direction */
		if (randomGen.nextBoolean(0.5)) {
			ballvx = -ballvx;
		}
	}

/**	Generate a random velocity (data type double) between 1.0 and 3.0. 
 * 	The randomGen instance variable is used to get the random double value
 *  @return a double value between 1 and 3
 */
	private double getRandomXVelocity() {
		return randomGen.nextDouble(1.0, 3.0);
	}

/**	Moves the ball around on the screen, basically the main method in the playing of the game.
 * 	Checks for collision of the ball, with container walls of the game, with the paddle and the bricks
 * 	A check is also contained to see if there are any bricks left, if not, the game is over
 */
	private void bounceAroundBall() {
		while (true) {
			moveBall();
			
			pause(DELAY);
		}
	}
	
	
	
/**	METHODS RELATED TO PADDLE */
	
/**
 * 	Create a paddle and add it to the window
 * 	Mouse move listener has to be setup also for the paddle
 */
	private void setupPaddle() {
		addPaddle();
	}
	
/**	
 * 	Creates a paddle (GRect object) and adds it to the screen at the required co-ordinates
 *  Adds color to the paddle, which is the defined constant
 */
	private void addPaddle() {
		//Getting paddle start point
		Point paddleStartPoint = getPaddleInitialPoint();
		paddle = new GRect((int) paddleStartPoint.getX(), (int) paddleStartPoint.getY(), PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setFillColor(PADDLE_COLOR);
		add(paddle);
	}
	
/**	Returns the initial starting point of the paddle
 * 	The paddle is a GRect object, the point returned is the x,y co-ordinates of the top left 
 * 	corner of that rectangle.
 * @return	A Point object containing the x,y co-ordinates
 */
	private Point getPaddleInitialPoint() {
		Point paddleLocation = new Point();
		/* The x location of the paddle, i.e. the GRect object which is at the x, y location of the screen. 
		 * So it will be at centre of screen, minus half the 
		 * paddle width. */
		paddleLocation.x = (WIDTH / 2) - (PADDLE_WIDTH / 2);
		/* The y offset of the paddle is given from the bottom of the screen, 
		 * hence calculating as such */
		paddleLocation.y = HEIGHT - PADDLE_Y_OFFSET;
		return paddleLocation;
	}
	
/**	The paddle has to move with the mouse, right upto the edge of the boundary
 * 	The mouse moved event calls this method and passes it the mouse event object.
 * 	Using the mouse event, finding out its x location and hence calculating the paddle location from it
 */
	private void setupPaddleListener(MouseEvent e) {
		/*	The distance the paddle has to move, will be positive to move towards right wall and
		 * 	if negative it will move towards left wall */
		double distanceToMove = e.getX() - mouseLocation.getX();
		/* Checking if there will be a collision with the game walls if the paddle moves the required distance */ 
		if (!checkPaddleCollision(distanceToMove)) {
			paddle.move(distanceToMove, 0);
		}
	}
	
/**	Given the current paddle location, calculating that if it moves to the new position,
 * 	whether it will collide with the game walls.
 *  The paddle x location is the corner of the GRect object, for collision with left wall, direct 
 *  comparison will be done, for collision with right wall, the width of the paddle 
 *  will also have to be considered.
 * @param distanceToMove This will be negative for paddle to move in left direction 
 * and positive if it has to move towards right
 * @return Returns true if there is a paddle collision with either of the walls
 */
	private boolean checkPaddleCollision(double distanceToMove) {
		//Checking if there is a collision with the left wall
		if ((distanceToMove < 0) && ((paddle.getX() + distanceToMove) >= 0)) {
			return false;
		}
		//Checking for collision with right wall
		else if ((distanceToMove > 0) && ((paddle.getX() + PADDLE_WIDTH + distanceToMove) <= WIDTH)) {
			return false;
		}
		return true;
	}
	
	
	
/**	METHODS RELATED TO INITIALIZING BRICKS */	
	
/**	
 * 	Creates the row of bricks near top of window
 * 	the rows are coloured in groups of 2.
 * 	Sets up the instance variable which keeps track of the number of bricks remaining in a turn
 */
	private void setupBricks() {
		/*This is the y location of the first row from the top	*/
		int brickRowXLocation = brickRowStartingLocation();
		int brickRowYLocation = BRICK_Y_OFFSET;
		for (int i=1; i <= NBRICK_ROWS; i++) {
			// Sends the brick row number and gets the color of that row
			Color colorOfRow = getBrickRowColor(i);
			//Creates a row at the specified y location with the given color
			createBrickRow(brickRowXLocation, brickRowYLocation, colorOfRow);
			/* The next row must be created with the following brick separation	*/
			brickRowYLocation += (BRICK_HEIGHT + BRICK_SEP);
		}
	}
	
/**
 * This method takes in the brick row number as a parameter and returns the color associated with that row	
 * @param brickRowNumber contains the row number
 * The row color values are hard coded:
 *  Rows 1,2-Red; Rows 3,4-orange; Rows 5,6-yellow; Rows 7,8-Green; Rows 9,10-cyan
 *  If there are any more rows than 10, a random color is returned
 */
	private Color getBrickRowColor(int brickRowNumber) {
		//Contains the color of the row which is to be returned
		Color colorOfRow;
		if ((brickRowNumber == 1) || (brickRowNumber == 2))
			colorOfRow = Color.RED;
		else if ((brickRowNumber == 3) || (brickRowNumber == 4))
			colorOfRow = Color.ORANGE;
		else if ((brickRowNumber == 5) || (brickRowNumber == 6))
			colorOfRow = Color.YELLOW;
		else if ((brickRowNumber == 7) || (brickRowNumber == 8))
			colorOfRow = Color.GREEN;
		else if ((brickRowNumber == 9) || (brickRowNumber == 10))
			colorOfRow = Color.CYAN;
		else
			colorOfRow = randomGen.nextColor();
		return colorOfRow;
	}
	
/**	
 * Calculates the starting location of the brick rows
 * @return
 */
	private int brickRowStartingLocation() {
		/* The starting offset of the first brick, it will be considered to be 0 */
		int startLocation = 0;
		/* Total width of a brick row, also considering the separation between bricks 
		 * If a row has n bricks, it will have n+1 space separators 
		 * (considering separation for starting and ending bricks too)*/
		int brickRowWidth = (BRICK_WIDTH * NBRICKS_PER_ROW) + ((NBRICKS_PER_ROW - 1) * BRICK_SEP);
		/* The space remaining, this has to be divided between the beginning and ending of a row
		* spaceRemaining is the amount of x distance that has to be divided between the beginning and ending of rows
		 * so the x offset of the row is half of this distance	*/
		int spaceRemaining = WIDTH - brickRowWidth;
		if (spaceRemaining > 0) {
			startLocation += (spaceRemaining / 2);
		}
		return startLocation;
	}
	
	
/**	Creates a row of bricks starting at the x,y location
 * 	The x location is updated with the width of the brick and the separation between bricks.	
 * @param x	The x location of top left corner of GRect object which is brick
 * @param y	The y location of top left corner of GRect object which is brick
 * @param colorOfRow The color of the brick, it is the same for the row
 */
	private void createBrickRow(int x, int y, Color colorOfRow) {
		for (int i=0; i<NBRICKS_PER_ROW; i++) {
			/* creating a rectangle, adding it to the screen	*/
			GRect rectangle = createBrick(x, y, colorOfRow);
			add(rectangle);
			/* the x location of the next brick 	*/
			x += (BRICK_WIDTH + BRICK_SEP);
		}
	}
	
/**
 * Creating a rectangle GRect object
 * The width and height of the rectangle are specified by the constants defined	
 * @param x @param y the x, y location of the top right corner of the rectangle
 * @param colorOfRect the color to fill in the rectangle
 */
	private GRect createBrick(int x, int y, Color colorOfRect) {
		GRect rectangle = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
		rectangle.setFilled(true);
		rectangle.setFillColor(colorOfRect);
		return rectangle;
	}

	
/**
 * 	INSTANCE VARIABLES
 */
	
/**	Random generator class instance
 * This is used to generate random colours and random numbers as the case may be	*/
	private RandomGenerator randomGen = RandomGenerator.getInstance();
	
/**	The paddle, which is used to bounce the ball */
	private GRect paddle;
	
/**	Current mouse location. This is saved and used later, 
 * when the mouse moves to calculate change in position	
 */
	private Point mouseLocation;
	
/**	Keeps track of the number of bricks remaining in the game, for a turn
 * 	During the course of the game, as the ball collides with a brick, that brick is removed.
 * 	Once all the bricks are removed, the game ends, this variable helps keep track of that
 */
	private int numBricksRemInTurn = NBRICKS_PER_ROW * NBRICK_ROWS;
	
/** The velocity of the ball is kept track using these variables
 */
	private double ballvx, ballvy;
	
/**	The ball which bounces around and causes the game to progess */
	private GOval ball;
}
