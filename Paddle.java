import java.awt.Color;

import acm.graphics.GRect;
import acm.program.GraphicsProgram;

/**
 *  The paddle object which is used in the Breakout game to bounce the ball back towards the
 *  brick rows.	
 *  Simply creates a GRect object with the desired attributes.
 *  Need to pass height, width and color of paddle.
 */
public class Paddle extends GraphicsProgram {
	
	public Paddle(int paddleWidth, int paddleHeight, Color colorPaddle) {
		GRect paddle = new GRect(paddleWidth, paddleHeight);
		paddle.setFilled(true);
	}
}
