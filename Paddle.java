import java.awt.Color;

import acm.graphics.GRect;

/**
 *  The paddle object which is used in the Breakout game to bounce the ball back towards the
 *  brick rows.	
 *  Simply creates a GRect object with the desired attributes.
 *  Need to pass height, width and color of paddle.
 */
public class Paddle {
	
	public Paddle(int paddleWidth, int paddleHeight) {
		paddle = new GRect(paddleWidth, paddleHeight);
	}
	
	public Paddle(int paddleWidth, int paddleHeight, Color colorPaddle) {
		paddle = new GRect(paddleWidth, paddleHeight);
		paddle.setFilled(true);
		paddle.setFillColor(colorPaddle);
	}
	
	public void setPaddleColor(Color paddleColor) {
		paddle.setFilled(true);
		paddle.setFillColor(paddleColor);
	}
	
/**	INSTANCE VARIABLES	*/
	
/**	The paddle object created by class	*/
	private GRect paddle;
}
