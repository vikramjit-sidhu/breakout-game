/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
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

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;

/* Method: run() */
/** Runs the Breakout program. */
	public void run() {
		setupEnviron();
	}
	
	private void setupEnviron() {
		createGameStartLabel();
		setupBricks();		
	}
	
	
/**	The label which flashes in the beginning of the game warning the player that the game is about to start
 */
	private void createGameStartLabel() {
		
	}
	
	
/**	Creates the row of bricks near top of window
 * 	the rows are coloured in groups of 2.
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
 * 	
 * @return
 */
	private int brickRowStartingLocation() {
		/* Total width of a brick row */
		int brickRowWidth = BRICK_WIDTH * NBRICKS_PER_ROW;
		/* The space remaining, this has to be divided between the beginning and ending of a row  */
		int spaceRemaining = WIDTH - brickRowWidth;
		/* spaceRemaining is the amount of x distance that has to be divided between the beginning and ending of rows
		 * so the x offset of the row is half of this distance	*/
		return (spaceRemaining / 2);
	}
	
	
	private void createBrickRow(int x, int y, Color colorOfRow) {
		for (int i=0; i<NBRICKS_PER_ROW; i++) {
			/* creating a rectangle, adding it to the screen	*/
			GRect rectangle = createRectangle(x, y, colorOfRow);
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
	private GRect createRectangle(int x, int y, Color colorOfRect) {
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
	private RandomGenerator randomGen = new RandomGenerator();
	
/**	The GLabel object which flashes to the user when it is time for the game to start
	
}
