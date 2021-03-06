/**
 * File: Breakout.java

 * This file implements the breakout game.
 * For more info, check here:
 * https://en.wikipedia.org/wiki/Breakout_%28video_game%29
 
 
 *	NOTES:
 *	The game is mostly complete, there are some enhancements that can be added:
 *	1. A Label and timer to signal to the user the starting and ending of each game
 *	2. Adding sounds when the ball collides with any object
 *	3. Changing the color of the ball, if a row of bricks is over
 *	4. Different velocities of the ball on collision with the paddle in different directions
	
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
	
/**	The length of the side of the square in which the ball object is contained */
	private static final int BALL_WIDTH = BALL_RADIUS * 2;
	
/**	Color of the ball */
	private static final Color BALL_COLOR = Color.BLACK;
	
/**	The initial value of the velocity of the ball in the y direction */
	private static final double BALL_Y_VELOCITY = 5.0;
	
/**	The minimum and max range of the x velocities possible, 
 * 	on some collisions a random x velocity is generated which uses this range.  
 */
	private static final double BALL_X_LOWER_VELOCITY = 2.0;
	private static final double BALL_X_UPPER_VELOCITY = 5.0;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;
	
/**	The delay variable; after these many milliseconds, the ball is moved.
 * 	The logic here is to mimic 24fps so that the ball moves seamlessly
 */
	private static final int DELAY = 42;

/** Runs the Breakout program. */
	public void run() {
		setupEnviron();
		/* Need to call this method to add mouse listener events */
		addMouseListeners();
		/* Once mouse listener has been added, game can start */
		startGame();
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
		return randomGen.nextDouble(BALL_X_LOWER_VELOCITY, BALL_X_UPPER_VELOCITY);
	}

/**	Inverts the x velocity, setting it to a random velocity
 * 	Changes the value of the instance variable ballvx
 */
	private void invertXVelocity() {
		double newXVel = -1 * Math.signum(ballvx) * getRandomXVelocity();
		ballvx = newXVel;
	}
	
/**	Inverts the y velocity, simply multiplying it by -1
* 	Changes the value of the instance variable ballvy
*/
	private void invertYVelocity() {
		ballvy = -ballvy;
	}

/**	Moves the ball around on the screen, basically the main method in the playing of the game.
 * 	Checks for collision of the ball, with container walls of the game, with the paddle and the bricks
 * 	A check is also contained to see if there are any bricks left, if not, the game is over
 */
	private void bounceAroundBall() {
		while (true) {
			moveBall();
			handleBallCollisions();
			if (checkIfTurnOver()) {
				break;
			}
			pause(DELAY);
		}
	}
	
/**	Move the ball
 * 	Update the position of the ball, using the move method 
 * 	ballvx and ballvy variables	are used to update the location, these values are the delta change in position
 */
	private void moveBall() {
		ball.move(ballvx, ballvy);
		ballTopLeftX = ball.getX();
		ballTopLeftY = ball.getY();
	}
	
/**	Handle the changes which happen in the game as the ball collides with objects.
 * 	The following collisions are handled here:
 * 	 with left wall - invert x velocity, choose a random value for x velocity
 *   with right wall - invert x velocity, choose a random value for x velocity
 *   with upper wall - invert y velocity
 *   with paddle - invert y velocity
 *   with bricks - remove that brick, reduce value of numBricksRemInTurn and invert y velocity
 *   Lower wall collision NOT handled, that is handled in checkIfTurnOver method
 */
	private void handleBallCollisions() {
		/* Collision with left wall, inverting velocity only if it is in -ve x direction */
		if ((ballTopLeftX <= 0) && !checkXVelocityTowardsRight()) {
			invertXVelocity();
			return;
		}
		/* Collision with right wall, inverting velocity only if it is in +ve x direction  */
		else if (((ballTopLeftX + BALL_WIDTH) >= WIDTH) && checkXVelocityTowardsRight()) {
			invertXVelocity();
			return;
		}
		/* Collision with upper wall, inverting velocity only if it is in -ve y direction (+ve y is downwards) */
		else if ((ballTopLeftY <= 0) && checkYVelocityUpwards()) {
			invertYVelocity();
			return;
		}

		/* Getting the object colliding with ball */
		GObject collidingObject = getCollidingObject();
		
		/* Collision with paddle */ 
		if ((collidingObject != null) && (collidingObject == paddle)) {
			/* Only if collision with ball is with top of paddle, inverting y velocity,
			 * else invert x velocity */
			if (checkCollisionWithTopOfPaddle()) {
				/* Check if velocity of ball is in +ve y direction, only then inverting its velocity */
				if (!checkYVelocityUpwards()) {
					invertYVelocity();
				}
			}
			else {
				invertXVelocity();
			}
			
		}
		
		/* Checking if colliding object is a brick */
		if ((collidingObject != null) && (checkGObjectIsBrick(collidingObject))) {
			/* No need to check if velocity is already inverted in this case, as brick is removed immediately
			 * and there is no chance of a 're-collision' happening */
			/* Inverting the balls velocity */
			invertYVelocity();
			/* Removing brick from screen, 
			 * and reducing the number of bricks left (instance variable numBricksRemInTurn */
			remove(collidingObject);
			numBricksRemInTurn--;
		}
	}
	
/** Check if the velocity of ball is in +ve x-direction, i.e. towards right
 * 	Using the ballvx and current ball x location (stored in ballTopLeftX) to check logic
 * @return True if velocity is to right
 */
	private boolean checkXVelocityTowardsRight() {
		if ((ballTopLeftX + ballvx) > ballTopLeftX) {
			return true;
		}
		return false;
	}
	
/**	Check if the velocity of the ball is in upwards direction, this is -ve y direction
 * 	Using ballvy and current ball y location (ballTopLeftY) to check logic
 * @return True if y velocity is upwards, or -ve y
 */
	private boolean checkYVelocityUpwards() {
		if ((ballTopLeftY + ballvy) < ballTopLeftY) {
			return true;
		}
		return false;
	}
	
/**	RECHECK METHOD	
 * Check if the ball collides with top surface of the paddle
 * 	The ball can be imagined to be contained in a square
 * 	If x,y is the upper left corner of the square (stored in instance variables ballTopLeftX and ballTopLeftY)
 * 	The lower 2 corners are given by (x, y+ball_width) and (x+ball_width, y+ball_width)
 * 	Checking if x and x+ball_width are contained within x co-ordinates of the top left and top right corner of the paddle
 * 	Checking if (y+ball_width) is greater than or equal to the y co-ordinate of the top of the paddle  
 * @return true if the collision is with top surface, false otherwise
 */
	private boolean checkCollisionWithTopOfPaddle() {
		double paddleTopLeftX = paddle.getX();
		double paddleTopRightX = paddleTopLeftX + PADDLE_WIDTH;
		double paddleTopLeftY = paddle.getY();
		
		/* vars to check if the ball is within the x and y boundaries of the top of paddle */
		boolean withinX = false, withinY = false;
		
		/* Checking if x co-ordinates are within bounds */
		if (((ballTopLeftX >= paddleTopLeftX) && (ballTopLeftX <= paddleTopRightX)) || 
				(((ballTopLeftX + BALL_WIDTH) >= paddleTopLeftX) && ((ballTopLeftX + BALL_WIDTH) <= paddleTopRightX))) {
			withinX = true;
		}
		/* checking if y co-ordinates are within bounds*/
		if ((ballTopLeftY + BALL_WIDTH) >= paddleTopLeftY) {
			withinY = true;
		}
		if (withinX && withinY) {
			return true;
		}
		return false;
	}
	
/**	Uses the ball co-ordinates to get and return the colliding object
 * 	Since the ball is a GOval, which can be imagined as inscribed in a square, 
 * 	the side of the square is equal to twice the length of the ball radius
 * 	To find colliding objects, the 4 corners of the square are checked
 * @return The colliding object or null if no object is found
 */
	private GObject getCollidingObject() {
		GObject collidingObject;

		/* Getting object at top left corner of square */
		collidingObject = getElementAt(ballTopLeftX, ballTopLeftY);
		/* Since no object found yet, finding if there is an object at top right corner of square */
		if (collidingObject == null) {
			collidingObject = getElementAt(ballTopLeftX + BALL_WIDTH, ballTopLeftY);
		}
		/* Since no object found yet, finding if there is an object at bottom left corner of square */
		if (collidingObject == null) {
			collidingObject = getElementAt(ballTopLeftX, ballTopLeftY + BALL_WIDTH);
		}
		/* Since no object found yet, finding if there is an object at bottom right corner of square */
		if (collidingObject == null) {
			collidingObject = getElementAt(ballTopLeftX + BALL_WIDTH, ballTopLeftY + BALL_WIDTH);
		}
		return collidingObject;
	}
	
/**	Checks the game to see if a turn is over. 
 * 	For a turn to be over:
 * 		1. Ball collides with lower wall
 * 		2. All bricks are finished	
 * @return	true if turn is over, false otherwise.
 */
	private boolean checkIfTurnOver() {
		if (numBricksRemInTurn == 0) {
			return true;
		}
		/* The side length of the square in which the ball is contained
		 * Checking if the square has a collision, the ball will also have a collision then */
		double ballLowerYPosition = ballTopLeftY + BALL_WIDTH;
		if (ballLowerYPosition >= HEIGHT) {
			return true;
		}
		return false;
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
		paddleLocation.y = HEIGHT - PADDLE_Y_OFFSET - PADDLE_WIDTH;
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
	
	
	
/**	METHODS RELATED TO BRICKS */	
	
/**	Given a GObject, check if it is the brick contained in the top row
 * @param obj The object to check conditions for
 * @return returns true if the object is a brick, false otherwise
 */
	private boolean checkGObjectIsBrick(GObject obj) {
		/* Brick is Visible */
		if (!(obj.isVisible())) {
			return false;
		}
		/* Object height and width should match that of the brick height and width */
		else if (obj.getWidth() != BRICK_WIDTH) {
			return false;
		}
		else if (obj.getHeight() != BRICK_HEIGHT) {
			return false;
		}
		return true;
	}
	
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
		 * If a row has n bricks, it will have n-1 space separators 
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
	
/**	The ball which bounces around and causes the game to progess */
	private GOval ball;
	
/** The velocity of the ball is kept track using these variables */
	private double ballvx, ballvy;
	
/**	The co-ordinates of the top left corner of the square the ball is contained by */
	private double ballTopLeftX, ballTopLeftY;
	
}
